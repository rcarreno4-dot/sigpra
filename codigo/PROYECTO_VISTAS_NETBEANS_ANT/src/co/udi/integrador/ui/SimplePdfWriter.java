package co.udi.integrador.ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Generador PDF basico sin dependencias externas.
 */
public final class SimplePdfWriter {
    private static final int PAGE_WIDTH = 612;   // Letter
    private static final int PAGE_HEIGHT = 792;  // Letter
    private static final int MARGIN_LEFT = 50;
    private static final int MARGIN_TOP = 700;
    private static final int MARGIN_BOTTOM = 50;
    private static final int LINE_HEIGHT = 14;

    private SimplePdfWriter() {
    }

    public static void writeTextReport(Path output, String title, List<String> lines) throws IOException {
        writeTextReport(output, title, null, lines);
    }

    public static void writeTextReport(Path output, String title, String subtitle, List<String> lines) throws IOException {
        writeTextReportWithLogo(output, title, subtitle, lines, null);
    }

    public static void writeTextReportWithLogo(
            Path output,
            String title,
            String subtitle,
            List<String> lines,
            byte[] logoJpeg
    ) throws IOException {
        List<String> wrapped = new ArrayList<>();
        if (title != null && !title.isBlank()) {
            wrapped.add(title);
        }
        if (subtitle != null && !subtitle.isBlank()) {
            wrapped.add(subtitle);
        }
        if ((title != null && !title.isBlank()) || (subtitle != null && !subtitle.isBlank())) {
            wrapped.add("");
        }
        for (String line : lines) {
            wrapped.addAll(wrapLine(line == null ? "" : line, 95));
        }

        List<List<String>> pages = paginate(wrapped);
        LogoImage logo = decodeLogo(logoJpeg);
        byte[] pdf = buildPdf(pages, logo);
        Files.write(output, pdf);
    }

    public static void writeStyledTableReportWithLogo(
            Path output,
            String title,
            String subtitle,
            List<String> metaLines,
            String[] headers,
            List<String[]> rows,
            byte[] logoJpeg
    ) throws IOException {
        LogoImage logo = decodeLogo(logoJpeg);
        byte[] pdf = buildStyledTablePdf(title, subtitle, metaLines, headers, rows, logo);
        Files.write(output, pdf);
    }

    private static List<List<String>> paginate(List<String> lines) {
        int maxLines = Math.max(1, (MARGIN_TOP - MARGIN_BOTTOM) / LINE_HEIGHT);
        List<List<String>> pages = new ArrayList<>();
        List<String> page = new ArrayList<>();
        for (String line : lines) {
            if (page.size() >= maxLines) {
                pages.add(page);
                page = new ArrayList<>();
            }
            page.add(line);
        }
        if (page.isEmpty()) {
            page.add("");
        }
        pages.add(page);
        return pages;
    }

    private static List<String> wrapLine(String line, int maxChars) {
        List<String> out = new ArrayList<>();
        if (line.length() <= maxChars) {
            out.add(line);
            return out;
        }
        String[] words = line.split("\\s+");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            if (current.isEmpty()) {
                current.append(word);
                continue;
            }
            if (current.length() + 1 + word.length() <= maxChars) {
                current.append(' ').append(word);
            } else {
                out.add(current.toString());
                current.setLength(0);
                current.append(word);
            }
        }
        if (!current.isEmpty()) {
            out.add(current.toString());
        }
        return out;
    }

    private static byte[] buildPdf(List<List<String>> pages, LogoImage logo) throws IOException {
        List<byte[]> objects = new ArrayList<>();
        int pageCount = pages.size();
        int firstPageObj = 3;
        int fontObjNum = firstPageObj + (pageCount * 2);
        int logoObjNum = logo == null ? -1 : fontObjNum + 1;

        objects.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pageCount; i++) {
            int pageObj = firstPageObj + (i * 2);
            kids.append(pageObj).append(" 0 R ");
        }
        objects.add(ascii("<< /Type /Pages /Count " + pageCount + " /Kids [" + kids + "] >>"));

        for (int i = 0; i < pageCount; i++) {
            int pageObj = firstPageObj + (i * 2);
            int contentObj = pageObj + 1;
            String resources = "<< /Font << /F1 " + fontObjNum + " 0 R >>";
            if (logoObjNum > 0) {
                resources += " /XObject << /Im1 " + logoObjNum + " 0 R >>";
            }
            resources += " >>";
            String page = "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT + "] "
                    + "/Resources " + resources + " /Contents " + contentObj + " 0 R >>";
            objects.add(ascii(page));

            byte[] stream = buildContentStream(pages.get(i), i + 1, pageCount, logo);
            byte[] header = ascii("<< /Length " + stream.length + " >>\nstream\n");
            byte[] footer = ascii("\nendstream");
            ByteArrayOutputStream contentObjBytes = new ByteArrayOutputStream();
            contentObjBytes.write(header);
            contentObjBytes.write(stream);
            contentObjBytes.write(footer);
            objects.add(contentObjBytes.toByteArray());
        }

        objects.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));
        if (logo != null) {
            byte[] header = ascii("<< /Type /XObject /Subtype /Image /Width " + logo.width()
                    + " /Height " + logo.height()
                    + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length "
                    + logo.jpegBytes().length + " >>\nstream\n");
            byte[] footer = ascii("\nendstream");
            ByteArrayOutputStream imageObjBytes = new ByteArrayOutputStream();
            imageObjBytes.write(header);
            imageObjBytes.write(logo.jpegBytes());
            imageObjBytes.write(footer);
            objects.add(imageObjBytes.toByteArray());
        }

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        pdf.write(ascii("%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n"));

        List<Integer> offsets = new ArrayList<>();
        offsets.add(0); // objeto 0 libre
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.size());
            int objNum = i + 1;
            pdf.write(ascii(objNum + " 0 obj\n"));
            pdf.write(objects.get(i));
            pdf.write(ascii("\nendobj\n"));
        }

        int xrefStart = pdf.size();
        pdf.write(ascii("xref\n0 " + (objects.size() + 1) + "\n"));
        pdf.write(ascii("0000000000 65535 f \n"));
        for (int i = 1; i < offsets.size(); i++) {
            String line = String.format("%010d 00000 n \n", offsets.get(i));
            pdf.write(ascii(line));
        }
        pdf.write(ascii("trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\n"));
        pdf.write(ascii("startxref\n" + xrefStart + "\n%%EOF"));
        return pdf.toByteArray();
    }

    private static byte[] buildContentStream(List<String> lines, int page, int totalPages, LogoImage logo) {
        StringBuilder sb = new StringBuilder();
        if (logo != null) {
            int drawWidth = 150;
            int drawHeight = Math.max(1, (int) Math.round((double) drawWidth * logo.height() / logo.width()));
            int x = PAGE_WIDTH - MARGIN_LEFT - drawWidth;
            int y = PAGE_HEIGHT - 58;
            sb.append("q\n")
                    .append(drawWidth).append(" 0 0 ").append(drawHeight).append(' ')
                    .append(x).append(' ').append(y).append(" cm\n")
                    .append("/Im1 Do\nQ\n");
        }
        sb.append("BT\n/F1 11 Tf\n");
        sb.append(MARGIN_LEFT).append(' ').append(MARGIN_TOP).append(" Td\n");

        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append("0 -").append(LINE_HEIGHT).append(" Td\n");
            }
            sb.append('(').append(escapePdf(lines.get(i))).append(") Tj\n");
        }
        sb.append("ET");
        sb.append("\nBT\n/F1 9 Tf\n500 30 Td\n(")
                .append(escapePdf("Pagina " + page + " de " + totalPages))
                .append(") Tj\nET");
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private static String escapePdf(String text) {
        String escaped = text.replace("\\", "\\\\");
        escaped = escaped.replace("(", "\\(").replace(")", "\\)");
        return escaped;
    }

    private static byte[] buildStyledTablePdf(
            String title,
            String subtitle,
            List<String> metaLines,
            String[] headers,
            List<String[]> rows,
            LogoImage logo
    ) throws IOException {
        List<byte[]> objects = new ArrayList<>();
        int fontObjNum = 3;
        int logoObjNum = logo == null ? -1 : 4;

        objects.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));
        objects.add(ascii("<< /Type /Pages /Count 1 /Kids [5 0 R] >>"));
        objects.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));
        if (logo != null) {
            byte[] header = ascii("<< /Type /XObject /Subtype /Image /Width " + logo.width()
                    + " /Height " + logo.height()
                    + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length "
                    + logo.jpegBytes().length + " >>\nstream\n");
            byte[] footer = ascii("\nendstream");
            ByteArrayOutputStream imageObjBytes = new ByteArrayOutputStream();
            imageObjBytes.write(header);
            imageObjBytes.write(logo.jpegBytes());
            imageObjBytes.write(footer);
            objects.add(imageObjBytes.toByteArray());
        }

        String resources = "<< /Font << /F1 " + fontObjNum + " 0 R >>";
        if (logoObjNum > 0) {
            resources += " /XObject << /Im1 " + logoObjNum + " 0 R >>";
        }
        resources += " >>";
        objects.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT
                + "] /Resources " + resources + " /Contents 6 0 R >>"));

        byte[] stream = buildStyledTableContentStream(title, subtitle, metaLines, headers, rows, logo);
        byte[] header = ascii("<< /Length " + stream.length + " >>\nstream\n");
        byte[] footer = ascii("\nendstream");
        ByteArrayOutputStream contentObjBytes = new ByteArrayOutputStream();
        contentObjBytes.write(header);
        contentObjBytes.write(stream);
        contentObjBytes.write(footer);
        objects.add(contentObjBytes.toByteArray());

        return assemblePdf(objects);
    }

    private static byte[] buildStyledTableContentStream(
            String title,
            String subtitle,
            List<String> metaLines,
            String[] headers,
            List<String[]> rows,
            LogoImage logo
    ) {
        StringBuilder sb = new StringBuilder();

        if (logo != null) {
            int drawWidth = 120;
            int drawHeight = Math.max(1, (int) Math.round((double) drawWidth * logo.height() / logo.width()));
            int x = PAGE_WIDTH - MARGIN_LEFT - drawWidth;
            int y = PAGE_HEIGHT - 50 - drawHeight;
            sb.append("q\n")
                    .append(drawWidth).append(" 0 0 ").append(drawHeight).append(' ')
                    .append(x).append(' ').append(y).append(" cm\n")
                    .append("/Im1 Do\nQ\n");
        }

        // Titles
        sb.append("0.35 0.56 0.78 rg\n");
        appendText(sb, 22, MARGIN_LEFT, PAGE_HEIGHT - 54, title == null ? "" : title);
        sb.append("0 0 0 rg\n");
        if (subtitle != null && !subtitle.isBlank()) {
            appendText(sb, 14, MARGIN_LEFT, PAGE_HEIGHT - 74, subtitle);
        }

        int y = PAGE_HEIGHT - 104;
        if (metaLines != null) {
            for (String meta : metaLines) {
                appendText(sb, 11, MARGIN_LEFT, y, meta == null ? "" : meta);
                y -= 16;
            }
        }

        int tableX = 40;
        int tableW = PAGE_WIDTH - 80;
        int headerH = 20;
        int rowH = 18;
        int colCount = Math.max(1, headers == null ? 1 : headers.length);
        double colW = (double) tableW / colCount;

        int tableTop = y - 10;
        int available = tableTop - MARGIN_BOTTOM - 40;
        int maxRows = Math.max(1, available / rowH - 1);
        int renderRows = Math.min(rows == null ? 0 : rows.size(), maxRows);

        // Header background
        int headerY = tableTop - headerH;
        sb.append("0.17 0.25 0.37 rg\n");
        sb.append(tableX).append(' ').append(headerY).append(' ').append(tableW).append(' ').append(headerH).append(" re f\n");

        // Alternating row background
        for (int r = 0; r < renderRows; r++) {
            int ry = headerY - ((r + 1) * rowH);
            if (r % 2 == 0) {
                sb.append("0.96 0.97 0.99 rg\n");
            } else {
                sb.append("0.92 0.95 0.98 rg\n");
            }
            sb.append(tableX).append(' ').append(ry).append(' ').append(tableW).append(' ').append(rowH).append(" re f\n");
        }

        // Grid lines
        sb.append("0.55 0.62 0.70 RG\n0.8 w\n");
        int tableBottom = headerY - (renderRows * rowH);
        sb.append(tableX).append(' ').append(tableBottom).append(' ').append(tableW).append(' ')
                .append(headerH + (renderRows * rowH)).append(" re S\n");

        for (int c = 1; c < colCount; c++) {
            int cx = tableX + (int) Math.round(c * colW);
            sb.append(cx).append(' ').append(tableBottom).append(" m ")
                    .append(cx).append(' ').append(tableTop).append(" l S\n");
        }
        for (int r = 0; r <= renderRows; r++) {
            int ly = headerY - (r * rowH);
            sb.append(tableX).append(' ').append(ly).append(" m ")
                    .append(tableX + tableW).append(' ').append(ly).append(" l S\n");
        }

        // Header text
        sb.append("1 1 1 rg\n");
        for (int c = 0; c < colCount; c++) {
            String h = headers == null || c >= headers.length ? "" : headers[c];
            int tx = tableX + (int) Math.round(c * colW) + 3;
            int ty = headerY + 6;
            appendText(sb, 9, tx, ty, cut(h, (int) (colW / 4.8)));
        }

        // Body text
        sb.append("0 0 0 rg\n");
        for (int r = 0; r < renderRows; r++) {
            String[] row = rows.get(r);
            int ty = headerY - (r * rowH) - 12;
            for (int c = 0; c < colCount; c++) {
                String value = (row != null && c < row.length && row[c] != null) ? row[c] : "";
                int tx = tableX + (int) Math.round(c * colW) + 3;
                appendText(sb, 9, tx, ty, cut(value, (int) (colW / 4.8)));
            }
        }

        // Footer
        appendText(sb, 9, MARGIN_LEFT, MARGIN_BOTTOM + 20, "Pagina 1 de 1");
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private static void appendText(StringBuilder sb, int size, int x, int y, String text) {
        sb.append("BT\n/F1 ").append(size).append(" Tf\n")
                .append(x).append(' ').append(y).append(" Td\n(")
                .append(escapePdf(text == null ? "" : text)).append(") Tj\nET\n");
    }

    private static String cut(String s, int max) {
        if (s == null) {
            return "";
        }
        int safeMax = Math.max(1, max);
        if (s.length() <= safeMax) {
            return s;
        }
        if (safeMax <= 3) {
            return s.substring(0, safeMax);
        }
        return s.substring(0, safeMax - 3) + "...";
    }

    private static byte[] assemblePdf(List<byte[]> objects) throws IOException {
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        pdf.write(ascii("%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n"));

        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.size());
            int objNum = i + 1;
            pdf.write(ascii(objNum + " 0 obj\n"));
            pdf.write(objects.get(i));
            pdf.write(ascii("\nendobj\n"));
        }

        int xrefStart = pdf.size();
        pdf.write(ascii("xref\n0 " + (objects.size() + 1) + "\n"));
        pdf.write(ascii("0000000000 65535 f \n"));
        for (int i = 1; i < offsets.size(); i++) {
            pdf.write(ascii(String.format("%010d 00000 n \n", offsets.get(i))));
        }
        pdf.write(ascii("trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\n"));
        pdf.write(ascii("startxref\n" + xrefStart + "\n%%EOF"));
        return pdf.toByteArray();
    }

    private static byte[] ascii(String text) {
        return text.getBytes(StandardCharsets.ISO_8859_1);
    }

    private static LogoImage decodeLogo(byte[] logoJpeg) {
        if (logoJpeg == null || logoJpeg.length == 0) {
            return null;
        }
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(logoJpeg));
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                return null;
            }
            return new LogoImage(logoJpeg, image.getWidth(), image.getHeight());
        } catch (IOException ex) {
            return null;
        }
    }

    private record LogoImage(byte[] jpegBytes, int width, int height) {
    }
}
