package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.PayHereDto;
import lk.ijse.raillankaprobackend.entity.User;
import lk.ijse.raillankaprobackend.repository.UserRepository;
import lk.ijse.raillankaprobackend.service.PayHereService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class PayHereServiceImpl implements PayHereService {

    private final UserRepository userRepository;

    private String MERCHANT_ID="1232119";

    private String MERCHANT_SECRET="Mjg2NTQ2MzI5Mzk1MzU1ODc0NzUyMDgyMzcyMzQ0MjI1NDU4Mw==";
    private String CURRENCY="LKR";


    @Override
    public PayHereDto generateHash(String userName, PayHereDto payHereHashDto) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String orderId = "ORD" + System.currentTimeMillis();

        // Format amount to exactly 2 decimal places with dot separator
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        df.setGroupingUsed(false);
        String formattedAmount = df.format(payHereHashDto.getOrderAmount());

        // Generate hash according to PayHere specification
        String hashedSecret = generateMD5Hash(MERCHANT_SECRET).toUpperCase();
        String hashInput = MERCHANT_ID + orderId + formattedAmount + CURRENCY + hashedSecret;
        String finalHash = generateMD5Hash(hashInput).toUpperCase();

        // Debug logging (remove in production)
        log.info("Hash Generation Details:");
        log.info("Merchant ID: {}", MERCHANT_ID);
        log.info("Order ID: {}", orderId);
        log.info("Formatted Amount: {}", formattedAmount);
        log.info("Currency: {}", CURRENCY);
        log.info("Hash Input: {}", hashInput);
        log.info("Final Hash: {}", finalHash);

        // Build response
        PayHereDto.PersonalDetailDto personalDetail = PayHereDto.PersonalDetailDto.builder()
                .phone(user.getPassenger().getPhoneNumber())
                .email(user.getPassenger().getEmail())
                .firstName(user.getPassenger().getFirstName())
                .lastName(user.getPassenger().getLastName())
                .address("No.1, Galle Road")
                .city("Colombo")
                .itemName("RailLanka Pro - Booking Ticket")
                .build();

        return PayHereDto.builder()
                .orderId(orderId)
                .currency(CURRENCY)
                .merchantId(MERCHANT_ID)
                .hash(finalHash)
                .orderAmount(payHereHashDto.getOrderAmount())
                .personalDetail(personalDetail)
                .build();
    }




    private String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 hash generation failed", e);
        }

    }

}
