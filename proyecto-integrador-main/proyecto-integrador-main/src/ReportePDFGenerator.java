import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportePDFGenerator {

    public static String generarComprobanteLiquidacion(
            int idLiquidacion,
            String nombreEmpleada,
            String especialidad,
            double porcentajeComision,
            String fechaInicio,
            String fechaFin,
            int totalServicios,
            double totalVentas,
            double totalComision,
            double totalSalon,
            List<Object[]> detallesServicios) throws Exception {

        String fileName = "Liquidacion_" + nombreEmpleada.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".pdf";
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        PdfWriter.getInstance(document, new FileOutputStream(fileName));

        document.open();

        // Estilos
        BaseColor rosaAremi = new BaseColor(219, 112, 147);
        BaseColor grisFondo = new BaseColor(245, 245, 245);
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, rosaAremi);
        Font fontSub = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.DARK_GRAY);
        Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        Font fontBold = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

        // Encabezado
        Paragraph pTitulo = new Paragraph("SALÓN DE BELLEZA AREMI", fontTitulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        Paragraph pSub = new Paragraph("Comprobante Oficial de Liquidación de Comisiones", fontSub);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(15);
        document.add(pSub);

        // Tabla de datos informativos
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(15);

        infoTable.addCell(createCell("N° Comprobante: LIQ-" + String.format("%05d", idLiquidacion), fontBold, grisFondo));
        infoTable.addCell(createCell("Fecha Emisión: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), fontNormal, grisFondo));

        infoTable.addCell(createCell("Empleada: " + nombreEmpleada, fontBold, BaseColor.WHITE));
        infoTable.addCell(createCell("Especialidad: " + especialidad, fontNormal, BaseColor.WHITE));

        infoTable.addCell(createCell("Porcentaje de Comisión: " + String.format("%.0f%%", porcentajeComision), fontBold, grisFondo));
        infoTable.addCell(createCell("Período Liquidado: " + fechaInicio + " al " + fechaFin, fontNormal, grisFondo));

        document.add(infoTable);

        // Tabla de servicios liquidados
        Paragraph pServ = new Paragraph("Detalle de Servicios Prestados en el Período:", fontBold);
        pServ.setSpacingAfter(8);
        document.add(pServ);

        PdfPTable servTable = new PdfPTable(new float[]{15f, 35f, 25f, 25f});
        servTable.setWidthPercentage(100);
        servTable.setSpacingAfter(15);

        servTable.addCell(createHeaderCell("Fecha", fontEncabezado, rosaAremi));
        servTable.addCell(createHeaderCell("Servicio", fontEncabezado, rosaAremi));
        servTable.addCell(createHeaderCell("Cliente", fontEncabezado, rosaAremi));
        servTable.addCell(createHeaderCell("Valor Facturado", fontEncabezado, rosaAremi));

        for (Object[] row : detallesServicios) {
            servTable.addCell(createCell(String.valueOf(row[0]), fontNormal, BaseColor.WHITE));
            servTable.addCell(createCell(String.valueOf(row[1]), fontNormal, BaseColor.WHITE));
            servTable.addCell(createCell(String.valueOf(row[2]), fontNormal, BaseColor.WHITE));
            servTable.addCell(createCell(String.valueOf(row[3]), fontNormal, BaseColor.WHITE));
        }

        document.add(servTable);

        // Resumen financiero
        PdfPTable totTable = new PdfPTable(2);
        totTable.setWidthPercentage(60);
        totTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totTable.setSpacingAfter(30);

        totTable.addCell(createCell("Total Servicios Prestados:", fontNormal, grisFondo));
        totTable.addCell(createCell(String.valueOf(totalServicios), fontBold, grisFondo));

        totTable.addCell(createCell("Ingreso Bruto Generado:", fontNormal, BaseColor.WHITE));
        totTable.addCell(createCell(String.format("$%,.0f COP", totalVentas), fontBold, BaseColor.WHITE));

        totTable.addCell(createCell("Porcentaje Aplicado:", fontNormal, grisFondo));
        totTable.addCell(createCell(String.format("%.0f%%", porcentajeComision), fontBold, grisFondo));

        totTable.addCell(createCell("TOTAL COMISIÓN A PAGAR:", fontBold, new BaseColor(255, 230, 240)));
        totTable.addCell(createCell(String.format("$%,.0f COP", totalComision), fontBold, new BaseColor(255, 230, 240)));

        totTable.addCell(createCell("Participación del Salón:", fontNormal, BaseColor.WHITE));
        totTable.addCell(createCell(String.format("$%,.0f COP", totalSalon), fontNormal, BaseColor.WHITE));

        document.add(totTable);

        // Firmas
        PdfPTable firmas = new PdfPTable(2);
        firmas.setWidthPercentage(100);
        firmas.setSpacingBefore(40);

        PdfPCell c1 = new PdfPCell(new Phrase("\n\n_______________________________\nFirma Empleada: " + nombreEmpleada, fontNormal));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell c2 = new PdfPCell(new Phrase("\n\n_______________________________\nFirma Administradora / Salón AREMI", fontNormal));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);

        firmas.addCell(c1);
        firmas.addCell(c2);

        document.add(firmas);
        document.close();

        return new File(fileName).getAbsolutePath();
    }

    private static PdfPCell createHeaderCell(String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private static PdfPCell createCell(String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        return cell;
    }
}
