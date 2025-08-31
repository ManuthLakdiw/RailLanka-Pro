package lk.ijse.raillankaprobackend.exception;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public class StationNameAlreadyExistsException extends RuntimeException {
    public StationNameAlreadyExistsException(String message) {
        super(message);
    }
}
