package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.BookingDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface BookingService {

    String generateNewBookingId();

    String placeBooking(BookingDto bookingDto);

    BookingDto getBookingDetailsByBookingId(String bookingId);

}
