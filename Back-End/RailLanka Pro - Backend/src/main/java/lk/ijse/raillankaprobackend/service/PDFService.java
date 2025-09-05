package lk.ijse.raillankaprobackend.service;

import java.io.ByteArrayOutputStream;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */
public interface PDFService {

    ByteArrayOutputStream generateEmployeePdfByStation(String stationName);
}
