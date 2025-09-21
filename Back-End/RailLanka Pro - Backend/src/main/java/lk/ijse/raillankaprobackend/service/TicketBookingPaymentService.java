package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.entity.TicketBookingPayment;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface TicketBookingPaymentService {
    String generatePaymentId();

    TicketBookingPayment saveTicketPayment(TicketBookingPayment ticketBookingPayment);
}
