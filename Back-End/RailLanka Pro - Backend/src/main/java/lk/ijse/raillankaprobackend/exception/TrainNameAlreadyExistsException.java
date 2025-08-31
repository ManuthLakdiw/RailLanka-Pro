package lk.ijse.raillankaprobackend.exception;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public class TrainNameAlreadyExistsException extends RuntimeException {
    public TrainNameAlreadyExistsException(String message) {
        super(message);
    }
}
