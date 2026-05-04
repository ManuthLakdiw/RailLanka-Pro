package lk.ijse.raillankaprobackend.entity.projection;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface EmployeeCountsProjection {
    long getActiveCount();
    long getInactiveCount();
    long getTotalCount();
}
