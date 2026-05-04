package lk.ijse.raillankaprobackend.entity.projection;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface TrainStationProjection {
    String getStationName();
    String getStationCode();
    boolean getStatus();  // active/inactive
    String getTrainName();

}
