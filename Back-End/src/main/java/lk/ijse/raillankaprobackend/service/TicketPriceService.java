package lk.ijse.raillankaprobackend.service;

import lk.ijse.raillankaprobackend.dto.PriceCalcDto;
import lk.ijse.raillankaprobackend.dto.TrainScheduleInfoDto;
import lk.ijse.raillankaprobackend.entity.Dtypes.TicketIssueCategory;
import lk.ijse.raillankaprobackend.entity.Dtypes.TravelClass;
import lk.ijse.raillankaprobackend.entity.Schedule;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface TicketPriceService {

    double getBasePrice(TravelClass travelClass, TicketIssueCategory issueCategory);

    TrainScheduleInfoDto.AllCalculatedTicketPriceDto calculatePrice(Schedule schedule, PriceCalcDto priceCalcDto);

}
