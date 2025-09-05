package lk.ijse.raillankaprobackend.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import lk.ijse.raillankaprobackend.entity.Employee;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.entity.projection.StaffProjection;
import lk.ijse.raillankaprobackend.repository.EmployeeRepository;
import lk.ijse.raillankaprobackend.repository.StationRepository;
import lk.ijse.raillankaprobackend.service.PDFService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PDFServiceImpl implements PDFService {

    private final StationRepository stationRepository;

    // Enhanced Rail Lanka theme colors
    private static final Color PRIMARY_BLUE = new Color(30, 64, 175);
    private static final Color SECONDARY_BLUE = new Color(59, 130, 246);
    private static final Color LIGHT_BG = new Color(249, 250, 251);
    private static final Color HEADER_BG = new Color(243, 244, 246);
    private static final Color TEXT_DARK = new Color(31, 41, 55);
    private static final Color TEXT_LIGHT = new Color(107, 114, 128);

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
            cb.showTextAligned(Element.ALIGN_LEFT, "Rail Lanka Employee Management System",
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