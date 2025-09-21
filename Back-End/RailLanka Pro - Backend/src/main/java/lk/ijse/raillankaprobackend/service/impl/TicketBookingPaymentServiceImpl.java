package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.entity.TicketBookingPayment;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.repository.TicketBookingPaymentRepository;
import lk.ijse.raillankaprobackend.service.TicketBookingPaymentService;
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
public class TicketBookingPaymentServiceImpl implements TicketBookingPaymentService {
    private final TicketBookingPaymentRepository ticketBookingPaymentRepository;
    @Override
    public String generatePaymentId() {
        String lastPaymentId = ticketBookingPaymentRepository.getLastPaymentId().orElse("PAY00000-00001");
        String[] split = lastPaymentId.split("-");
        int prefixNumber = Integer.parseInt(split[0].substring(3));
        int suffixNumber = Integer.parseInt(split[1]);
        suffixNumber++;
        if (suffixNumber > 99999){
            suffixNumber = 1;
            prefixNumber++;
            if (prefixNumber > 99999){
                throw new IdGenerateLimitReachedException("All available Payment IDs have been used. Please contact the system administrator");
            }
        }
        return  String.format("PAY%05d-%05d", prefixNumber, suffixNumber);
    }

    @Override
    public TicketBookingPayment saveTicketPayment(TicketBookingPayment ticketBookingPayment) {
        return ticketBookingPaymentRepository.save(ticketBookingPayment);
    }
}
