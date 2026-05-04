package lk.ijse.raillankaprobackend.exception;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public class IdGenerateLimitReachedException extends RuntimeException {
    public IdGenerateLimitReachedException(String message) {
        super(message);
    }
}
