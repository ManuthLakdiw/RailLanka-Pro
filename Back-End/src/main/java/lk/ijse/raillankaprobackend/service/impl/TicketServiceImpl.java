package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.entity.Ticket;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.repository.TicketRepository;
import lk.ijse.raillankaprobackend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@RequiredArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    @Override
    public String generateNewTicketId() {
        String lastTicketId = ticketRepository.getLastTicketId().orElse("TKT00000-00001");
        String[] split = lastTicketId.split("-");
        int prefixNumber = Integer.parseInt(split[0].substring(3));
        int suffixNumber = Integer.parseInt(split[1]);
        suffixNumber++;
        if (suffixNumber > 99999){
            suffixNumber = 1;
            prefixNumber++;
            if (prefixNumber > 99999){
                throw new IdGenerateLimitReachedException("All available Ticket IDs have been used. Please contact the system administrator");
            }
        }
        return  String.format("TKT%05d-%05d", prefixNumber, suffixNumber);
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
}
