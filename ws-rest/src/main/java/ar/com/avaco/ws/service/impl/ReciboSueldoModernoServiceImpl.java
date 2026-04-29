package ar.com.avaco.ws.service.impl;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.stereotype.Service;

import ar.com.avaco.ws.dto.timesheet.ReciboSueldoArchivoDTO;
import ar.com.avaco.ws.dto.timesheet.ReciboSueldoDTO;
import ar.com.avaco.ws.service.AbstractSapService;
import ar.com.avaco.ws.service.ReciboSueldoModernoService;

@Service("reciboSueldoModernoService")
public class ReciboSueldoModernoServiceImpl extends AbstractSapService implements ReciboSueldoModernoService {

	@Override
	public List<ReciboSueldoDTO> procesarRecibos(byte[] pdfBytes, String tipo) throws IOException {

		List<ReciboSueldoDTO> recibos = new ArrayList<>();
		
		File rec = new File("D:\\desarrollo\\premec\\recibos-nuevo.pdf");
		String outputDir = "D:\\desarrollo\\premec\\nuevosrecibos";
		
		try (PDDocument document = PDDocument.load(rec)) {

			float pageHeight = document.getPage(0).getMediaBox().getHeight();

			// Rect para extracción (invertido)
			Rectangle rectLegajo = new Rectangle(20, (int) (pageHeight - 447 - 9), 84, 9);
			Rectangle rectPeriodo = new Rectangle(20, (int) (pageHeight - 473 - 13), 84, 12);
			Rectangle rectDescripcion = new Rectangle(20 + 87, (int) (pageHeight - 473 - 13), 133, 12);
			Rectangle rectNombre = new Rectangle(20 + 87, (int) (pageHeight - 447 - 9), 192, 9);			
			Rectangle rectNeto = new Rectangle(80, 485, 80, 13);
			
			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);
			stripper.addRegion("legajo", rectLegajo);
			stripper.addRegion("periodo", rectPeriodo);
			stripper.addRegion("descripcion", rectDescripcion);
			stripper.addRegion("nombre", rectNombre);
			stripper.addRegion("neto", rectNeto);

			Map<String, ReciboSueldoArchivoDTO> docsPorLegajo = new LinkedHashMap<>();

			for (int i = 0; i < document.getNumberOfPages(); i++) {

				PDPage page = document.getPage(i);

				// debug visual (mismo rect, sin transformar)
				drawRectangle(document, page, rectLegajo);
				drawRectangle(document, page, rectPeriodo);
				drawRectangle(document, page, rectDescripcion);
				drawRectangle(document, page, rectNombre);
				drawRectangle(document, page, rectNeto);

				stripper.extractRegions(page);

				String textoLegajo = stripper.getTextForRegion("legajo").replaceAll("\\s+", "").trim();
				String periodo = stripper.getTextForRegion("periodo").trim();
				String descripcion = stripper.getTextForRegion("descripcion").trim();
				String nombre = stripper.getTextForRegion("nombre").trim();
				String textoNeto = stripper.getTextForRegion("neto").trim().replace("\\n", "").replace("\\n", "");
				
				System.out.println(textoLegajo + " " + periodo  + " " + descripcion + " " + nombre + " " + textoNeto);

				Long timeInMillis = Calendar.getInstance().getTimeInMillis();
				
				
				Integer legajo = Integer.parseInt(textoLegajo);
				BigDecimal neto = new BigDecimal(textoNeto.replace(",", ""));
				
				ReciboSueldoDTO recibo = new ReciboSueldoDTO(legajo, nombre, periodo, neto, tipo, descripcion,
						timeInMillis.toString());
				
				ReciboSueldoArchivoDTO recarc = docsPorLegajo.get(textoLegajo);

				PDDocument pdDocument = null;
				if (recarc == null) {
					pdDocument = new PDDocument();
				} else {
					pdDocument = recarc.getDocument();
				}
				
				pdDocument.addPage(page);
				
				recarc = new ReciboSueldoArchivoDTO(recibo, pdDocument);
				
				docsPorLegajo.put(textoLegajo, recarc);
				
			}

			for (Map.Entry<String, ReciboSueldoArchivoDTO> entry : docsPorLegajo.entrySet()) {
				
				ReciboSueldoDTO recibo = entry.getValue().getReciboSueldo();
				PDDocument pdfdoc = entry.getValue().getDocument();
				
				String month = recibo.getPeriodo().split("/")[0];
				String year = recibo.getPeriodo().split("/")[1];
				
				Long timeInMillis = Calendar.getInstance().getTimeInMillis();
				String key = recibo.getLegajo() + "_" + year + month + "_" + tipo + "_" + timeInMillis.toString();
				
				File out = new File(outputDir, key  + ".pdf");
				pdfdoc.save(out);
				pdfdoc.close();
				
				recibos.add(recibo);
			}

		}
		
		return recibos;
		
	}

	private void drawRectangle(PDDocument document, PDPage page, Rectangle rect) throws IOException {

		try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND,
				true)) {

			cs.setStrokingColor(255, 0, 0);
			cs.setLineWidth(1);

			cs.addRect(rect.x, rect.y, rect.width, rect.height);
			cs.stroke();
		}
	}

}
