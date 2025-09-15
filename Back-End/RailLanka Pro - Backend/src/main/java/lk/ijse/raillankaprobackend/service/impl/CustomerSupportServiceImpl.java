package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.CustomerSupportDto;
import lk.ijse.raillankaprobackend.service.CustomerSupportService;
import lk.ijse.raillankaprobackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class CustomerSupportServiceImpl implements CustomerSupportService {
    private final EmailService emailService;
    @Override
    public String sendCustomerSupportEmail(CustomerSupportDto customerSupportDto) {
            emailService.sendCustomerSupportEmail(customerSupportDto);

        return "Mail sent successfully";
    }
}
