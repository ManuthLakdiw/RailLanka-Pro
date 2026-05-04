package lk.ijse.raillankaprobackend.dto;

import lombok.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayHereDto {
    String merchantId;
    String merchantSecret;
    String orderId;
    double orderAmount;
    String currency;
    String hash;
    String appId;
    String appSecret;
    private PersonalDetailDto personalDetail;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PersonalDetailDto{
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zip;
        private String country;
        private String itemName;
    }

}
