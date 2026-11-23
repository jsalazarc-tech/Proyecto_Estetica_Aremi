package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generador de Comprobantes Formales de Liquidación en formato PDF con iText.
 */
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

        BaseColor rosaAremi = new BaseColor(219, 112, 147);
        BaseColor grisFondo = new BaseColor(245, 245, 245);
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, rosaAremi);
        Font fontSub = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.DARK_GRAY);
        Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        Font fontBold = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

        Paragraph pTitulo = new Paragraph("SALÓN DE BELLEZA AREMI", fontTitulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        Paragraph pSub = new Paragraph("Comprobante Oficial de Liquidación de Comisiones", fontSub);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(15);
        document.add(pSub);

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

        Paragraph pServ = new Paragraph("Detalle de Servicios Prestados en el Período:", fontBold);
        pServ.setSpacingAfter(8);
        document.add(pServ);

        PdfPTable servTable = new PdfPTable(new float[]{15f, 35f, 25f, 25f});
        servTable.setWidthPercentage(100);
        servTable.setSpacingAfter(15);

        servTable.addCell(createCell("Fecha", fontEncabezado, rosaAremi));
        servTable.addCell(createCell("Servicio Prestado", fontEncabezado, rosaAremi));
        servTable.addCell(createCell("Cliente", fontEncabezado, rosaAremi));
        servTable.addCell(createCell("Valor Cobrado", fontEncabezado, rosaAremi));

        if (detallesServicios != null) {
            boolean zebra = false;
            for (Object[] fila : detallesServicios) {
                BaseColor bg = zebra ? grisFondo : BaseColor.WHITE;
                servTable.addCell(createCell(String.valueOf(fila[0]), fontNormal, bg));
                servTable.addCell(createCell(String.valueOf(fila[1]), fontNormal, bg));
                servTable.addCell(createCell(String.valueOf(fila[2]), fontNormal, bg));
                servTable.addCell(createCell("$" + String.format("%,.0f", (Double) fila[3]), fontNormal, bg));
                zebra = !zebra;
            }
        }

        document.add(servTable);

        PdfPTable resTable = new PdfPTable(2);
        resTable.setWidthPercentage(100);
        resTable.setSpacingAfter(25);

        resTable.addCell(createCell("Total Servicios Atendidos:", fontNormal, grisFondo));
        resTable.addCell(createCell(String.valueOf(totalServicios), fontBold, grisFondo));

        resTable.addCell(createCell("Total Ventas Generadas:", fontNormal, BaseColor.WHITE));
        resTable.addCell(createCell("$" + String.format("%,.0f", totalVentas) + " COP", fontBold, BaseColor.WHITE));

        resTable.addCell(createCell("Comisión a Pagar a la Empleada (" + String.format("%.0f%%", porcentajeComision) + "):", fontBold, new BaseColor(230, 245, 230)));
        resTable.addCell(createCell("$" + String.format("%,.0f", totalComision) + " COP", fontBold, new BaseColor(230, 245, 230)));

        resTable.addCell(createCell("Ingreso Neto Salón de Belleza AREMI:", fontBold, new BaseColor(255, 240, 245)));
        resTable.addCell(createCell("$" + String.format("%,.0f", totalSalon) + " COP", fontBold, new BaseColor(255, 240, 245)));

        document.add(resTable);

        Paragraph pFirmas = new Paragraph("\n\n___________________________                 ___________________________\nFirma Administradora                          Firma de Conformidad Empleada", fontNormal);
        pFirmas.setAlignment(Element.ALIGN_CENTER);
        document.add(pFirmas);

        document.close();
        return new File(fileName).getAbsolutePath();
    }

    private static PdfPCell createCell(String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
