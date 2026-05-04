package lk.ijse.raillankaprobackend.service;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface SmsService {
    String sendTicketConfirmation(String bookingId, String passengerPhoneNumber);
    void sendBookingReminder(String bookingId, String passengerPhoneNumber);
    void sendCancellationNotice(String bookingId, String passengerPhoneNumber);
    boolean isValidPhoneNumber(String phoneNumber);
}
