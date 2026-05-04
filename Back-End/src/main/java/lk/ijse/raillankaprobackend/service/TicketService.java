package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.BookingDto;
import lk.ijse.raillankaprobackend.entity.Ticket;

import java.io.ByteArrayOutputStream;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface TicketService {
    String generateNewTicketId();

    Ticket saveTicket(Ticket ticket);

}
