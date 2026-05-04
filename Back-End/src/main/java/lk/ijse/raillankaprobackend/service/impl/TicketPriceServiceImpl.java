package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.PriceCalcDto;
import lk.ijse.raillankaprobackend.dto.TrainScheduleInfoDto;
import lk.ijse.raillankaprobackend.entity.Dtypes.TicketIssueCategory;
import lk.ijse.raillankaprobackend.entity.Dtypes.TravelClass;
import lk.ijse.raillankaprobackend.entity.Schedule;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.service.TicketPriceService;
import lk.ijse.raillankaprobackend.util.StationSequenceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class TicketPriceServiceImpl implements TicketPriceService {

    private final StationSequenceUtil stationSequenceUtil;



    @Override
    public double getBasePrice(TravelClass travelClass, TicketIssueCategory issueCategory) {
        if (!TicketIssueCategory.CHILD.equals(issueCategory)) {
            return switch (travelClass) {
                case FIRST ->  120.0;
                case SECOND -> 70.0;
                case THIRD -> 40.0;
            };
        }else {
            return switch (travelClass) {
                case FIRST ->  80.0;
                case SECOND ->  50.0;
                case THIRD ->  30.0;
            };
        }
    }

    @Override
    public TrainScheduleInfoDto.AllCalculatedTicketPriceDto calculatePrice(Schedule schedule, PriceCalcDto priceCalcDto) {
        System.out.println("from ticket price");
        System.out.println(priceCalcDto.getDeparture() + "-" +priceCalcDto.getDestination());
        System.out.println("from ticket price");
        List<Station> sequence = stationSequenceUtil.getStationSequence(
                schedule, priceCalcDto.getDeparture(), priceCalcDto.getDestination());
        int distance = sequence.size() - 1;

        double firstClass = (getBasePrice(TravelClass.FIRST, TicketIssueCategory.ADULT) * priceCalcDto.getAdultCount()
                + getBasePrice(TravelClass.FIRST, TicketIssueCategory.CHILD) * priceCalcDto.getChildCount()) * distance;

        double secondClass = (getBasePrice(TravelClass.SECOND, TicketIssueCategory.ADULT) * priceCalcDto.getAdultCount()
                + getBasePrice(TravelClass.SECOND, TicketIssueCategory.CHILD) * priceCalcDto.getChildCount()) * distance;

        double thirdClass = (getBasePrice(TravelClass.THIRD, TicketIssueCategory.ADULT) * priceCalcDto.getAdultCount()
                + getBasePrice(TravelClass.THIRD, TicketIssueCategory.CHILD) * priceCalcDto.getChildCount()) * distance;

        return new TrainScheduleInfoDto.AllCalculatedTicketPriceDto(firstClass, secondClass, thirdClass);
    }
}



