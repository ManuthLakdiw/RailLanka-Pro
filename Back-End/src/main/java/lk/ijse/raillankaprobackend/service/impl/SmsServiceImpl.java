package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.BookingDto;
import lk.ijse.raillankaprobackend.dto.PayeeInfoDto;
import lk.ijse.raillankaprobackend.service.BookingService;
import lk.ijse.raillankaprobackend.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.*;
import java.net.URI;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final BookingService bookingService;

    @Value("${notifylk.user_id}")
    private String notifyUserId;

    @Value("${notifylk.api_key}")
    private String notifyApiKey;

    @Value("${notifylk.sender_id}")
    private String notifySenderId;

    private static final String NOTIFY_API_URL = "https://app.notify.lk/api/v1/send";



    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    private static final Color PRIMARY_BLUE = new Color(30, 64, 175);
    private static final Color TEXT_DARK = new Color(31, 41, 55);


    @Override
    public String sendTicketConfirmation(String bookingId, String passengerPhoneNumber) {
        BookingDto bookingDto = bookingService.getBookingDetailsByBookingId(bookingId);
        if (!isValidPhoneNumber(formatSriLankaNumber(passengerPhoneNumber))) {
            return null;
        }

        try {
            String smsBody = buildTicketConfirmationSms(bookingDto);
            sendSms(formatSriLankaNumber(passengerPhoneNumber), smsBody);
        } catch (Exception e) {
            logger.error("Failed to Send {}: {}", formatSriLankaNumber(passengerPhoneNumber), e.getMessage());
        }
        return "SMS has been send successfully.";

    }

    @Override
    public void sendBookingReminder(String bookingId, String passengerPhoneNumber) {
        BookingDto booking = bookingService.getBookingDetailsByBookingId(bookingId);
        if (!isValidPhoneNumber(formatSriLankaNumber(passengerPhoneNumber))) {
            return;
        }

        try {
            String smsBody = buildBookingReminderSms(booking);
            sendSms(passengerPhoneNumber, smsBody);
            logger.info("Booking reminder SMS sent to: {}", passengerPhoneNumber);
        } catch (Exception e) {
            logger.error("Failed to send reminder SMS to {}: {}", passengerPhoneNumber, e.getMessage());
        }
    }

    @Override
    public void sendCancellationNotice(String bookingId, String passengerPhoneNumber) {
        BookingDto booking = bookingService.getBookingDetailsByBookingId(bookingId);
        if (!isValidPhoneNumber(formatSriLankaNumber(passengerPhoneNumber))) {
            return;
        }

        try {
            String smsBody = buildCancellationSms(booking);
            sendSms(passengerPhoneNumber, smsBody);
            logger.info("Cancellation notice SMS sent to: {}", formatSriLankaNumber(passengerPhoneNumber));
        } catch (Exception e) {
            logger.error("Failed to send cancellation SMS to {}: {}", formatSriLankaNumber(passengerPhoneNumber), e.getMessage());
        }
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return phoneNumber.matches("^\\+94[0-9]{9}$");
    }



    private String buildTicketConfirmationSms(BookingDto booking) {
        PayeeInfoDto payeeInfo = booking.getPayeeInfo();
        String passengerName = payeeInfo != null ? payeeInfo.getFirstName() : "Passenger";

        return "🚂 RailLanka Pro - Booking Confirmed\n\n" +
                "Dear " + passengerName + ",\n\n" +
                "✅ Your booking is confirmed!\n\n" +
                "Booking ID: " + booking.getBookingId() + "\n" +
                "Route: " + booking.getDepartureStation() + " to " + booking.getDestinationStation() + "\n" +
                "Train: " + booking.getTrainName() + "\n" +
                "Date: " + booking.getFormattedTravelDate() + "\n" +
                "Time: " + booking.getDepartureTime() + "\n" +
                "Class: " + booking.getTravelClass() + "\n" +
                "Passengers: " + booking.getAdultCount() + " Adult(s), " + booking.getChildCount() + " Child(ren)\n" +
                "Amount: " + booking.getFormattedTotalAmount() + "\n\n" +
                "Seats: " + (booking.getFormatedselectedSeat() != null ?
                booking.getFormatedselectedSeat() : "To be assigned at station") + "\n\n" +
                "📱 Download ticket: raillanka.lk/tickets/" + booking.getBookingId() + "\n" +
                "📞 Support: +94 11 234 5678\n\n" +
                "Please arrive 30 minutes before departure. Carry valid ID proof.\n\n" +
                "Thank you for choosing RailLanka Pro!";
    }

    private void sendSms(String toPhoneNumber, String body) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String formattedNumber = toPhoneNumber.startsWith("+") ? toPhoneNumber.substring(1) : toPhoneNumber;

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(NOTIFY_API_URL)
                    .queryParam("user_id", notifyUserId)
                    .queryParam("api_key", notifyApiKey)
                    .queryParam("sender_id", notifySenderId)
                    .queryParam("to", formattedNumber)
                    .queryParam("message", body)
                    .build()
                    .encode()
                    .toUri();

            String response = restTemplate.getForObject(uri, String.class);
            logger.debug("Notify.lk API Response {}", response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Send SMS " + e.getMessage(), e);
        }
    }


    private String buildBookingReminderSms(BookingDto booking) {
        PayeeInfoDto payeeInfo = booking.getPayeeInfo();
        String passengerName = payeeInfo != null ? payeeInfo.getFirstName() : "Passenger";

        return "🔔 RailLanka Pro - Journey Reminder\n\n" +
                "Dear " + passengerName + ",\n\n" +
                "This is a reminder for your upcoming journey:\n\n" +
                "Booking ID: " + booking.getBookingId() + "\n" +
                "Route: " + booking.getDepartureStation() + " to " + booking.getDestinationStation() + "\n" +
                "Date: " + booking.getFormattedTravelDate() + "\n" +
                "Time: " + booking.getDepartureTime() + "\n" +
                "Train: " + booking.getTrainName() + "\n\n" +
                "📍 Please arrive at " + booking.getDepartureStation() + " station 30 minutes before departure.\n" +
                "🎫 Don't forget to carry your ticket and valid ID proof.\n\n" +
                "Safe travels!\nRailLanka Pro";
    }

    public static String formatSriLankaNumber(String number) {
        if (number == null || number.isEmpty()) {
            throw new IllegalArgumentException("❌ Phone number cannot be null or empty");
        }

        number = number.replaceAll("[\\s-]", "");

        if (!number.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("❌ Invalid phone number format. Must be 10 digits starting with 0.");
        }

        return "+94" + number.substring(1);
    }

    private String buildCancellationSms(BookingDto booking) {
        PayeeInfoDto payeeInfo = booking.getPayeeInfo();
        String passengerName = payeeInfo != null ? payeeInfo.getFirstName() : "Passenger";

        return "❌ RailLanka Pro - Booking Cancelled\n\n" +
                "Dear " + passengerName + ",\n\n" +
                "Your booking has been cancelled:\n\n" +
                "Booking ID: " + booking.getBookingId() + "\n" +
                "Route: " + booking.getDepartureStation() + " to " + booking.getDestinationStation() + "\n" +
                "Date: " + booking.getFormattedTravelDate() + "\n" +
                "Train: " + booking.getTrainName() + "\n\n" +
                "Refund will be processed within 5-7 business days (if applicable).\n\n" +
                "We hope to serve you again soon!\n" +
                "RailLanka Pro";
    }
}
