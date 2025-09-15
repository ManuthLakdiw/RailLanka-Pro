package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.CustomerSupportDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface CustomerSupportService {

    String sendCustomerSupportEmail(CustomerSupportDto customerSupportDto);

}
