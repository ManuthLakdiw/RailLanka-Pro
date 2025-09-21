package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.BookingDto;
import lk.ijse.raillankaprobackend.dto.PayeeInfoDto;
import lk.ijse.raillankaprobackend.entity.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.*;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.repository.*;
import lk.ijse.raillankaprobackend.service.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.TreeMap;


/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final StationRepository stationRepository;
    private final ScheduleRepository scheduleRepository;
    private final TicketService ticketService;
    private final TicketBookingPaymentService ticketBookingPaymentService;
    private final PassengerRepository passengerRepository;
    private final PayeeInfoRepository payeeInfoRepository;


    @Override
    public String generateNewBookingId() {
        String lastId = bookingRepository.getLastBookingId().orElse("BKN00000-00001");
        String[] split = lastId.split("-");
        int prefixNumber = Integer.parseInt(split[0].substring(3));
        int suffixNumber = Integer.parseInt(split[1]);
        suffixNumber++;
        if (suffixNumber > 99999){
            suffixNumber = 1;
            prefixNumber++;
            if (prefixNumber > 99999){
                throw new IdGenerateLimitReachedException("All available Booking IDs have been used. Please contact the system administrator");
            }
        }
        return  String.format("BKN%05d-%05d", prefixNumber, suffixNumber);
    }

    @Transactional
    @Override
    public String placeBooking(BookingDto bookingDto) {

        Schedule schedule = scheduleRepository.findById(bookingDto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        Station departureStation = stationRepository.findByName(bookingDto.getDepartureStation())
                .orElseThrow(() -> new RuntimeException("Departure station not found"));

        Station destinationStation = stationRepository.findByName(bookingDto.getDestinationStation())
                .orElseThrow(() -> new RuntimeException("Destination station not found"));

        Passenger passenger = passengerRepository.findById(bookingDto.getPassengerId()).orElseThrow(
                () -> new RuntimeException("Passenger not found"));
        Booking booking = Booking.builder()
                .bookingId(generateNewBookingId())
                .travelDate(bookingDto.getTravelDate())
                .travelClass(TravelClass.valueOf(bookingDto.getTravelClass()))
                .bookedAt(LocalDateTime.now())
                .departureStation(departureStation)
                .destinationStation(destinationStation)
                .passenger(passenger)
                .schedule(schedule)
                .build();

        bookingRepository.save(booking);

        if (bookingDto.getSelectedSeat() != null && !bookingDto.getSelectedSeat().isEmpty()){
            List<FirstClassBookingSeat> bookedSeats = bookingDto.getSelectedSeat().stream()
                    .map(cs -> {
                        String rowLetterStr = cs.getSeat().substring(0, 1);
                        int seatNumber = Integer.parseInt(cs.getSeat().substring(1));

                        return FirstClassBookingSeat.builder()
                                .seatId(UUID.randomUUID().toString())
                                .rowLetter(RowLetter.valueOf(rowLetterStr))
                                .seatPosition(SeatPosition.values()[seatNumber-1])
                                .carriageNumber(CarriageNumber.valueOf(cs.getCarriage()))
                                .travelDate(bookingDto.getTravelDate())
                                .booking(booking)
                                .schedule(schedule)
                                .build();
                    })
                    .toList();

            booking.setFirstClassBookedSeats(bookedSeats);
        }

        bookingRepository.save(booking);


        Ticket ticket = Ticket.builder()
                .ticketId(ticketService.generateNewTicketId())
                .validPassengerCount(bookingDto.getAdultCount() + bookingDto.getChildCount())
                .adultCount(bookingDto.getAdultCount())
                .childCount(bookingDto.getChildCount())
                .expireAt(calcTicketExpiration(bookingDto.getTravelDate()))
                .ticketStatus(TicketStatus.ACTIVE)
                .booking(booking)
                .build();
        ticketService.saveTicket(ticket);
        booking.setTicket(ticket);

        PayeeInfo payeeInfo = PayeeInfo.builder()
                .firstName(bookingDto.getPayeeInfo().getFirstName())
                .lastName(bookingDto.getPayeeInfo().getLastName())
                .email(bookingDto.getPayeeInfo().getEmail())
                .phoneNumber(bookingDto.getPayeeInfo().getPhoneNumber())
                .identityNumber(bookingDto.getPayeeInfo().getNicOrPassport())
                .orderId(bookingDto.getPayeeInfo().getOrderId())
                .build();

        payeeInfoRepository.save(payeeInfo);


        TicketBookingPayment ticketBookingPayment = TicketBookingPayment.builder()
                .ticketBookingPaymentId(ticketBookingPaymentService.generatePaymentId())
                .paymentType("CARD")
                .amount(bookingDto.getTotalAmount())
                .ticket(ticket)
                .payeeInfo(payeeInfo)
                .build();

        ticketBookingPaymentService.saveTicketPayment(ticketBookingPayment);

        return booking.getBookingId();

    }

    @Override
    public BookingDto getBookingDetailsByBookingId(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));

        String formattedTravelClass;
        TravelClass travelClass = booking.getTravelClass();
        formattedTravelClass = switch (travelClass) {
            case FIRST -> "First Class";
            case SECOND -> "Second Class";
            case THIRD -> "Third Class";
        };

        String bookingName = formattedName(booking.getTicket().getTicketBookingPayment().getPayeeInfo().getFirstName()) +
                " " + formattedName(booking.getTicket().getTicketBookingPayment().getPayeeInfo().getLastName());
        String idCard = booking.getTicket().getTicketBookingPayment().getPayeeInfo().getIdentityNumber();

        String formattedSeats = "";

        if (formattedTravelClass.equals("First Class")){
            if (booking.getFirstClassBookedSeats() == null || booking.getFirstClassBookedSeats().isEmpty()){
                formattedSeats = "No seat booked";
            }else{
                formattedSeats = booking.getFirstClassBookedSeats().stream()
                        .collect(Collectors.groupingBy(
                                seat -> seat.getCarriageNumber().name(),
                                TreeMap::new,
                                Collectors.mapping(
                                        seat -> seat.getRowLetter().name() + convertSeatPosition(seat.getSeatPosition()), // B4,A3...
                                        Collectors.toList()
                                )
                        ))
                        .entrySet().stream()
                        .map(entry -> {
                            String crName = switch (entry.getKey()) {
                                case "ONE" -> "CRR1";
                                case "TWO" -> "CRR2";
                                default -> entry.getKey();
                            };
                            return crName + "-[" + String.join(",", entry.getValue()) + "]";
                        })
                        .collect(Collectors.joining(" | "));
            }
        }else {
            formattedSeats = "No online booking available for this Class!";
        }

        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Departure Station: " + booking.getDepartureStation());
        System.out.println("Destination Station: " + booking.getDestinationStation());
        System.out.println("Schedule: " + booking.getSchedule());
        if (booking.getSchedule() != null) {
            System.out.println("Train: " + booking.getSchedule().getTrain());
        }



        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .departureStation(booking.getDepartureStation().getName())
                .destinationStation(booking.getDestinationStation().getName())
                .trainName(booking.getSchedule().getTrain().getName())
                .departureTime(getDepartureTime(booking.getSchedule(), booking.getDepartureStation()))
                .arrivalTime(getArrivalTime(booking.getSchedule(), booking.getDestinationStation()))
                .formattedTravelDate(formatTravelDate(booking.getTravelDate()))
                .formattedTotalAmount(formatAmount(booking.getTicket().getTicketBookingPayment().getAmount()))
                .adultCount(booking.getTicket().getAdultCount())
                .childCount(booking.getTicket().getChildCount())
                .payeeInfo(PayeeInfoDto.builder()
                        .firstName(bookingName)
                        .nicOrPassport(idCard)
                        .build())
                .travelClass(formattedTravelClass)
                .formatedselectedSeat(formattedSeats)
                .build();


    }

    private  String getDepartureTime(Schedule schedule , Station departureStation){
        if (schedule.getMainDepartureStation().getStationId().equals(departureStation.getStationId())){
            return formatTime(schedule.getMainDepartureTime());
        }
        return schedule.getStops().stream()
                .filter(stop -> stop.getStation().getStationId()
                        .equals(departureStation.getStationId()))
                .findFirst()
                .map(stop -> formatTime(stop.getDepartureTime()) )
                .orElseThrow(() -> new RuntimeException("Station not found in schedule"));
    }

    private String formatTravelDate(LocalDate travelDate){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            return travelDate.format(formatter);

    }

    private String getArrivalTime(Schedule schedule , Station destinationStation){
        if (schedule.getMainArrivalStation().getStationId().equals(destinationStation.getStationId())) {
            return formatTime(schedule.getMainArrivalTime());
        }

        return schedule.getStops().stream()
                .filter(stop -> stop.getStation().getStationId()
                        .equals(destinationStation.getStationId()))
                .findFirst()
                .map(stop -> formatTime(stop.getArrivalTime()) )
                .orElseThrow(() -> new RuntimeException("Station not found in schedule"));
    }

    private String formatTime(LocalTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh.mm a");
        return time.format(formatter);
    }

    private String formatAmount(double amount){
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
        return formatter.format(amount);
    }

    private String formattedName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "N/A";
        }

        String trimmed = name.trim();
        if (trimmed.length() == 0) {
            return "N/A";
        }

        // Your existing logic here, but with safe checks
        return trimmed.substring(0, 1).toUpperCase() +
                (trimmed.length() > 1 ? trimmed.substring(1).toLowerCase() : "");
    }

    private String convertSeatPosition(SeatPosition seatPosition) {
        return switch (seatPosition) {
            case ONE -> "1";
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
        };
    }




    private LocalDate calcTicketExpiration(LocalDate travelDate){
        return travelDate.plusDays(1);
    }

}
