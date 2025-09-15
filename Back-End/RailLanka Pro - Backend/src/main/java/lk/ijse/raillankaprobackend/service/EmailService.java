package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.CustomerSupportDto;
import lk.ijse.raillankaprobackend.dto.StaffDto;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface EmailService {

    void sendStationMasterCredentials(String template , StaffDto staffDto , String id);

    void sendCounterCredentials(String template , CounterDto counterDto , String id);

    void sendOtpCode(String toEmail , String otp);

    void sendCustomerSupportEmail(CustomerSupportDto customerSupportDto);

}
