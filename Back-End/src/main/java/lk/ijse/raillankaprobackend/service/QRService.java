package lk.ijse.raillankaprobackend.service;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface QRService {

//    byte[] generateQRCode(String content, int width, int height);

    byte[] generateBeautifulBookingQR(String bookingId);

}

