package lk.ijse.raillankaprobackend.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSupportDto {
    private String ticketId;
    private String requesterName;
    private String requesterEmail;
    private String subject;
    private String description;
    private String category;
    private String priority;
    private List<MultipartFile> attachments;

}
