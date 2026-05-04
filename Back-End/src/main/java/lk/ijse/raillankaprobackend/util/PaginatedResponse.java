package lk.ijse.raillankaprobackend.util;

import lombok.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PaginatedResponse<T> extends ApiResponse<T> {
    private int totalPages;
    private int currentPage;
    private long totalItems;
    private int startNumber;
    private int endNumber;

    @Builder
    public PaginatedResponse(int code, String message, int totalPages, int currentPage, long totalItems, T data , int startNumber, int endNumber) {
        super(code, message, data);
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.startNumber = startNumber;
        this.endNumber = endNumber;
    }
}
