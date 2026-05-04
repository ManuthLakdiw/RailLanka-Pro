package lk.ijse.raillankaprobackend.entity.projection;

import java.time.LocalDate;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */


public interface StaffProjection {
    String getName();
    String getId();
    String getEmail();
    String getTelephone();
    String getPosition();
    LocalDate getJoinDate();
    LocalDate getDob();
    String getLocation();
    String getStatus();




}
