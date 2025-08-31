package lk.ijse.raillankaprobackend.entity.projection;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface TrainProjection{
    String getTrainId();
    String getName();
    String getCategory();
    String getTrainType();
    String getClasses();
    boolean getActive();
    Long getStopStationCount();
    String getStationNames();
    String getCargoType();
    Long getCapacity();
    String getSpecialFeatures();
    String getSpecialTrainType();
}
