package lk.ijse.raillankaprobackend.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import lk.ijse.raillankaprobackend.dto.BookingDto;
import lk.ijse.raillankaprobackend.dto.FullEmployeeRecordDto;
import lk.ijse.raillankaprobackend.dto.PayeeInfoDto;
import lk.ijse.raillankaprobackend.entity.*;
import lk.ijse.raillankaprobackend.entity.Dtypes.PassengerType;
import lk.ijse.raillankaprobackend.entity.projection.StaffProjection;
import lk.ijse.raillankaprobackend.repository.*;
import lk.ijse.raillankaprobackend.service.BookingService;
import lk.ijse.raillankaprobackend.service.PDFService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PDFServiceImpl implements PDFService {

    private final StationRepository stationRepository;
    private final PassengerRepository passengerRepository;
    private final EmployeeRepository employeeRepository;
    private final TrainRepository trainRepository;
    private final ScheduleRepository scheduleRepository;
    private final StationMasterRepository stationMasterRepository;
    private final CounterRepository counterRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;


    // Enhanced Rail Lanka theme colors
    private static final Color PRIMARY_BLUE = new Color(30, 64, 175);
    private static final Color SECONDARY_BLUE = new Color(59, 130, 246);
    private static final Color LIGHT_BG = new Color(249, 250, 251);
    private static final Color HEADER_BG = new Color(243, 244, 246);
    private static final Color TEXT_DARK = new Color(31, 41, 55);
    private static final Color TEXT_LIGHT = new Color(107, 114, 128);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);



    @Override
    public ByteArrayOutputStream generateEmployeePdfByStation(String stationName) {
        Station station = stationRepository.findByName(stationName)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        List<StaffProjection> staffProjections = stationRepository.findAllStaffByStationName(station.getName());

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding
            addDocumentHeader(document, station);

            // Add summary statistics section
            addSummarySection(document, staffProjections, station);

            // Create employee table with enhanced design
            addEmployeeTable(document, staffProjections);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateAllPassengersPdf() {
        List<Passenger> passengers = passengerRepository.findAll();

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for passengers
            addPassengerDocumentHeader(document, "PASSENGER DIRECTORY");

            // Add summary statistics section for passengers
            addPassengerSummarySection(document, passengers);

            // Create passenger table with enhanced design
            addPassengerTable(document, passengers);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateLocalPassengersPdf() {
        List<Passenger> localPassengers = passengerRepository.findPassengerByPassengerType(PassengerType.LOCAL);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for local passengers
            addPassengerDocumentHeader(document,"LOCAL PASSENGER DIRECTORY");

            // Add summary statistics section for local passengers
            addPassengerSummarySection(document, localPassengers);

            // Create passenger table with enhanced design
            addPassengerTable(document, localPassengers);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateForeignPassengersPdf() {
        List<Passenger> localPassengers = passengerRepository.findPassengerByPassengerType(PassengerType.FOREIGN);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for local passengers
            addPassengerDocumentHeader(document,"FOREIGN PASSENGER DIRECTORY");

            // Add summary statistics section for local passengers
            addPassengerSummarySection(document, localPassengers);

            // Create passenger table with enhanced design
            addPassengerTable(document, localPassengers);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateAllActivePassengersPdf() {
        List<Passenger> allActivePassengers = passengerRepository.findAllByBlocked(false);

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for local passengers
            addPassengerDocumentHeader(document,"ACTIVE PASSENGER DIRECTORY");

            // Add summary statistics section for local passengers
            addPassengerSummarySection(document, allActivePassengers);

            // Create passenger table with enhanced design
            addPassengerTable(document, allActivePassengers);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateAllBlockedPassengersPdf() {
        List<Passenger> allActivePassengers = passengerRepository.findAllByBlocked(true);

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for local passengers
            addPassengerDocumentHeader(document,"BLOCKED PASSENGER DIRECTORY");

            // Add summary statistics section for local passengers
            addPassengerSummarySection(document, allActivePassengers);

            // Create passenger table with enhanced design
            addPassengerTable(document, allActivePassengers);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateLocalActivePassengersPdf() {
        List<Passenger> byPassengerTypeAndBlocked = passengerRepository
                .findByPassengerTypeAndBlocked(PassengerType.LOCAL, false);


        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for local passengers
            addPassengerDocumentHeader(document,"LOCAL ACTIVE PASSENGER DIRECTORY");

            // Add summary statistics section for local passengers
            addPassengerSummarySection(document, byPassengerTypeAndBlocked);

            // Create passenger table with enhanced design
            addPassengerTable(document, byPassengerTypeAndBlocked);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }


        return out;
    }

    @Override
    public ByteArrayOutputStream generateLocalBlockedPassengersPdf() {
        List<Passenger> byPassengerTypeAndBlocked = passengerRepository
                .findByPassengerTypeAndBlocked(PassengerType.LOCAL, true);


        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for local passengers
            addPassengerDocumentHeader(document,"LOCAL BLOCKED PASSENGER DIRECTORY");

            // Add summary statistics section for local passengers
            addPassengerSummarySection(document, byPassengerTypeAndBlocked);

            // Create passenger table with enhanced design
            addPassengerTable(document, byPassengerTypeAndBlocked);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }


        return out;
    }

    @Override
    public ByteArrayOutputStream generateForeignActivePassengersPdf() {
        List<Passenger> byPassengerTypeAndBlocked = passengerRepository
                .findByPassengerTypeAndBlocked(PassengerType.FOREIGN, false);


        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for local passengers
            addPassengerDocumentHeader(document,"FOREIGN ACTIVE PASSENGER DIRECTORY");

            // Add summary statistics section for local passengers
            addPassengerSummarySection(document, byPassengerTypeAndBlocked);

            // Create passenger table with enhanced design
            addPassengerTable(document, byPassengerTypeAndBlocked);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }


        return out;
    }

    @Override
    public ByteArrayOutputStream generateForeignBlockedPassengersPdf() {
        List<Passenger> byPassengerTypeAndBlocked = passengerRepository
                .findByPassengerTypeAndBlocked(PassengerType.FOREIGN, true);


        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for local passengers
            addPassengerDocumentHeader(document,"FOREIGN BLOCKED PASSENGER DIRECTORY");

            // Add summary statistics section for local passengers
            addPassengerSummarySection(document, byPassengerTypeAndBlocked);

            // Create passenger table with enhanced design
            addPassengerTable(document, byPassengerTypeAndBlocked);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }


        return out;
    }





    @Override
    public ByteArrayOutputStream generateAllTrainsPdf() {
        List<Train> trains = trainRepository.findAll();

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for trains
            addTrainDocumentHeader(document, "TRAIN DIRECTORY");

            // Add summary statistics section for trains
            addTrainSummarySection(document, trains);

            // Create train table with enhanced design
            addTrainTable(document, trains);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateActiveTrainsPdf() {
        List<Train> trains = trainRepository.findAllByActive(true);

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for trains
            addTrainDocumentHeader(document, "ACTIVE TRAIN DIRECTORY");

            // Add summary statistics section for trains
            addTrainSummarySection(document, trains);

            // Create train table with enhanced design
            addTrainTable(document, trains);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateInactiveTrainsPdf() {
        List<Train> trains = trainRepository.findAllByActive(false);

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for trains
            addTrainDocumentHeader(document, "INACTIVE TRAIN DIRECTORY");

            // Add summary statistics section for trains
            addTrainSummarySection(document, trains);

            // Create train table with enhanced design
            addTrainTable(document, trains);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }


    private void addTrainDocumentHeader(Document document, String titleText) throws DocumentException {
        // Create header table (same as passenger version)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Logo and company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Try to add logo (if available)
        try {
            URL logoUrl = getClass().getResource("/images/rail-lanka-logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 50);
                leftCell.addElement(logo);

            } else {
                // Create text-based logo
                Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
                Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
                leftCell.addElement(logoText);
            }
        } catch (Exception e) {
            // Fallback to text logo
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
            Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
            leftCell.addElement(logoText);
        }

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);
        Paragraph companyInfo = new Paragraph("Sri Lanka Railway Department\nColombo, Sri Lanka", companyFont);
        companyInfo.setSpacingBefore(5f);
        leftCell.addElement(companyInfo);

        // Right side - Report title and details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_BLUE);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        String dateString = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph datePara = new Paragraph("Generated on: " + dateString, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingBefore(5f);

        rightCell.addElement(title);
        rightCell.addElement(datePara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add decorative separator
        addSeparator(document);
    }

    private void addTrainSummarySection(Document document, List<Train> trains) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(15f);
        summaryTable.setWidths(new float[]{1, 1, 1, 1});

        // Summary header
        Font summaryHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("TRAIN SUMMARY", summaryHeaderFont));
        headerCell.setBackgroundColor(PRIMARY_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setColspan(4);
        headerCell.setPadding(8);
        headerCell.setBorderWidth(0);
        summaryTable.addCell(headerCell);

        int activeCount = 0;
        int passengerCount = 0;
        int goodsCount = 0;
        int specialCount = 0;
        int postCount = 0;

        for (Train t : trains) {
            if (t.isActive()) activeCount++;

            String category = t.getCategory().name();
            switch (category) {
                case "PASSENGER" -> passengerCount++;
                case "GOODS" -> goodsCount++;
                case "SPECIAL" -> specialCount++;
                case "POST" -> postCount++;
            }
        }

        String[] labels = {"Total Trains", "Active Trains", "Passenger Trains", "Goods Trains", "Special Trains", "Post Trains"};
        String[] values = {
                String.valueOf(trains.size()),
                String.valueOf(activeCount),
                String.valueOf(passengerCount),
                String.valueOf(goodsCount),
                String.valueOf(specialCount),
                String.valueOf(postCount)
        };

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);

        for (int i = 0; i < labels.length; i++) {
            PdfPCell labelCell = new PdfPCell(new Phrase(labels[i], labelFont));
            labelCell.setBackgroundColor(HEADER_BG);
            labelCell.setPadding(6);
            labelCell.setBorderWidth(0.5f);
            labelCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(labelCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(values[i], valueFont));
            valueCell.setPadding(6);
            valueCell.setBorderWidth(0.5f);
            valueCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(valueCell);
        }

        document.add(summaryTable);
    }

    private void addTrainTable(Document document, List<Train> trains) throws DocumentException {
        if (trains.isEmpty()) {
            Font emptyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_LIGHT);
            Paragraph empty = new Paragraph("No trains found.", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            empty.setSpacingBefore(20f);
            document.add(empty);
            return;
        }

        // Create table with enhanced design
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{1.5f, 2.5f, 1.5f, 1.5f, 2f, 1.5f});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headers = {"Train ID", "Name", "Category", "Train Type", "Classes", "Status"};

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(SECONDARY_BLUE);
            headerCell.setPadding(8);
            headerCell.setBorderWidth(0);
            headerCell.setBorderColor(Color.WHITE);
            headerCell.setBorderWidthRight(1);
            headerCell.setBorderColorRight(Color.WHITE);
            table.addCell(headerCell);
        }

        // Data rows with enhanced styling
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

        boolean alternate = false;
        int rowCount = 0;


        for (Train train : trains) {
            Color rowColor = alternate ? LIGHT_BG : Color.WHITE;
            alternate = !alternate;
            rowCount++;

            addStyledCell(table, train.getTrainId(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, train.getName(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, train.getCategory().name().charAt(0) + train.getCategory().name().substring(1).toLowerCase(),
                    rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, train.getTrainType().name().charAt(0) + train.getTrainType().name().substring(1).toLowerCase(),
                    rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, train.getClasses(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, train.isActive() ? "Active" : "Inactive", rowColor, dataFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Add row count summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        Paragraph summary = new Paragraph(
                "Total trains displayed: " + rowCount, summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(10f);
        document.add(summary);
    }






    @Override
    public ByteArrayOutputStream generateAllStationPdf() {
        List<Station> stations = stationRepository.findAll();

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for stations
            addStationDocumentHeader(document, "STATION DIRECTORY");

            // Add summary statistics section for stations with province-wise count
            addStationSummarySection(document, stations);

            // Create station table with enhanced design
            addStationTable(document, stations);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateActiveStationPdf() {
        List<Station> stations = stationRepository.findStationByInService(true);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for stations
            addStationDocumentHeader(document, "ACTIVE STATION DIRECTORY");

            // Add summary statistics section for stations with province-wise count
            addStationSummarySection(document, stations);

            // Create station table with enhanced design
            addStationTable(document, stations);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateInactiveStationPdf() {
        List<Station> stations = stationRepository.findStationByInService(false);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for stations
            addStationDocumentHeader(document, "INACTIVE STATION DIRECTORY");

            // Add summary statistics section for stations with province-wise count
            addStationSummarySection(document, stations);

            // Create station table with enhanced design
            addStationTable(document, stations);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }



    private void addStationDocumentHeader(Document document, String titleText) throws DocumentException {
        // Create header table (same as other reports)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Logo and company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Try to add logo (if available)
        try {
            URL logoUrl = getClass().getResource("/images/rail-lanka-logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 50);
                leftCell.addElement(logo);

            } else {
                // Create text-based logo
                Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
                Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
                leftCell.addElement(logoText);
            }
        } catch (Exception e) {
            // Fallback to text logo
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
            Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
            leftCell.addElement(logoText);
        }

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);
        Paragraph companyInfo = new Paragraph("Sri Lanka Railway Department\nColombo, Sri Lanka", companyFont);
        companyInfo.setSpacingBefore(5f);
        leftCell.addElement(companyInfo);

        // Right side - Report title and details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_BLUE);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        String dateString = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph datePara = new Paragraph("Generated on: " + dateString, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingBefore(5f);

        rightCell.addElement(title);
        rightCell.addElement(datePara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add decorative separator
        addSeparator(document);
    }

    private void addStationSummarySection(Document document, List<Station> stations) throws DocumentException {
        String[] allProvinces = {
                "Western province", "Central province", "Southern province", "Northern province",
                "Eastern province", "North Western province", "North Central province",
                "Uva province", "Sabaragamuwa province"
        };

        // Calculate province-wise counts
        Map<String, Long> provinceCounts = stations.stream()
                .collect(Collectors.groupingBy(Station::getProvince, Collectors.counting()));

        long activeCount = stations.stream().filter(Station::isInService).count();
        long inactiveCount = stations.size() - activeCount;

        // Create summary table with fixed columns for all 9 provinces
        PdfPTable summaryTable = new PdfPTable(4); // 2 fixed columns + 2 for province stats
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(15f);
        summaryTable.setWidths(new float[]{1, 1, 1, 1});

        // Summary header
        Font summaryHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("STATION SUMMARY", summaryHeaderFont));
        headerCell.setBackgroundColor(PRIMARY_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setColspan(4);
        headerCell.setPadding(8);
        headerCell.setBorderWidth(0);
        summaryTable.addCell(headerCell);

        // Add general statistics
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);

        // Total Stations
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total Stations", labelFont));
        totalLabelCell.setBackgroundColor(HEADER_BG);
        totalLabelCell.setPadding(6);
        totalLabelCell.setBorderWidth(0.5f);
        totalLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.valueOf(stations.size()), valueFont));
        totalValueCell.setPadding(6);
        totalValueCell.setBorderWidth(0.5f);
        totalValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalValueCell);

        // Active Stations
        PdfPCell activeLabelCell = new PdfPCell(new Phrase("Active Stations", labelFont));
        activeLabelCell.setBackgroundColor(HEADER_BG);
        activeLabelCell.setPadding(6);
        activeLabelCell.setBorderWidth(0.5f);
        activeLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeLabelCell);

        PdfPCell activeValueCell = new PdfPCell(new Phrase(String.valueOf(activeCount), valueFont));
        activeValueCell.setPadding(6);
        activeValueCell.setBorderWidth(0.5f);
        activeValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeValueCell);

        // Inactive Stations
        PdfPCell inactiveLabelCell = new PdfPCell(new Phrase("Inactive Stations", labelFont));
        inactiveLabelCell.setBackgroundColor(HEADER_BG);
        inactiveLabelCell.setPadding(6);
        inactiveLabelCell.setBorderWidth(0.5f);
        inactiveLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveLabelCell);

        PdfPCell inactiveValueCell = new PdfPCell(new Phrase(String.valueOf(inactiveCount), valueFont));
        inactiveValueCell.setPadding(6);
        inactiveValueCell.setBorderWidth(0.5f);
        inactiveValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveValueCell);

        // Province-wise counts header
        PdfPCell provinceHeaderCell = new PdfPCell(new Phrase("PROVINCE WISE STATION COUNT", labelFont));
        provinceHeaderCell.setBackgroundColor(SECONDARY_BLUE);
        provinceHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        provinceHeaderCell.setPadding(6);
        provinceHeaderCell.setBorderWidth(0.5f);
        provinceHeaderCell.setBorderColor(Color.LIGHT_GRAY);
        provinceHeaderCell.setColspan(4);
        summaryTable.addCell(provinceHeaderCell);

        // Add province-wise counts (all 9 provinces)
        for (String province : allProvinces) {
            String shortProvince = province.replace(" province", "");
            PdfPCell provinceLabelCell = new PdfPCell(new Phrase(shortProvince, labelFont));
            provinceLabelCell.setBackgroundColor(HEADER_BG);
            provinceLabelCell.setPadding(6);
            provinceLabelCell.setBorderWidth(0.5f);
            provinceLabelCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(provinceLabelCell);

            Long count = provinceCounts.getOrDefault(province, 0L);
            PdfPCell provinceValueCell = new PdfPCell(new Phrase(String.valueOf(count), valueFont));
            provinceValueCell.setPadding(6);
            provinceValueCell.setBorderWidth(0.5f);
            provinceValueCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(provinceValueCell);
        }

        document.add(summaryTable);
    }

    private void addStationTable(Document document, List<Station> stations) throws DocumentException {
        if (stations.isEmpty()) {
            Font emptyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_LIGHT);
            Paragraph empty = new Paragraph("No stations found.", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            empty.setSpacingBefore(20f);
            document.add(empty);
            return;
        }

        // Create table with enhanced design
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{1.2f, 2f, 1f, 1.5f, 1.5f, 1.2f, 1.5f, 2f, 1.2f});

        // Table header with enhanced styling
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headers = {"Station ID", "Name", "Station Code", "District", "Province", "Platforms", "Platform Length", "Facilities", "Status"};

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(SECONDARY_BLUE);
            headerCell.setPadding(8);
            headerCell.setBorderWidth(0);
            headerCell.setBorderColor(Color.WHITE);
            headerCell.setBorderWidthRight(1);
            headerCell.setBorderColorRight(Color.WHITE);
            table.addCell(headerCell);
        }

        // Data rows with enhanced styling
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

        boolean alternate = false;
        int rowCount = 0;

        for (Station station : stations) {
            Color rowColor = alternate ? LIGHT_BG : Color.WHITE;
            alternate = !alternate;
            rowCount++;

            addStyledCell(table, station.getStationId(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, station.getName(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, station.getStationCode(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, station.getDistrict(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, station.getProvince(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, String.valueOf(station.getNoOfPlatforms()), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, station.getPlatformLength() + " m", rowColor, dataFont, Element.ALIGN_CENTER);

            // Truncate facilities if too long
            String facilities = station.getOtherFacilities();
            if (facilities != null && facilities.length() > 50) {
                facilities = facilities.substring(0, 47) + "...";
            }
            addStyledCell(table, facilities != null ? facilities : "", rowColor, dataFont, Element.ALIGN_LEFT);

            addStyledCell(table, station.isInService() ? "Active" : "Inactive", rowColor, dataFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Add row count summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        Paragraph summary = new Paragraph(
                "Total stations displayed: " + rowCount, summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(10f);
        document.add(summary);
    }




    @Override
    public ByteArrayOutputStream generateAllSchedulesPdf() {
        List<Schedule> schedules = scheduleRepository.findAll();

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for schedules
            addScheduleDocumentHeader(document, "SCHEDULE DIRECTORY");

            // Add summary statistics section for schedules
            addScheduleSummarySection(document, schedules);

            // Create schedule table with enhanced design
            addScheduleTable(document, schedules);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateActiveSchedulesPdf() {
        List<Schedule> schedules = scheduleRepository.findSchedulesByStatus(true);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for schedules
            addScheduleDocumentHeader(document, "ACTIVE SCHEDULE DIRECTORY");

            // Add summary statistics section for schedules
            addScheduleSummarySection(document, schedules);

            // Create schedule table with enhanced design
            addScheduleTable(document, schedules);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateInactiveSchedulesPdf() {
        List<Schedule> schedules = scheduleRepository.findSchedulesByStatus(false);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for schedules
            addScheduleDocumentHeader(document, "INACTIVE SCHEDULE DIRECTORY");

            // Add summary statistics section for schedules
            addScheduleSummarySection(document, schedules);

            // Create schedule table with enhanced design
            addScheduleTable(document, schedules);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    private void addScheduleDocumentHeader(Document document, String titleText) throws DocumentException {
        // Create header table (same as other reports)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Logo and company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Try to add logo (if available)
        try {
            URL logoUrl = getClass().getResource("/images/rail-lanka-logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 50);
                leftCell.addElement(logo);

            } else {
                // Create text-based logo
                Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
                Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
                leftCell.addElement(logoText);
            }
        } catch (Exception e) {
            // Fallback to text logo
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
            Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
            leftCell.addElement(logoText);
        }

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);
        Paragraph companyInfo = new Paragraph("Sri Lanka Railway Department\nColombo, Sri Lanka", companyFont);
        companyInfo.setSpacingBefore(5f);
        leftCell.addElement(companyInfo);

        // Right side - Report title and details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_BLUE);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        String dateString = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph datePara = new Paragraph("Generated on: " + dateString, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingBefore(5f);

        rightCell.addElement(title);
        rightCell.addElement(datePara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add decorative separator
        addSeparator(document);
    }

    private void addScheduleSummarySection(Document document, List<Schedule> schedules) throws DocumentException {
        long activeCount = 0;
        long inactiveCount = 0;

        // Active / inactive count ganna
        for (Schedule schedule : schedules) {
            if (schedule.isStatus()) {
                activeCount++;
            } else {
                inactiveCount++;
            }
        }

        // Count by frequency
        Map<String, Integer> frequencyCounts = new HashMap<>();
        for (Schedule schedule : schedules) {
            String freq = schedule.getScheduleFrequency().name();
            frequencyCounts.put(freq, frequencyCounts.getOrDefault(freq, 0) + 1);
        }


        // Define all possible frequencies
        String[] allFrequencies = {"DAILY", "WEEK_DAYS", "WEEK_ENDS", "CUSTOM"};

        // Create summary table
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(15f);
        summaryTable.setWidths(new float[]{1, 1, 1, 1});

        // Summary header
        Font summaryHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("SCHEDULE SUMMARY", summaryHeaderFont));
        headerCell.setBackgroundColor(PRIMARY_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setColspan(4);
        headerCell.setPadding(8);
        headerCell.setBorderWidth(0);
        summaryTable.addCell(headerCell);

        // Add general statistics
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);

        // Total Schedules
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total Schedules", labelFont));
        totalLabelCell.setBackgroundColor(HEADER_BG);
        totalLabelCell.setPadding(6);
        totalLabelCell.setBorderWidth(0.5f);
        totalLabelCell.setColspan(1);
        totalLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.valueOf(schedules.size()), valueFont));
        totalValueCell.setPadding(6);
        totalValueCell.setBorderWidth(0.5f);
        totalValueCell.setColspan(3);
        totalValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalValueCell);

        // Active Schedules
        PdfPCell activeLabelCell = new PdfPCell(new Phrase("Active Schedules", labelFont));
        activeLabelCell.setBackgroundColor(HEADER_BG);
        activeLabelCell.setPadding(6);
        activeLabelCell.setColspan(1);
        activeLabelCell.setBorderWidth(0.5f);
        activeLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeLabelCell);

        PdfPCell activeValueCell = new PdfPCell(new Phrase(String.valueOf(activeCount), valueFont));
        activeValueCell.setPadding(6);
        activeValueCell.setBorderWidth(0.5f);
        activeValueCell.setColspan(3);
        activeValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeValueCell);

        // Inactive Schedules
        PdfPCell inactiveLabelCell = new PdfPCell(new Phrase("Inactive Schedules", labelFont));
        inactiveLabelCell.setBackgroundColor(HEADER_BG);
        inactiveLabelCell.setPadding(6);
        inactiveLabelCell.setColspan(1);
        inactiveLabelCell.setBorderWidth(0.5f);
        inactiveLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveLabelCell);

        PdfPCell inactiveValueCell = new PdfPCell(new Phrase(String.valueOf(inactiveCount), valueFont));
        inactiveValueCell.setPadding(6);
        inactiveValueCell.setBorderWidth(0.5f);
        inactiveValueCell.setColspan(3);
        inactiveValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveValueCell);

        // Frequency-wise counts header
        PdfPCell frequencyHeaderCell = new PdfPCell(new Phrase("FREQUENCY DISTRIBUTION", labelFont));
        frequencyHeaderCell.setBackgroundColor(SECONDARY_BLUE);
        frequencyHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        frequencyHeaderCell.setPadding(6);
        frequencyHeaderCell.setBorderWidth(0.5f);
        frequencyHeaderCell.setBorderColor(Color.LIGHT_GRAY);
        frequencyHeaderCell.setColspan(4);
        summaryTable.addCell(frequencyHeaderCell);

        // Add frequency-wise counts (all frequencies)
        for (String frequency : allFrequencies) {
            String formattedFrequency = frequency.replace("_", " ").toLowerCase();
            formattedFrequency = formattedFrequency.substring(0, 1).toUpperCase() + formattedFrequency.substring(1);

            PdfPCell frequencyLabelCell = new PdfPCell(new Phrase(formattedFrequency, labelFont));
            frequencyLabelCell.setBackgroundColor(HEADER_BG);
            frequencyLabelCell.setPadding(6);
            frequencyLabelCell.setBorderWidth(0.5f);
            frequencyLabelCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(frequencyLabelCell);

            Long count = Long.valueOf(frequencyCounts.getOrDefault(frequency, 0));
            PdfPCell frequencyValueCell = new PdfPCell(new Phrase(String.valueOf(count), valueFont));
            frequencyValueCell.setPadding(6);
            frequencyValueCell.setBorderWidth(0.5f);
            frequencyValueCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(frequencyValueCell);
        }

        document.add(summaryTable);
    }

    private void addScheduleTable(Document document, List<Schedule> schedules) throws DocumentException {
        if (schedules.isEmpty()) {
            Font emptyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_LIGHT);
            Paragraph empty = new Paragraph("No schedules found.", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            empty.setSpacingBefore(20f);
            document.add(empty);
            return;
        }

        // Create table with enhanced design
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{1.2f, 2f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.2f});

        // Table header with enhanced styling
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headers = {"Schedule ID", "Description", "Train", "Departure Station", "Arrival Station", "Departure Time", "Arrival Time", "Frequency", "Status"};

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(SECONDARY_BLUE);
            headerCell.setPadding(8);
            headerCell.setBorderWidth(0);
            headerCell.setBorderColor(Color.WHITE);
            headerCell.setBorderWidthRight(1);
            headerCell.setBorderColorRight(Color.WHITE);
            table.addCell(headerCell);
        }

        // Data rows with enhanced styling
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

        boolean alternate = false;
        int rowCount = 0;

        for (Schedule schedule : schedules) {
            Color rowColor = alternate ? LIGHT_BG : Color.WHITE;
            alternate = !alternate;
            rowCount++;

            addStyledCell(table, schedule.getScheduleId(), rowColor, dataFont, Element.ALIGN_CENTER);

            // Truncate description if too long
            String description = schedule.getDescription();
            if (description != null && description.length() > 30) {
                description = description.substring(0, 27) + "...";
            }
            addStyledCell(table, description != null ? description : "N/A", rowColor, dataFont, Element.ALIGN_LEFT);

            addStyledCell(table, schedule.getTrain().getName(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, schedule.getMainDepartureStation().getName(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, schedule.getMainArrivalStation().getName(), rowColor, dataFont, Element.ALIGN_CENTER);

            // Format time to remove milliseconds
            String departureTime = formatTime(String.valueOf(schedule.getMainDepartureTime()));
            String arrivalTime = formatTime(String.valueOf(schedule.getMainArrivalTime()));

            addStyledCell(table, departureTime, rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, arrivalTime, rowColor, dataFont, Element.ALIGN_CENTER);

            // Format frequency for better readability
            String frequency = schedule.getScheduleFrequency().name();
            if (frequency != null) {
                frequency = frequency.replace("_", " ").toLowerCase();
                frequency = frequency.substring(0, 1).toUpperCase() + frequency.substring(1);
            }
            addStyledCell(table, frequency, rowColor, dataFont, Element.ALIGN_CENTER);

            addStyledCell(table, schedule.isStatus() ? "Active" : "Inactive", rowColor, dataFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Add row count summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        Paragraph summary = new Paragraph(
                "Total schedules displayed: " + rowCount, summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(10f);
        document.add(summary);
    }

    private String formatTime(String timeString) {
        if (timeString == null) return "N/A";

        try {
            // Try to parse the time and format it without milliseconds
            LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"));
            return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e) {
            // If parsing fails, return the original string
            return timeString;
        }
    }



    @Override
    public ByteArrayOutputStream generateAllStationMastersPdf() {
        List<StationMaster> stationMasters = stationMasterRepository.findAll();

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for station masters
            addStationMasterDocumentHeader(document, "STATION MASTER DIRECTORY");

            // Add summary statistics section for station masters
            addStationMasterSummarySection(document, stationMasters);

            // Create station master table with enhanced design
            addStationMasterTable(document, stationMasters);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateActiveStationMastersPdf() {
        List<StationMaster> stationMasters = stationMasterRepository.findStationMasterByActive(true);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for station masters
            addStationMasterDocumentHeader(document, "ACTIVE STATION MASTER DIRECTORY");

            // Add summary statistics section for station masters
            addStationMasterSummarySection(document, stationMasters);

            // Create station master table with enhanced design
            addStationMasterTable(document, stationMasters);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateInactiveStationMastersPdf() {
        List<StationMaster> stationMasters = stationMasterRepository.findStationMasterByActive(false);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for station masters
            addStationMasterDocumentHeader(document, "INACTIVE STATION MASTER DIRECTORY");

            // Add summary statistics section for station masters
            addStationMasterSummarySection(document, stationMasters);

            // Create station master table with enhanced design
            addStationMasterTable(document, stationMasters);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    private void addStationMasterDocumentHeader(Document document, String titleText) throws DocumentException {
        // Create header table (same as other reports)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Logo and company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Try to add logo (if available)
        try {
            URL logoUrl = getClass().getResource("/images/rail-lanka-logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 50);
                leftCell.addElement(logo);

            } else {
                // Create text-based logo
                Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
                Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
                leftCell.addElement(logoText);
            }
        } catch (Exception e) {
            // Fallback to text logo
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
            Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
            leftCell.addElement(logoText);
        }

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);
        Paragraph companyInfo = new Paragraph("Sri Lanka Railway Department\nColombo, Sri Lanka", companyFont);
        companyInfo.setSpacingBefore(5f);
        leftCell.addElement(companyInfo);

        // Right side - Report title and details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_BLUE);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        String dateString = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph datePara = new Paragraph("Generated on: " + dateString, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingBefore(5f);

        rightCell.addElement(title);
        rightCell.addElement(datePara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add decorative separator
        addSeparator(document);
    }

    private void addStationMasterSummarySection(Document document, List<StationMaster> stationMasters) throws DocumentException {
        // Calculate statistics
        long activeCount = stationMasters.stream().filter(StationMaster::isActive).count();
        long inactiveCount = stationMasters.size() - activeCount;

        // Calculate average years of experience
        double avgExperience = stationMasters.stream()
                .mapToInt(StationMaster::getYearsOfExperience)
                .average()
                .orElse(0.0);

        // Calculate experience distribution
        long juniorCount = stationMasters.stream().filter(sm -> sm.getYearsOfExperience() < 5).count();
        long midCount = stationMasters.stream().filter(sm -> sm.getYearsOfExperience() >= 5 && sm.getYearsOfExperience() < 10).count();
        long seniorCount = stationMasters.stream().filter(sm -> sm.getYearsOfExperience() >= 10).count();

        // Create summary table
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(15f);
        summaryTable.setWidths(new float[]{1, 1, 1, 1});

        // Summary header
        Font summaryHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("STATION MASTER SUMMARY", summaryHeaderFont));
        headerCell.setBackgroundColor(PRIMARY_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setColspan(4);
        headerCell.setPadding(8);
        headerCell.setBorderWidth(0);
        summaryTable.addCell(headerCell);

        // Add general statistics
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);

        // Total Station Masters
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total Station Masters", labelFont));
        totalLabelCell.setBackgroundColor(HEADER_BG);
        totalLabelCell.setPadding(6);
        totalLabelCell.setBorderWidth(0.5f);
        totalLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.valueOf(stationMasters.size()), valueFont));
        totalValueCell.setPadding(6);
        totalValueCell.setBorderWidth(0.5f);
        totalValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalValueCell);

        // Active Station Masters
        PdfPCell activeLabelCell = new PdfPCell(new Phrase("Active Station Masters", labelFont));
        activeLabelCell.setBackgroundColor(HEADER_BG);
        activeLabelCell.setPadding(6);
        activeLabelCell.setBorderWidth(0.5f);
        activeLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeLabelCell);

        PdfPCell activeValueCell = new PdfPCell(new Phrase(String.valueOf(activeCount), valueFont));
        activeValueCell.setPadding(6);
        activeValueCell.setBorderWidth(0.5f);
        activeValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeValueCell);

        // Inactive Station Masters
        PdfPCell inactiveLabelCell = new PdfPCell(new Phrase("Inactive Station Masters", labelFont));
        inactiveLabelCell.setBackgroundColor(HEADER_BG);
        inactiveLabelCell.setPadding(6);
        inactiveLabelCell.setBorderWidth(0.5f);
        inactiveLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveLabelCell);

        PdfPCell inactiveValueCell = new PdfPCell(new Phrase(String.valueOf(inactiveCount), valueFont));
        inactiveValueCell.setPadding(6);
        inactiveValueCell.setBorderWidth(0.5f);
        inactiveValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveValueCell);

        // Average Experience
        PdfPCell avgExpLabelCell = new PdfPCell(new Phrase("Avg. Experience (Years)", labelFont));
        avgExpLabelCell.setBackgroundColor(HEADER_BG);
        avgExpLabelCell.setPadding(6);
        avgExpLabelCell.setBorderWidth(0.5f);
        avgExpLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(avgExpLabelCell);

        PdfPCell avgExpValueCell = new PdfPCell(new Phrase(String.format("%.1f", avgExperience), valueFont));
        avgExpValueCell.setPadding(6);
        avgExpValueCell.setBorderWidth(0.5f);
        avgExpValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(avgExpValueCell);

        // Experience distribution header
        PdfPCell expHeaderCell = new PdfPCell(new Phrase("EXPERIENCE DISTRIBUTION", labelFont));
        expHeaderCell.setBackgroundColor(SECONDARY_BLUE);
        expHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        expHeaderCell.setPadding(6);
        expHeaderCell.setBorderWidth(0.5f);
        expHeaderCell.setBorderColor(Color.LIGHT_GRAY);
        expHeaderCell.setColspan(4);
        summaryTable.addCell(expHeaderCell);

        // Junior (0-4 years)
        PdfPCell juniorLabelCell = new PdfPCell(new Phrase("Junior (0-4 years)", labelFont));
        juniorLabelCell.setBackgroundColor(HEADER_BG);
        juniorLabelCell.setPadding(6);
        juniorLabelCell.setBorderWidth(0.5f);
        juniorLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(juniorLabelCell);

        PdfPCell juniorValueCell = new PdfPCell(new Phrase(String.valueOf(juniorCount), valueFont));
        juniorValueCell.setPadding(6);
        juniorValueCell.setBorderWidth(0.5f);
        juniorValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(juniorValueCell);

        // Mid-level (5-9 years)
        PdfPCell midLabelCell = new PdfPCell(new Phrase("Mid-level (5-9 years)", labelFont));
        midLabelCell.setBackgroundColor(HEADER_BG);
        midLabelCell.setPadding(6);
        midLabelCell.setBorderWidth(0.5f);
        midLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(midLabelCell);

        PdfPCell midValueCell = new PdfPCell(new Phrase(String.valueOf(midCount), valueFont));
        midValueCell.setPadding(6);
        midValueCell.setBorderWidth(0.5f);
        midValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(midValueCell);

        // Senior (10+ years)
        PdfPCell seniorLabelCell = new PdfPCell(new Phrase("Senior (10+ years)", labelFont));
        seniorLabelCell.setBackgroundColor(HEADER_BG);
        seniorLabelCell.setPadding(6);
        seniorLabelCell.setBorderWidth(0.5f);
        seniorLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(seniorLabelCell);

        PdfPCell seniorValueCell = new PdfPCell(new Phrase(String.valueOf(seniorCount), valueFont));
        seniorValueCell.setPadding(6);
        seniorValueCell.setBorderWidth(0.5f);
        seniorValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(seniorValueCell);

        document.add(summaryTable);
    }

    private void addStationMasterTable(Document document, List<StationMaster> stationMasters) throws DocumentException {
        if (stationMasters.isEmpty()) {
            Font emptyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_LIGHT);
            Paragraph empty = new Paragraph("No station masters found.", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            empty.setSpacingBefore(20f);
            document.add(empty);
            return;
        }

        // Create table with enhanced design
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{1.2f, 1.2f, 1.5f, 1.8f, 1.5f, 1.5f, 1.5f, 1.2f, 1.2f, 1f});

        // Table header with enhanced styling
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headers = {"Master ID", "First Name", "Last Name", "Email", "Phone", "ID Number", "Station ID", "Experience", "DOB", "Status"};

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(SECONDARY_BLUE);
            headerCell.setPadding(8);
            headerCell.setBorderWidth(0);
            headerCell.setBorderColor(Color.WHITE);
            headerCell.setBorderWidthRight(1);
            headerCell.setBorderColorRight(Color.WHITE);
            table.addCell(headerCell);
        }

        // Data rows with enhanced styling
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

        boolean alternate = false;
        int rowCount = 0;

        for (StationMaster stationMaster : stationMasters) {
            Color rowColor = alternate ? LIGHT_BG : Color.WHITE;
            alternate = !alternate;
            rowCount++;

            addStyledCell(table, stationMaster.getStationMasterId(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, stationMaster.getFirstname(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, stationMaster.getLastname(), rowColor, dataFont, Element.ALIGN_LEFT);

            // Truncate email if too long
            String email = stationMaster.getEmail();
            if (email != null && email.length() > 25) {
                email = email.substring(0, 22) + "...";
            }
            addStyledCell(table, email != null ? email : "N/A", rowColor, dataFont, Element.ALIGN_LEFT);

            addStyledCell(table, stationMaster.getPhoneNumber(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, stationMaster.getIdNumber(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, stationMaster.getStation().getName(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, stationMaster.getYearsOfExperience() + " years", rowColor, dataFont, Element.ALIGN_CENTER);

            // Format date of birth
            String dob = formatDate(String.valueOf(stationMaster.getDob()));
            addStyledCell(table, dob, rowColor, dataFont, Element.ALIGN_CENTER);

            addStyledCell(table, stationMaster.isActive() ? "Active" : "Inactive", rowColor, dataFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Add row count summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        Paragraph summary = new Paragraph(
                "Total station masters displayed: " + rowCount, summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(10f);
        document.add(summary);
    }

    private String formatDate(String dateString) {
        if (dateString == null) return "N/A";

        try {
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception e) {
            return dateString;
        }
    }




    @Override
    public ByteArrayOutputStream generateAllCountersPdf() {
        List<Counter> counters = counterRepository.findAll();

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for counters
            addCounterDocumentHeader(document, "COUNTER STAFF DIRECTORY");

            // Add summary statistics section for counters
            addCounterSummarySection(document, counters);

            // Create counter table with enhanced design
            addCounterTable(document, counters);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateActiveCountersPdf() {
        List<Counter> counters = counterRepository.findCounterByActive(true);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for counters
            addCounterDocumentHeader(document, "ACTIVE COUNTER STAFF DIRECTORY");

            // Add summary statistics section for counters
            addCounterSummarySection(document, counters);

            // Create counter table with enhanced design
            addCounterTable(document, counters);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateInactiveCountersPdf() {
        List<Counter> counters = counterRepository.findCounterByActive(false);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for counters
            addCounterDocumentHeader(document, "INACTIVE COUNTER STAFF DIRECTORY");

            // Add summary statistics section for counters
            addCounterSummarySection(document, counters);

            // Create counter table with enhanced design
            addCounterTable(document, counters);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    private void addCounterDocumentHeader(Document document, String titleText) throws DocumentException {
        // Create header table (same as other reports)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Logo and company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Try to add logo (if available)
        try {
            URL logoUrl = getClass().getResource("/images/rail-lanka-logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 50);
                leftCell.addElement(logo);

            } else {
                // Create text-based logo
                Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
                Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
                leftCell.addElement(logoText);
            }
        } catch (Exception e) {
            // Fallback to text logo
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
            Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
            leftCell.addElement(logoText);
        }

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);
        Paragraph companyInfo = new Paragraph("Sri Lanka Railway Department\nColombo, Sri Lanka", companyFont);
        companyInfo.setSpacingBefore(5f);
        leftCell.addElement(companyInfo);

        // Right side - Report title and details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_BLUE);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        String dateString = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph datePara = new Paragraph("Generated on: " + dateString, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingBefore(5f);

        rightCell.addElement(title);
        rightCell.addElement(datePara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add decorative separator
        addSeparator(document);
    }

    private void addCounterSummarySection(Document document, List<Counter> counters) throws DocumentException {
        long activeCount = counters.stream().filter(c -> c.isActive()).count();
        long inactiveCount = counters.size() - activeCount;

        // Calculate average years of experience
        double avgExperience = counters.stream()
                .mapToInt(c -> c.getYearsOfExperience())
                .average()
                .orElse(0.0);

        Map<String, Long> counterNumberCounts = counters.stream()
                .collect(Collectors.groupingBy(c -> String.valueOf(c.getCounterNumber()), Collectors.counting()));

        String[] allCounterNumbers = {"COUNTER_1", "COUNTER_2", "COUNTER_3"};

        // Calculate experience distribution
        long juniorCount = counters.stream().filter(c -> c.getYearsOfExperience() < 3).count();
        long midCount = counters.stream().filter(c -> c.getYearsOfExperience() >= 3 && c.getYearsOfExperience() < 7).count();
        long seniorCount = counters.stream().filter(c -> c.getYearsOfExperience() >= 7).count();

        // Create summary table
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(15f);
        summaryTable.setWidths(new float[]{1, 1, 1, 1});

        // Summary header
        Font summaryHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("COUNTER STAFF SUMMARY", summaryHeaderFont));
        headerCell.setBackgroundColor(PRIMARY_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setColspan(4);
        headerCell.setPadding(8);
        headerCell.setBorderWidth(0);
        summaryTable.addCell(headerCell);

        // Add general statistics
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);

        // Total Counter Staff
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total Counter Staff", labelFont));
        totalLabelCell.setBackgroundColor(HEADER_BG);
        totalLabelCell.setPadding(6);
        totalLabelCell.setBorderWidth(0.5f);
        totalLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.valueOf(counters.size()), valueFont));
        totalValueCell.setPadding(6);
        totalValueCell.setBorderWidth(0.5f);
        totalValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalValueCell);

        // Active Counter Staff
        PdfPCell activeLabelCell = new PdfPCell(new Phrase("Active Staff", labelFont));
        activeLabelCell.setBackgroundColor(HEADER_BG);
        activeLabelCell.setPadding(6);
        activeLabelCell.setBorderWidth(0.5f);
        activeLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeLabelCell);

        PdfPCell activeValueCell = new PdfPCell(new Phrase(String.valueOf(activeCount), valueFont));
        activeValueCell.setPadding(6);
        activeValueCell.setBorderWidth(0.5f);
        activeValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeValueCell);

        // Inactive Counter Staff
        PdfPCell inactiveLabelCell = new PdfPCell(new Phrase("Inactive Staff", labelFont));
        inactiveLabelCell.setBackgroundColor(HEADER_BG);
        inactiveLabelCell.setPadding(6);
        inactiveLabelCell.setBorderWidth(0.5f);
        inactiveLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveLabelCell);

        PdfPCell inactiveValueCell = new PdfPCell(new Phrase(String.valueOf(inactiveCount), valueFont));
        inactiveValueCell.setPadding(6);
        inactiveValueCell.setBorderWidth(0.5f);
        inactiveValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveValueCell);

        // Average Experience
        PdfPCell avgExpLabelCell = new PdfPCell(new Phrase("Avg. Experience (Years)", labelFont));
        avgExpLabelCell.setBackgroundColor(HEADER_BG);
        avgExpLabelCell.setPadding(6);
        avgExpLabelCell.setBorderWidth(0.5f);
        avgExpLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(avgExpLabelCell);

        PdfPCell avgExpValueCell = new PdfPCell(new Phrase(String.format("%.1f", avgExperience), valueFont));
        avgExpValueCell.setPadding(6);
        avgExpValueCell.setBorderWidth(0.5f);
        avgExpValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(avgExpValueCell);

        // Counter distribution header
        PdfPCell counterHeaderCell = new PdfPCell(new Phrase("COUNTER DISTRIBUTION", labelFont));
        counterHeaderCell.setBackgroundColor(SECONDARY_BLUE);
        counterHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        counterHeaderCell.setPadding(6);
        counterHeaderCell.setBorderWidth(0.5f);
        counterHeaderCell.setBorderColor(Color.LIGHT_GRAY);
        counterHeaderCell.setColspan(4);
        summaryTable.addCell(counterHeaderCell);


        // Add counter-wise counts (all counter numbers)
        for (String counterNumber : allCounterNumbers) {
            String formattedCounter = counterNumber.replace("_", " ");

            PdfPCell counterLabelCell = new PdfPCell(new Phrase(formattedCounter, labelFont));
            counterLabelCell.setBackgroundColor(HEADER_BG);
            counterLabelCell.setPadding(6);
            counterLabelCell.setBorderWidth(0.5f);
            counterLabelCell.setColspan(2);
            counterLabelCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(counterLabelCell);

            Long count = counterNumberCounts.getOrDefault(counterNumber, 0L);
            PdfPCell counterValueCell = new PdfPCell(new Phrase(String.valueOf(count), valueFont));
            counterValueCell.setPadding(6);
            counterValueCell.setBorderWidth(0.5f);
            counterValueCell.setBorderColor(Color.LIGHT_GRAY);
            counterValueCell.setColspan(2);
            summaryTable.addCell(counterValueCell);

        }

        // Experience distribution header
        PdfPCell expHeaderCell = new PdfPCell(new Phrase("EXPERIENCE DISTRIBUTION", labelFont));
        expHeaderCell.setBackgroundColor(SECONDARY_BLUE);
        expHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        expHeaderCell.setPadding(6);
        expHeaderCell.setBorderWidth(0.5f);
        expHeaderCell.setBorderColor(Color.LIGHT_GRAY);
        expHeaderCell.setColspan(4);
        summaryTable.addCell(expHeaderCell);

        // Junior (0-2 years)
        PdfPCell juniorLabelCell = new PdfPCell(new Phrase("Junior (0-2 years)", labelFont));
        juniorLabelCell.setBackgroundColor(HEADER_BG);
        juniorLabelCell.setPadding(6);
        juniorLabelCell.setBorderWidth(0.5f);
        juniorLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(juniorLabelCell);

        PdfPCell juniorValueCell = new PdfPCell(new Phrase(String.valueOf(juniorCount), valueFont));
        juniorValueCell.setPadding(6);
        juniorValueCell.setBorderWidth(0.5f);
        juniorValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(juniorValueCell);

        // Mid-level (3-6 years)
        PdfPCell midLabelCell = new PdfPCell(new Phrase("Mid-level (3-6 years)", labelFont));
        midLabelCell.setBackgroundColor(HEADER_BG);
        midLabelCell.setPadding(6);
        midLabelCell.setBorderWidth(0.5f);
        midLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(midLabelCell);

        PdfPCell midValueCell = new PdfPCell(new Phrase(String.valueOf(midCount), valueFont));
        midValueCell.setPadding(6);
        midValueCell.setBorderWidth(0.5f);
        midValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(midValueCell);

        // Senior (7+ years)
        PdfPCell seniorLabelCell = new PdfPCell(new Phrase("Senior (7+ years)", labelFont));
        seniorLabelCell.setBackgroundColor(HEADER_BG);
        seniorLabelCell.setPadding(6);
        seniorLabelCell.setBorderWidth(0.5f);
        seniorLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(seniorLabelCell);

        PdfPCell seniorValueCell = new PdfPCell(new Phrase(String.valueOf(seniorCount), valueFont));
        seniorValueCell.setPadding(6);
        seniorValueCell.setBorderWidth(0.5f);
        seniorValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(seniorValueCell);

        document.add(summaryTable);
    }

    private void addCounterTable(Document document, List<Counter> counters) throws DocumentException {
        if (counters.isEmpty()) {
            Font emptyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_LIGHT);
            Paragraph empty = new Paragraph("No counter staff found.", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            empty.setSpacingBefore(20f);
            document.add(empty);
            return;
        }

        // Create table with enhanced design
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{1.2f, 1.2f, 1.5f, 1.8f, 1.5f, 1.5f, 1.5f, 1.2f, 1.2f, 1f});

        // Table header with enhanced styling
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headers = {"Counter ID", "First Name", "Last Name", "Email", "Phone", "ID Number", "Station", "Counter No", "Experience", "Status"};

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(SECONDARY_BLUE);
            headerCell.setPadding(8);
            headerCell.setBorderWidth(0);
            headerCell.setBorderColor(Color.WHITE);
            headerCell.setBorderWidthRight(1);
            headerCell.setBorderColorRight(Color.WHITE);
            table.addCell(headerCell);
        }

        // Data rows with enhanced styling
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

        boolean alternate = false;
        int rowCount = 0;

        for (Counter counter : counters) {
            Color rowColor = alternate ? LIGHT_BG : Color.WHITE;
            alternate = !alternate;
            rowCount++;

            addStyledCell(table, counter.getCounterId(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, counter.getFirstname(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, counter.getLastname(), rowColor, dataFont, Element.ALIGN_LEFT);

            // Truncate email if too long
            String email = counter.getEmail();
            if (email != null && email.length() > 25) {
                email = email.substring(0, 22) + "...";
            }
            addStyledCell(table, email != null ? email : "N/A", rowColor, dataFont, Element.ALIGN_LEFT);

            addStyledCell(table, counter.getPhoneNumber(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, counter.getIdNumber(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, counter.getStation().getName(), rowColor, dataFont, Element.ALIGN_CENTER);

            // Format counter number for better readability
            String counterNumber = counter.getCounterNumber().name().charAt(0) + counter.getCounterNumber().name().substring(1).toLowerCase();
            if (counterNumber != null) {
                counterNumber = counterNumber.replace("_", " ");
            }
            addStyledCell(table, counterNumber, rowColor, dataFont, Element.ALIGN_CENTER);

            addStyledCell(table, counter.getYearsOfExperience() + " years", rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, counter.isActive() ? "Active" : "Inactive", rowColor, dataFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Add row count summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        Paragraph summary = new Paragraph(
                "Total counter staff displayed: " + rowCount, summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(10f);
        document.add(summary);
    }



    @Override
    public ByteArrayOutputStream generateAllEmployeesPdf() {
        List<StationMaster> stationMasters = stationMasterRepository.findAll();
        List<Counter> counters = counterRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for employees
            addEmployeeDocumentHeader(document, "EMPLOYEE DIRECTORY");

            // Add summary statistics section for all employees
            addEmployeeSummarySection(document, stationMasters, counters, employees);

            // Create employee table with enhanced design
            addEmployeeTable(document, stationMasters, counters, employees);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;

    }

    @Override
    public ByteArrayOutputStream generateActiveEmployeesPdf() {
        List<StationMaster> stationMasters = stationMasterRepository.findStationMasterByActive(true);
        List<Counter> counters = counterRepository.findCounterByActive(true);
        List<Employee> employees = employeeRepository.findEmployeeByActive(true);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for employees
            addEmployeeDocumentHeader(document, "ACTIVE EMPLOYEE DIRECTORY");

            // Add summary statistics section for all employees
            addEmployeeSummarySection(document, stationMasters, counters, employees);

            // Create employee table with enhanced design
            addEmployeeTable(document, stationMasters, counters, employees);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateInactiveEmployeesPdf() {
        List<StationMaster> stationMasters = stationMasterRepository.findStationMasterByActive(false);
        List<Counter> counters = counterRepository.findCounterByActive(false);
        List<Employee> employees = employeeRepository.findEmployeeByActive(false);

        // Use landscape orientation for better table layout
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add custom header and footer
            RailLankaPageEvent event = new RailLankaPageEvent();
            writer.setPageEvent(event);

            document.open();

            // Add decorative header with Rail Lanka branding for employees
            addEmployeeDocumentHeader(document, "INACTIVE EMPLOYEE DIRECTORY");

            // Add summary statistics section for all employees
            addEmployeeSummarySection(document, stationMasters, counters, employees);

            // Create employee table with enhanced design
            addEmployeeTable(document, stationMasters, counters, employees);

            // Add footer with additional information
            addDocumentFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }


    private void addEmployeeTable(Document document, List<StationMaster> stationMasters,
                                  List<Counter> counters, List<Employee> employees) throws DocumentException {
        // Combine all employees into a single list with a common type indicator
        List<FullEmployeeRecordDto> allEmployees = new ArrayList<>();

        // Add station masters
        for (StationMaster sm : stationMasters) {
            allEmployees.add(new FullEmployeeRecordDto(
                    sm.getStationMasterId(),
                    sm.getFirstname(),
                    sm.getLastname(),
                    sm.getEmail(),
                    sm.getPhoneNumber(),
                    sm.getIdNumber(),
                    "Station Master",
                    sm.getStation().getName(),
                    sm.getYearsOfExperience(),
                    sm.isActive() ? "Active" : "Inactive",
                    sm.getDob().toString(),
                    "STATION_MASTER"
            ));
        }

        // Add counter staff
        for (Counter counter : counters) {
            allEmployees.add(new FullEmployeeRecordDto(
                    counter.getCounterId(),
                    counter.getFirstname(),
                    counter.getLastname(),
                    counter.getEmail(),
                    counter.getPhoneNumber(),
                    counter.getIdNumber(),
                    "Counter Staff (" + counter.getCounterNumber().name().charAt(0)+counter.getCounterNumber().name().substring(8,9) + ")",
                    counter.getStation().getName(),
                    counter.getYearsOfExperience(),
                    counter.isActive() ? "Active" : "Inactive",
                    counter.getDob().toString(),
                    "COUNTER"
            ));
        }

        // Add regular employees
        for (Employee employee : employees) {
            // Calculate years of experience from joining date
            int yearsOfExperience = 0;
            try {
                LocalDate joiningDate = employee.getJoiningDate(); // Already LocalDate
                yearsOfExperience = Period.between(joiningDate, LocalDate.now()).getYears();
            } catch (Exception e) {
                yearsOfExperience = 0;
            }

            allEmployees.add(new FullEmployeeRecordDto(
                    employee.getEmployeeId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    employee.getContactNumber(),
                    employee.getIdNumber(),
                    employee.getPosition().name().charAt(0) + employee.getPosition().name().substring(1).toLowerCase().replace("_", " "),
                    employee.getStation().getName(),
                    yearsOfExperience,
                    employee.isActive() ? "Active" : "Inactive",
                    employee.getDateOfBirth().toString(),
                    "EMPLOYEE"
            ));
        }


        allEmployees.sort(Comparator.comparing(e -> e.getEmployeeId()));


        if (allEmployees.isEmpty()) {
            Font emptyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_LIGHT);
            Paragraph empty = new Paragraph("No employees found.", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            empty.setSpacingBefore(20f);
            document.add(empty);
            return;
        }

        // Create table with enhanced design
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{1.2f, 1.2f, 1.5f, 1.8f, 1.5f, 1.5f, 2f, 1.2f, 1.2f, 1f});

        // Table header with enhanced styling
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headers = {"Employee ID", "First Name", "Last Name", "Email", "Phone", "ID Number", "Position", "Station ID", "Experience", "Status"};

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(SECONDARY_BLUE);
            headerCell.setPadding(8);
            headerCell.setBorderWidth(0);
            headerCell.setBorderColor(Color.WHITE);
            headerCell.setBorderWidthRight(1);
            headerCell.setBorderColorRight(Color.WHITE);
            table.addCell(headerCell);
        }

        // Data rows with enhanced styling
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

        boolean alternate = false;
        int rowCount = 0;

        for (FullEmployeeRecordDto employee : allEmployees) {
            Color rowColor = alternate ? LIGHT_BG : Color.WHITE;
            alternate = !alternate;
            rowCount++;

            addStyledCell(table, employee.getEmployeeId(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, employee.getFirstName(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, employee.getLastName(), rowColor, dataFont, Element.ALIGN_LEFT);

            // Truncate email if too long
            String email = employee.getEmail();
            if (email != null && email.length() > 25) {
                email = email.substring(0, 22) + "...";
            }
            addStyledCell(table, email != null ? email : "N/A", rowColor, dataFont, Element.ALIGN_LEFT);

            addStyledCell(table, employee.getPhoneNumber(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, employee.getIdNumber(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, employee.getPosition(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, employee.getStationId(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, employee.getYearsOfExperience() + " years", rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, employee.getStatus(), rowColor, dataFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Add row count summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        Paragraph summary = new Paragraph(
                "Total employees displayed: " + rowCount, summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(10f);
        document.add(summary);
    }

    private void addEmployeeDocumentHeader(Document document, String titleText) throws DocumentException {
        // Create header table (same as other reports)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Logo and company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Try to add logo (if available)
        try {
            URL logoUrl = getClass().getResource("/images/rail-lanka-logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 50);
                leftCell.addElement(logo);

            } else {
                // Create text-based logo
                Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
                Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
                leftCell.addElement(logoText);
            }
        } catch (Exception e) {
            // Fallback to text logo
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
            Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
            leftCell.addElement(logoText);
        }

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);
        Paragraph companyInfo = new Paragraph("Sri Lanka Railway Department\nColombo, Sri Lanka", companyFont);
        companyInfo.setSpacingBefore(5f);
        leftCell.addElement(companyInfo);

        // Right side - Report title and details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_BLUE);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        String dateString = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph datePara = new Paragraph("Generated on: " + dateString, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingBefore(5f);

        rightCell.addElement(title);
        rightCell.addElement(datePara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add decorative separator
        addSeparator(document);
    }


    private void addEmployeeSummarySection(Document document, List<StationMaster> stationMasters,
                                           List<Counter> counters, List<Employee> employees) throws DocumentException {
        // Calculate statistics for each employee type
        long totalStationMasters = stationMasters.size();
        long activeStationMasters = stationMasters.stream().filter(StationMaster::isActive).count();

        long totalCounters = counters.size();
        long activeCounters = counters.stream().filter(Counter::isActive).count();

        long totalEmployees = employees.size();
        long activeEmployees = employees.stream().filter(Employee::isActive).count();

        // Calculate overall totals
        long totalAllEmployees = totalStationMasters + totalCounters + totalEmployees;
        long activeAllEmployees = activeStationMasters + activeCounters + activeEmployees;
        long inactiveAllEmployees = totalAllEmployees - activeAllEmployees;

        // Calculate average experience for each category
        double avgExpStationMasters = stationMasters.stream()
                .mapToInt(StationMaster::getYearsOfExperience)
                .average().orElse(0.0);

        double avgExpCounters = counters.stream()
                .mapToInt(Counter::getYearsOfExperience)
                .average().orElse(0.0);

        // For regular employees, calculate experience from joining date
        double avgExpEmployees = employees.stream()
                .mapToInt(e -> {
                    try {
                        LocalDate joiningDate = e.getJoiningDate(); // Already LocalDate
                        return Period.between(joiningDate, LocalDate.now()).getYears();
                    } catch (Exception ex) {
                        return 0;
                    }
                })
                .average().orElse(0.0);
        // Calculate overall average experience
        double avgExpAll = (avgExpStationMasters * totalStationMasters +
                avgExpCounters * totalCounters +
                avgExpEmployees * totalEmployees) / totalAllEmployees;

        // Create summary table
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(15f);
        summaryTable.setWidths(new float[]{1, 1, 1, 1});

        // Summary header
        Font summaryHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("EMPLOYEE SUMMARY", summaryHeaderFont));
        headerCell.setBackgroundColor(PRIMARY_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setColspan(4);
        headerCell.setPadding(8);
        headerCell.setBorderWidth(0);
        summaryTable.addCell(headerCell);

        // Add general statistics
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);

        // Total Employees
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total Employees", labelFont));
        totalLabelCell.setBackgroundColor(HEADER_BG);
        totalLabelCell.setPadding(6);
        totalLabelCell.setBorderWidth(0.5f);
        totalLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.valueOf(totalAllEmployees), valueFont));
        totalValueCell.setPadding(6);
        totalValueCell.setBorderWidth(0.5f);
        totalValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(totalValueCell);

        // Active Employees
        PdfPCell activeLabelCell = new PdfPCell(new Phrase("Active Employees", labelFont));
        activeLabelCell.setBackgroundColor(HEADER_BG);
        activeLabelCell.setPadding(6);
        activeLabelCell.setBorderWidth(0.5f);
        activeLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeLabelCell);

        PdfPCell activeValueCell = new PdfPCell(new Phrase(String.valueOf(activeAllEmployees), valueFont));
        activeValueCell.setPadding(6);
        activeValueCell.setBorderWidth(0.5f);
        activeValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(activeValueCell);

        // Inactive Employees
        PdfPCell inactiveLabelCell = new PdfPCell(new Phrase("Inactive Employees", labelFont));
        inactiveLabelCell.setBackgroundColor(HEADER_BG);
        inactiveLabelCell.setPadding(6);
        inactiveLabelCell.setBorderWidth(0.5f);
        inactiveLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveLabelCell);

        PdfPCell inactiveValueCell = new PdfPCell(new Phrase(String.valueOf(inactiveAllEmployees), valueFont));
        inactiveValueCell.setPadding(6);
        inactiveValueCell.setBorderWidth(0.5f);
        inactiveValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(inactiveValueCell);

        // Average Experience
        PdfPCell avgExpLabelCell = new PdfPCell(new Phrase("Avg. Experience (Years)", labelFont));
        avgExpLabelCell.setBackgroundColor(HEADER_BG);
        avgExpLabelCell.setPadding(6);
        avgExpLabelCell.setBorderWidth(0.5f);
        avgExpLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(avgExpLabelCell);

        PdfPCell avgExpValueCell = new PdfPCell(new Phrase(String.format("%.1f", avgExpAll), valueFont));
        avgExpValueCell.setPadding(6);
        avgExpValueCell.setBorderWidth(0.5f);
        avgExpValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(avgExpValueCell);

        // Employee type distribution header
        PdfPCell typeHeaderCell = new PdfPCell(new Phrase("EMPLOYEE TYPE DISTRIBUTION", labelFont));
        typeHeaderCell.setBackgroundColor(SECONDARY_BLUE);
        typeHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        typeHeaderCell.setPadding(6);
        typeHeaderCell.setBorderWidth(0.5f);
        typeHeaderCell.setBorderColor(Color.LIGHT_GRAY);
        typeHeaderCell.setColspan(4);
        summaryTable.addCell(typeHeaderCell);

        // Station Masters
        PdfPCell smLabelCell = new PdfPCell(new Phrase("Station Masters", labelFont));
        smLabelCell.setBackgroundColor(HEADER_BG);
        smLabelCell.setPadding(6);
        smLabelCell.setBorderWidth(0.5f);
        smLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(smLabelCell);

        PdfPCell smValueCell = new PdfPCell(new Phrase(String.valueOf(totalStationMasters), valueFont));
        smValueCell.setPadding(6);
        smValueCell.setBorderWidth(0.5f);
        smValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(smValueCell);

        PdfPCell smActiveLabelCell = new PdfPCell(new Phrase("Active Station Masters", labelFont));
        smActiveLabelCell.setBackgroundColor(HEADER_BG);
        smActiveLabelCell.setPadding(6);
        smActiveLabelCell.setBorderWidth(0.5f);
        smActiveLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(smActiveLabelCell);

        PdfPCell smActiveValueCell = new PdfPCell(new Phrase(String.valueOf(activeStationMasters), valueFont));
        smActiveValueCell.setPadding(6);
        smActiveValueCell.setBorderWidth(0.5f);
        smActiveValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(smActiveValueCell);

        // Counter Staff
        PdfPCell counterLabelCell = new PdfPCell(new Phrase("Counter Staff", labelFont));
        counterLabelCell.setBackgroundColor(HEADER_BG);
        counterLabelCell.setPadding(6);
        counterLabelCell.setBorderWidth(0.5f);
        counterLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(counterLabelCell);

        PdfPCell counterValueCell = new PdfPCell(new Phrase(String.valueOf(totalCounters), valueFont));
        counterValueCell.setPadding(6);
        counterValueCell.setBorderWidth(0.5f);
        counterValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(counterValueCell);

        PdfPCell counterActiveLabelCell = new PdfPCell(new Phrase("Active Counter Staff", labelFont));
        counterActiveLabelCell.setBackgroundColor(HEADER_BG);
        counterActiveLabelCell.setPadding(6);
        counterActiveLabelCell.setBorderWidth(0.5f);
        counterActiveLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(counterActiveLabelCell);

        PdfPCell counterActiveValueCell = new PdfPCell(new Phrase(String.valueOf(activeCounters), valueFont));
        counterActiveValueCell.setPadding(6);
        counterActiveValueCell.setBorderWidth(0.5f);
        counterActiveValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(counterActiveValueCell);

        // Regular Employees
        PdfPCell empLabelCell = new PdfPCell(new Phrase("Regular Employees", labelFont));
        empLabelCell.setBackgroundColor(HEADER_BG);
        empLabelCell.setPadding(6);
        empLabelCell.setBorderWidth(0.5f);
        empLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(empLabelCell);

        PdfPCell empValueCell = new PdfPCell(new Phrase(String.valueOf(totalEmployees), valueFont));
        empValueCell.setPadding(6);
        empValueCell.setBorderWidth(0.5f);
        empValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(empValueCell);

        PdfPCell empActiveLabelCell = new PdfPCell(new Phrase("Active Regular Employees", labelFont));
        empActiveLabelCell.setBackgroundColor(HEADER_BG);
        empActiveLabelCell.setPadding(6);
        empActiveLabelCell.setBorderWidth(0.5f);
        empActiveLabelCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(empActiveLabelCell);

        PdfPCell empActiveValueCell = new PdfPCell(new Phrase(String.valueOf(activeEmployees), valueFont));
        empActiveValueCell.setPadding(6);
        empActiveValueCell.setBorderWidth(0.5f);
        empActiveValueCell.setBorderColor(Color.LIGHT_GRAY);
        summaryTable.addCell(empActiveValueCell);

        document.add(summaryTable);
    }


    @Override
    public ByteArrayOutputStream generateTicketPdf(String bookingId) {
        BookingDto bookingDetailsByBookingId = bookingService.getBookingDetailsByBookingId(bookingId);
        System.out.println("Booking details: " + bookingDetailsByBookingId);

        Document document = new Document(new Rectangle(226, 450));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Add ticket content
            addTicketHeader(document, bookingDetailsByBookingId);
            addJourneyDetails(document, bookingDetailsByBookingId);
            addPassengerDetails(document, bookingDetailsByBookingId.getPayeeInfo());
            addBookingDetails(document, bookingDetailsByBookingId);
            addTicketFooter(document, bookingDetailsByBookingId);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating ticket PDF", e);
        }

        return out;
    }

    private void addTicketHeader(Document document, BookingDto booking) throws DocumentException {
        // Header table
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(15f);

        // Left cell - Logo and title
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPaddingBottom(10f);

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, PRIMARY_BLUE);
        Paragraph title = new Paragraph("RAIL LANKA PRO", titleFont);
        title.setAlignment(Element.ALIGN_LEFT);

        Font subtitleFont = new Font(Font.HELVETICA, 8, Font.NORMAL, TEXT_LIGHT);
        Paragraph subtitle = new Paragraph("Electronic Travel Ticket", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_LEFT);
        subtitle.setSpacingBefore(2f);

        leftCell.addElement(title);
        leftCell.addElement(subtitle);

        // Right cell - Booking ID and QR code area
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font idFont = new Font(Font.HELVETICA, 9, Font.BOLD, TEXT_DARK);
        Paragraph bookingId = new Paragraph(booking.getBookingId(), idFont);
        bookingId.setAlignment(Element.ALIGN_RIGHT);

        Font statusFont = new Font(Font.HELVETICA, 8, Font.BOLD, ACCENT_GREEN);
        Paragraph status = new Paragraph("CONFIRMED", statusFont);
        status.setAlignment(Element.ALIGN_RIGHT);
        status.setSpacingBefore(2f);

        rightCell.addElement(bookingId);
        rightCell.addElement(status);

        // Add QR code placeholder
        addQrCodePlaceholder(rightCell, booking.getBookingId());

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add separator line
        addSeparator(document, 1f, PRIMARY_BLUE);
    }

    private void addQrCodePlaceholder(PdfPCell cell, String bookingId) {
        try {
            // Create a simple QR code placeholder
            Font qrFont = new Font(Font.HELVETICA, 6, Font.NORMAL, TEXT_LIGHT);
            Paragraph qrText = new Paragraph("[QR Code]", qrFont);
            qrText.setAlignment(Element.ALIGN_CENTER);
            qrText.setSpacingBefore(5f);

            cell.addElement(qrText);

            // Add booking ID below QR placeholder
            Font smallFont = new Font(Font.HELVETICA, 5, Font.NORMAL, TEXT_LIGHT);
            Paragraph idText = new Paragraph(bookingId, smallFont);
            idText.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(idText);

        } catch (Exception e) {
            // Fallback if QR code generation fails
            System.out.println("QR code generation placeholder added");
        }
    }

    private void addJourneyDetails(Document document, BookingDto booking) throws DocumentException {
        // Journey section header
        Font sectionFont = new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_BLUE);
        Paragraph sectionHeader = new Paragraph("JOURNEY DETAILS", sectionFont);
        sectionHeader.setSpacingBefore(10f);
        sectionHeader.setSpacingAfter(5f);
        document.add(sectionHeader);

        // Route information
        PdfPTable routeTable = new PdfPTable(2);
        routeTable.setWidthPercentage(100);
        routeTable.setSpacingAfter(10f);

        addRouteCell(routeTable, "FROM", booking.getDepartureStation(), true);
        addRouteCell(routeTable, "TO", booking.getDestinationStation(), false);

        // Add train icon between stations
        PdfPCell iconCell = new PdfPCell();
        iconCell.setBorder(Rectangle.NO_BORDER);
        iconCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        iconCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font iconFont = new Font(Font.HELVETICA, 8, Font.NORMAL, SECONDARY_BLUE);
        Paragraph trainIcon = new Paragraph("⇨", iconFont);
        trainIcon.setAlignment(Element.ALIGN_CENTER);
        iconCell.addElement(trainIcon);

        routeTable.addCell(iconCell);

        document.add(routeTable);

        // Train and timing details
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingAfter(15f);

        addDetailRow(detailsTable, "Train", booking.getTrainName());
        addDetailRow(detailsTable, "Class", booking.getTravelClass());
        addDetailRow(detailsTable, "Date", booking.getFormattedTravelDate());
        addDetailRow(detailsTable, "Departure", booking.getDepartureTime());
        addDetailRow(detailsTable, "Arrival", booking.getArrivalTime());
//        addDetailRow(detailsTable, "Adult Count", String.valueOf(booking.getAdultCount()));
//        addDetailRow(detailsTable, "Child Count", String.valueOf(booking.getChildCount()));
//        addDetailRow(detailsTable, "Total Valid Passengers", String.valueOf(booking.getChildCount()+booking.getAdultCount()));


        document.add(detailsTable);

        addSeparator(document, 0.5f, BORDER_COLOR);
    }

    private void addRouteCell(PdfPTable table, String label, String station, boolean isDeparture) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5f);

        Font labelFont = new Font(Font.HELVETICA, 7, Font.BOLD, TEXT_LIGHT);
        Font stationFont = new Font(Font.HELVETICA, 10, Font.BOLD, TEXT_DARK);

        Paragraph labelPara = new Paragraph(label, labelFont);
        Paragraph stationPara = new Paragraph(station.toUpperCase(), stationFont);
        stationPara.setSpacingBefore(2f);

        cell.addElement(labelPara);
        cell.addElement(stationPara);

        table.addCell(cell);
    }

    private void addDetailRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.HELVETICA, 8, Font.BOLD, TEXT_LIGHT);
        Font valueFont = new Font(Font.HELVETICA, 8, Font.NORMAL, TEXT_DARK);

        PdfPCell labelCell = new PdfPCell(new Phrase(label + ":", labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3f);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3f);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addPassengerDetails(Document document, PayeeInfoDto payeeInfo) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_BLUE);
        Paragraph sectionHeader = new Paragraph("PASSENGER INFORMATION", sectionFont);
        sectionHeader.setSpacingBefore(10f);
        sectionHeader.setSpacingAfter(5f);
        document.add(sectionHeader);

        PdfPTable passengerTable = new PdfPTable(2);
        passengerTable.setWidthPercentage(100);
        passengerTable.setSpacingAfter(15f);

        addDetailRow(passengerTable, "Name", payeeInfo.getFirstName());
        addDetailRow(passengerTable, "ID Type", "NIC");
        addDetailRow(passengerTable, "ID Number", payeeInfo.getNicOrPassport());

        document.add(passengerTable);

        addSeparator(document, 0.5f, BORDER_COLOR);
    }

    private void addBookingDetails(Document document, BookingDto booking) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_BLUE);
        Paragraph sectionHeader = new Paragraph("BOOKING DETAILS", sectionFont);
        sectionHeader.setSpacingBefore(10f);
        sectionHeader.setSpacingAfter(5f);
        document.add(sectionHeader);

        PdfPTable bookingTable = new PdfPTable(2);
        bookingTable.setWidthPercentage(100);
        bookingTable.setSpacingAfter(10f);

        addDetailRow(bookingTable, "Adults", String.valueOf(booking.getAdultCount()));
        addDetailRow(bookingTable, "Children", String.valueOf(booking.getChildCount()));

        if (booking.getFormatedselectedSeat() != null &&
                !booking.getFormatedselectedSeat().equals("No online booking available for this Class!")) {
            addDetailRow(bookingTable, "Seats", booking.getFormatedselectedSeat());
        }

        document.add(bookingTable);

        // Total amount with emphasis
        PdfPTable amountTable = new PdfPTable(1);
        amountTable.setWidthPercentage(100);

        PdfPCell amountCell = new PdfPCell();
        amountCell.setBorder(Rectangle.NO_BORDER);
        amountCell.setBackgroundColor(LIGHT_BG);
        amountCell.setPadding(8f);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font amountLabelFont = new Font(Font.HELVETICA, 8, Font.BOLD, TEXT_LIGHT);
        Font amountFont = new Font(Font.HELVETICA, 12, Font.BOLD, PRIMARY_BLUE);

        Paragraph amountLabel = new Paragraph("TOTAL AMOUNT", amountLabelFont);
        amountLabel.setAlignment(Element.ALIGN_CENTER);

        Paragraph amount = new Paragraph(booking.getFormattedTotalAmount(), amountFont);
        amount.setAlignment(Element.ALIGN_CENTER);
        amount.setSpacingBefore(2f);

        amountCell.addElement(amountLabel);
        amountCell.addElement(amount);

        amountTable.addCell(amountCell);
        document.add(amountTable);

        addSeparator(document, 0.5f, BORDER_COLOR);
    }

    private void addTicketFooter(Document document, BookingDto booking) throws DocumentException {
        Font footerFont = new Font(Font.HELVETICA, 6, Font.NORMAL, TEXT_LIGHT);

        Paragraph terms = new Paragraph(
                "• This ticket is valid only for the specified journey and selected total of passengers count\n" +
                        "• Please carry valid ID proof for verification\n" +
                        "• Boarding begins 30 minutes before departure\n" +
                        "• Ticket non-transferable and non-refundable", footerFont);
        terms.setSpacingBefore(10f);
        document.add(terms);

        // Generated timestamp
        String generatedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Paragraph generated = new Paragraph("Generated: " + generatedTime, footerFont);
        generated.setAlignment(Element.ALIGN_CENTER);
        generated.setSpacingBefore(8f);
        document.add(generated);

        // Contact information
        Paragraph contact = new Paragraph(
                "For assistance: support@raillanka.lk | +94 11 234 5678", footerFont);
        contact.setAlignment(Element.ALIGN_CENTER);
        contact.setSpacingBefore(3f);
        document.add(contact);
    }


    private void addSeparator(Document document, float thickness, Color color) throws DocumentException {
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100);
        separator.setSpacingBefore(5f);
        separator.setSpacingAfter(5f);

        PdfPCell lineCell = new PdfPCell();
        lineCell.setFixedHeight(thickness);
        lineCell.setBackgroundColor(color);
        lineCell.setBorder(Rectangle.NO_BORDER);

        separator.addCell(lineCell);
        document.add(separator);
    }






    private void addPassengerDocumentHeader(Document document, String titleText) throws DocumentException {
        // Create header table
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Logo and company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Try to add logo (if available)
        try {
            URL logoUrl = getClass().getResource("/images/rail-lanka-logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 50);
                leftCell.addElement(logo);

            } else {
                // Create text-based logo
                Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
                Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
                leftCell.addElement(logoText);
            }
        } catch (Exception e) {
            // Fallback to text logo
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
            Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
            leftCell.addElement(logoText);
        }

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);
        Paragraph companyInfo = new Paragraph("Sri Lanka Railway Department\nColombo, Sri Lanka", companyFont);
        companyInfo.setSpacingBefore(5f);
        leftCell.addElement(companyInfo);

        // Right side - Report title and details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_BLUE);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        String dateString = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph datePara = new Paragraph("Generated on: " + dateString, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingBefore(5f);

        rightCell.addElement(title);
        rightCell.addElement(datePara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add decorative separator
        addSeparator(document);
    }

    private void addPassengerSummarySection(Document document, List<Passenger> passengers) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(3);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(15f);
        summaryTable.setWidths(new float[]{1, 1, 1});

        // Summary header
        Font summaryHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("PASSENGER SUMMARY", summaryHeaderFont));
        headerCell.setBackgroundColor(PRIMARY_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setColspan(3);
        headerCell.setPadding(8);
        headerCell.setBorderWidth(0);
        summaryTable.addCell(headerCell);

        long blockedCount = passengers.stream().filter(Passenger::isBlocked).count();

        String[] labels = {"Total Passengers", "Blocked Accounts", "Active Accounts"};
        String[] values = {
                String.valueOf(passengers.size()),
                String.valueOf(blockedCount),
                String.valueOf(passengers.size() - blockedCount)
        };

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);

        for (int i = 0; i < labels.length; i++) {
            PdfPCell labelCell = new PdfPCell(new Phrase(labels[i], labelFont));
            labelCell.setBackgroundColor(HEADER_BG);
            labelCell.setPadding(6);
            labelCell.setBorderWidth(0.5f);
            labelCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(labelCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(values[i], valueFont));
            valueCell.setPadding(6);
            valueCell.setBorderWidth(0.5f);
            valueCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(valueCell);
        }

        document.add(summaryTable);
    }

    private void addPassengerTable(Document document, List<Passenger> passengers) throws DocumentException {
        if (passengers.isEmpty()) {
            Font emptyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_LIGHT);
            Paragraph empty = new Paragraph("No passengers found.", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            empty.setSpacingBefore(20f);
            document.add(empty);
            return;
        }

        // Create table with enhanced design
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{1, 1, 1.5f, 1.5f, 1.2f, 1.5f, 2f, 2f, 1});

        // Table header with enhanced styling
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headers = {"ID", "Title", "First Name", "Last Name", "Type", "ID Type", "ID Number", "Contact", "Status"};

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(SECONDARY_BLUE);
            headerCell.setPadding(8);
            headerCell.setBorderWidth(0);
            headerCell.setBorderColor(Color.WHITE);
            headerCell.setBorderWidthRight(1);
            headerCell.setBorderColorRight(Color.WHITE);
            table.addCell(headerCell);
        }

        // Data rows with enhanced styling
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

        boolean alternate = false;
        int rowCount = 0;

        for (Passenger passenger : passengers) {
            Color rowColor = alternate ? LIGHT_BG : Color.WHITE;
            alternate = !alternate;
            rowCount++;

            addStyledCell(table, passenger.getPassengerId(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, passenger.getTitle(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, passenger.getFirstName(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, passenger.getLastName(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, passenger.getPassengerType().toString(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, passenger.getIdtype().toString(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, passenger.getIdNumber(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, passenger.getPhoneNumber(), rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, passenger.isBlocked() ? "Blocked" : "Active", rowColor, dataFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Add row count summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        Paragraph summary = new Paragraph(
                "Total passengers displayed: " + rowCount, summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(10f);
        document.add(summary);
    }

    private void addDocumentHeader(Document document, Station station) throws DocumentException {
        // Create header table
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Logo and company info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        // Try to add logo (if available)
        try {
            URL logoUrl = getClass().getResource("/images/rail-lanka-logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(120, 50);
                leftCell.addElement(logo);

            } else {
                // Create text-based logo
                Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
                Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
                leftCell.addElement(logoText);
            }
        } catch (Exception e) {
            // Fallback to text logo
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_BLUE);
            Paragraph logoText = new Paragraph("RAIL LANKA PRO", logoFont);
            leftCell.addElement(logoText);
        }

        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);
        Paragraph companyInfo = new Paragraph("Sri Lanka Railway Department\nColombo, Sri Lanka", companyFont);
        companyInfo.setSpacingBefore(5f);
        leftCell.addElement(companyInfo);

        // Right side - Report title and details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_BLUE);
        Paragraph title = new Paragraph("EMPLOYEE DIRECTORY", titleFont);
        title.setAlignment(Element.ALIGN_RIGHT);

        Font stationFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, SECONDARY_BLUE);
        Paragraph stationPara = new Paragraph(station.getName() + " Station", stationFont);
        stationPara.setAlignment(Element.ALIGN_RIGHT);
        stationPara.setSpacingBefore(5f);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        String dateString = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph datePara = new Paragraph("Generated on: " + dateString, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        datePara.setSpacingBefore(5f);

        rightCell.addElement(title);
        rightCell.addElement(stationPara);
        rightCell.addElement(datePara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Add decorative separator
        addSeparator(document);
    }

    private void addSummarySection(Document document, List<StaffProjection> staffProjections, Station station) throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(15f);
        summaryTable.setWidths(new float[]{1, 1, 1, 1});

        // Summary header
        Font summaryHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("STATION SUMMARY", summaryHeaderFont));
        headerCell.setBackgroundColor(PRIMARY_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setColspan(4);
        headerCell.setPadding(8);
        headerCell.setBorderWidth(0);
        summaryTable.addCell(headerCell);

        long activeStaffCount = staffProjections.stream()
                .filter(s -> "true".equals(s.getStatus())) // active status check
                .count();

        String[] labels = {"Total Employees", "Active Staff", "Station Location", "Contact Info"};
        String[] values = {
                String.valueOf(staffProjections.size()),
                String.valueOf(activeStaffCount), // Assuming all are active
                staffProjections.getFirst().getLocation() != null ? staffProjections.getFirst().getLocation() : "Not specified",
                "Please contact Station Master!"
        };

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_LIGHT);

        for (int i = 0; i < labels.length; i++) {
            PdfPCell labelCell = new PdfPCell(new Phrase(labels[i], labelFont));
            labelCell.setBackgroundColor(HEADER_BG);
            labelCell.setPadding(6);
            labelCell.setBorderWidth(0.5f);
            labelCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(labelCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(values[i], valueFont));
            valueCell.setPadding(6);
            valueCell.setBorderWidth(0.5f);
            valueCell.setBorderColor(Color.LIGHT_GRAY);
            summaryTable.addCell(valueCell);
        }

        document.add(summaryTable);
    }

    private void addEmployeeTable(Document document, List<StaffProjection> staffProjections) throws DocumentException {
        if (staffProjections.isEmpty()) {
            Font emptyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, TEXT_LIGHT);
            Paragraph empty = new Paragraph("No employees found for this station.", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            empty.setSpacingBefore(20f);
            document.add(empty);
            return;
        }

        // Create table with enhanced design
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{1.2f, 1.5f, 1.5f, 2f, 1.5f, 1.2f, 1.2f, 1.5f});

        // Table header with enhanced styling
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headers = {"ID", "First Name", "Last Name", "Email", "Contact No", "DOB", "Joined", "Position"};

        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setBackgroundColor(SECONDARY_BLUE);
            headerCell.setPadding(8);
            headerCell.setBorderWidth(0);
            headerCell.setBorderColor(Color.WHITE);
            headerCell.setBorderWidthRight(1);
            headerCell.setBorderColorRight(Color.WHITE);
            table.addCell(headerCell);
        }

        // Data rows with enhanced styling
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);
        Font dataFontLight = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_LIGHT);

        boolean alternate = false;
        int rowCount = 0;

        for (StaffProjection staff : staffProjections) {
            Color rowColor = alternate ? LIGHT_BG : Color.WHITE;
            alternate = !alternate;
            rowCount++;
            String[] splitName = staff.getName().split(" ");

            addStyledCell(table, staff.getId(), rowColor, dataFont, Element.ALIGN_CENTER);
            addStyledCell(table, splitName[0], rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, splitName[1], rowColor, dataFont, Element.ALIGN_LEFT);
            addStyledCell(table, staff.getEmail(), rowColor, dataFontLight, Element.ALIGN_LEFT);
            addStyledCell(table, staff.getTelephone(), rowColor, dataFont, Element.ALIGN_CENTER);

            String dob = staff.getDob() != null ?
                    staff.getDob().format(dateFormatter) : "N/A";
            addStyledCell(table, dob, rowColor, dataFont, Element.ALIGN_CENTER);

            String joinDate = staff.getJoinDate() != null ?
                    staff.getJoinDate().format(dateFormatter) : "N/A";
            addStyledCell(table, joinDate, rowColor, dataFont, Element.ALIGN_CENTER);

            addStyledCell(table, formatPosition(staff.getPosition()), rowColor, dataFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Add row count summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, TEXT_LIGHT);
        Paragraph summary = new Paragraph(
                "Total employees displayed: " + rowCount, summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(10f);
        document.add(summary);
    }

    private void addDocumentFooter(Document document) throws DocumentException {
        // Add a footer note
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, TEXT_LIGHT);
        Paragraph footerNote = new Paragraph(
                "This report contains confidential information intended only for authorized Rail Lanka personnel. " +
                        "Unauthorized distribution is prohibited.", footerFont);
        footerNote.setAlignment(Element.ALIGN_CENTER);
        footerNote.setSpacingBefore(20f);
        document.add(footerNote);
    }

    private void addSeparator(Document document) throws DocumentException {
        // Add a decorative separator line
        Paragraph separator = new Paragraph();
        separator.setSpacingBefore(10f);
        separator.setSpacingAfter(10f);

        Chunk line = new Chunk("______________________________________________________________________________" +
                "____________________________________________________________");
        line.setFont(FontFactory.getFont(FontFactory.HELVETICA, 10, SECONDARY_BLUE));
        separator.add(line);

        document.add(separator);
    }

    private void addStyledCell(PdfPTable table, String text, Color bgColor, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "N/A", font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(6);
        cell.setBackgroundColor(bgColor);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    private String formatPosition(String position) {
        if (position == null) return "N/A";

        return position.charAt(0) + position.substring(1).toLowerCase().replace("_", " ");
    }






    // Enhanced page event handler for header and footer
    class RailLankaPageEvent extends PdfPageEventHelper {

        private PdfTemplate template;
        private Image totalPages;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            template = writer.getDirectContent().createTemplate(30, 16);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Add page number footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_LIGHT);
            String text = "Page " + writer.getPageNumber() + " of ";
            float textWidth = FontFactory.getFont(FontFactory.HELVETICA, 8).getBaseFont().getWidthPoint(text, 8);
            float x = (document.right() + document.left()) / 2;
            float y = document.bottom() - 25;

            cb.beginText();
            cb.setFontAndSize(FontFactory.getFont(FontFactory.HELVETICA, 8).getBaseFont(), 8);
            cb.setColorFill(TEXT_LIGHT);
            cb.showTextAligned(Element.ALIGN_CENTER, text, x, y, 0);
            cb.endText();

            cb.addTemplate(template, x + textWidth, y);

            // Add decorative footer line
            cb.setColorStroke(SECONDARY_BLUE);
            cb.setLineWidth(0.5f);
            cb.moveTo(document.left(), document.bottom() - 15);
            cb.lineTo(document.right(), document.bottom() - 15);
            cb.stroke();

            // Add company name in footer
            cb.beginText();
            cb.setFontAndSize(FontFactory.getFont(FontFactory.HELVETICA, 8).getBaseFont(), 8);
            cb.setColorFill(PRIMARY_BLUE);
            cb.showTextAligned(Element.ALIGN_LEFT, "Rail Lanka Management System",
                    document.left(), document.bottom() - 30, 0);
            cb.endText();

            cb.beginText();
            cb.setFontAndSize(FontFactory.getFont(FontFactory.HELVETICA, 7).getBaseFont(), 7);
            cb.setColorFill(TEXT_LIGHT);
            cb.showTextAligned(Element.ALIGN_RIGHT, "Confidential - For Internal Use Only",
                    document.right(), document.bottom() - 30, 0);
            cb.endText();
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            // Add total page count when document is closed
            Font font = FontFactory.getFont(FontFactory.HELVETICA, 8);
            ColumnText.showTextAligned(template, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), font), 0, 0, 0);
        }
    }
}