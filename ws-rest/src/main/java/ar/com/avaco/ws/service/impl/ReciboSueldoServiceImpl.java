package ar.com.avaco.ws.service.impl;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ar.com.avaco.arc.core.service.MailSenderSMTPService;
import ar.com.avaco.arc.sec.service.UsuarioService;
import ar.com.avaco.commons.exception.ErrorValidationException;
import ar.com.avaco.utils.DateUtils;
import ar.com.avaco.ws.dto.attachment.AttachmentLine;
import ar.com.avaco.ws.dto.attachment.ResponseAttachmentGetPost;
import ar.com.avaco.ws.dto.employee.EmployeesInfoReponseSapDTO;
import ar.com.avaco.ws.dto.timesheet.ProjectManagementTimeSheetAttachDTO;
import ar.com.avaco.ws.dto.timesheet.ReciboSueldoArchivoDTO;
import ar.com.avaco.ws.dto.timesheet.ReciboSueldoDTO;
import ar.com.avaco.ws.dto.timesheet.RegistroReciboPorUsuarioDTO;
import ar.com.avaco.ws.service.AbstractSapService;
import ar.com.avaco.ws.service.ReciboSueldoService;

@Service("reciboSueldoService")
public class ReciboSueldoServiceImpl extends AbstractSapService implements ReciboSueldoService {

	
	private final String SUELDO_JORNAL = "Sueldo Jornal";
	private final String SUELDO_MENSUAL = "Sueldo Mensual";
	
	@Value("${email.contador.from}")
	private String emailFromContador;

	@Value("${email.contador.to}")
	private String emailToContador;

	@Value("${email.contador.subject.rechazo}")
	private String subjectRechazo;

	@Value("${email.contador.body.rechazo}")
	private String bodyRechazo;

	@Value("${path.recibo}")
	private String reciboPath;

	@Value("${path.recibo.serversap}")
	private String reciboPathServeSap;

	@Autowired
	private MailSenderSMTPService sender;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private TimeSheetService timeSheetService;

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private EmployeeService employeeService;

	@Override
	public void aprobarRecibos(List<ReciboSueldoDTO> lista) {

		// Por cada recibo
		for (ReciboSueldoDTO recibo : lista) {

			// Obtengo usuario sap
			String usuarioSap = usuarioService.getUsuarioSAPByLegajo(recibo.getLegajo());

			// Obtengo legajo
			String legajo = Integer.toString(recibo.getLegajo());

			String timeInMilis = recibo.getTimeInMilis();
			String descripcion = recibo.getDescripcion();

			String[] split = recibo.getPeriodo().split("/");
			String month = split[0];
			String year = split[1];

			// Armo el periodo desde/hasta para buscar en sap
			String from = year + month + "01";

			Calendar instance = Calendar.getInstance();
			instance.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
			instance.add(Calendar.MONTH, 1);
			instance.add(Calendar.DAY_OF_MONTH, -1);

			String to = DateUtils.toString(instance.getTime(), "yyyyMMdd");

			// Busco el registros del timesheet con el periodo
			Long usuarioSapLong = Long.parseLong(usuarioSap);
			TimeSheetEntryAttach entryAttach = this.timeSheetService.getTimeSheetEntries(usuarioSapLong, from,
					to);

			// Nuevo attachment entry del project management timesheet existente
			Long newAttachmentEntry = null;

			Boolean existeTimeSheet = entryAttach.getAbsEntry() != null;
			
			// Si no existe el registro del timesheet lo creo y obtengo el nuevo entry.
			// El attachmententry va a ser null en este caso
			boolean esReciboJornalMensual = recibo.getTipo().equals(SUELDO_JORNAL) || recibo.getTipo().equals(SUELDO_MENSUAL);
			if (!existeTimeSheet) {
				// Genero el timesheet y luego obtengo el absentry
				Long absEntry = null;
				if (esReciboJornalMensual) {
					absEntry = this.timeSheetService.generarTimeSheet(usuarioSapLong, from, to, recibo.getNeto(), recibo.getSueldoJornal());
				} else {
					absEntry = this.timeSheetService.generarTimeSheet(usuarioSapLong, from, to);
				}
				entryAttach.setAbsEntry(absEntry);
			}

			// Ya quedo seteado el absEntry (ya sea porque existencia o creacion)

			// Paso al attachment (el pdf del recibo)

			// Creo un attachment y le adjunto el archivo
			List<Map<String, String>> attachments = new ArrayList<>();

			Map<String, String> nuevoRecibo = genearAttachmentRecibo(recibo.getTipo(), legajo, usuarioSap, month, year,
					timeInMilis, descripcion);

			if (entryAttach.getAttachmentEntry() != null) {
				// Si ya existe un attachment en ese timesheet
				// Obtengo el attachment

				ResponseAttachmentGetPost currentAttach = this.attachmentService
						.getAttachment(entryAttach.getAttachmentEntry());

				// Busco si dentro de los adjuntos existe uno con el mismo freetext
				Optional<AttachmentLine> findAny = currentAttach.getAttachments2Lines().stream()
						.filter(actual -> actual.getFreeText().equals(nuevoRecibo.get("FreeText"))).findAny();

				// Si no encontré uno igual al que quiero subir, entonces lo subo.
				if (!findAny.isPresent()) {
					attachments.add(nuevoRecibo);
				}

				// Luego por cada attachment armo el adjunto en la lista para volver a enviar
				currentAttach.getAttachments2Lines().stream().forEach(att -> {
					attachments.add(genearAttachmentReciboExistente(att.getFreeText(), usuarioSap, att.getSourcePath(),
							att.getFileName()));
				});

				// De esta manera ya tengo los actuales y el nuevo que voy a agregar
			} else {
				attachments.add(nuevoRecibo);
			}

			// Envio el nuevo archivo y los posibles actuales (si existian) y obtengo un
			// nuevo entry
			newAttachmentEntry = this.attachmentService.enviarAttachmentsSap(attachments);

			if (existeTimeSheet && esReciboJornalMensual) {
				this.timeSheetService.updateTimeSheet(entryAttach.getAbsEntry(), newAttachmentEntry, recibo.getNeto(), recibo.getSueldoJornal());
			} else {
				this.timeSheetService.updateTimeSheet(entryAttach.getAbsEntry(), newAttachmentEntry);
			}
			
			if (esReciboJornalMensual) {
				employeeService.updateNetoSueldoJornal(usuarioSapLong, recibo.getNeto(), recibo.getSueldoJornal());
			}

			
		}
	}

	private Map<String, String> genearAttachmentRecibo(String tipo, String legajo, String usuarioSap, String month,
			String year, String timeInMilis, String descripcion) {
		String path = reciboPath + "\\" + year + month;
		String nombre = legajo + "_" + year + month + "_" + tipo + "_" + timeInMilis;

		Map<String, String> fotoMap = new HashMap<String, String>();
		fotoMap.put("SourcePath", path);
		fotoMap.put("FileName", nombre);
		fotoMap.put("FileExtension", "pdf");
		fotoMap.put("UserID", usuarioSap.toString());
		fotoMap.put("Override", "tYES");
		fotoMap.put("FreeText", tipo + "|" + descripcion);
		return fotoMap;
	}

	private Map<String, String> genearAttachmentReciboExistente(String tipo, String usuarioSap, String path,
			String nombre) {
		Map<String, String> fotoMap = new HashMap<String, String>();
		fotoMap.put("SourcePath", path);
		fotoMap.put("FileName", nombre);
		fotoMap.put("FileExtension", "pdf");
		fotoMap.put("UserID", usuarioSap.toString());
		fotoMap.put("Override", "tYES");
		fotoMap.put("FreeText", tipo);
		return fotoMap;
	}

	@Override
	public List<ReciboSueldoDTO> procesarRecibos(String tipo, byte[] archivo) throws IOException {
		List<ReciboSueldoDTO> recibos = new ArrayList<>();

		try (PDDocument document = PDDocument.load(archivo)) {

			float pageHeight = document.getPage(0).getMediaBox().getHeight();

			// Rect para extracción (invertido)
			Rectangle rectLegajo = new Rectangle(20, (int) (pageHeight - 447 - 9), 84, 9);
			Rectangle rectPeriodo = new Rectangle(20, (int) (pageHeight - 473 - 13), 84, 12);
			Rectangle rectDescripcion = new Rectangle(20 + 87, (int) (pageHeight - 473 - 13), 133, 12);
			Rectangle rectNombre = new Rectangle(20 + 87, (int) (pageHeight - 447 - 9), 192, 9);
			Rectangle rectNeto = new Rectangle(80, 485, 80, 13);
			Rectangle rectSueldoJornal = new Rectangle(80, (int) (pageHeight - 435 - 9), 60, 9);

			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);
			stripper.addRegion("legajo", rectLegajo);
			stripper.addRegion("periodo", rectPeriodo);
			stripper.addRegion("descripcion", rectDescripcion);
			stripper.addRegion("nombre", rectNombre);
			stripper.addRegion("neto", rectNeto);
			stripper.addRegion("sueldoJornal", rectSueldoJornal);

			Map<String, ReciboSueldoArchivoDTO> docsPorLegajo = new LinkedHashMap<>();

			for (int i = 0; i < document.getNumberOfPages(); i++) {

				PDPage page = document.getPage(i);

				stripper.extractRegions(page);

				String textoLegajo = stripper.getTextForRegion("legajo").replaceAll("\\s+", "").trim();
				String periodo = stripper.getTextForRegion("periodo").trim();
				String descripcion = stripper.getTextForRegion("descripcion").trim();
				String nombre = stripper.getTextForRegion("nombre").trim();
				String textoNeto = stripper.getTextForRegion("neto").trim().replace("\\n", "").replace("\\n", "");
				String textoSueldoJornal = stripper.getTextForRegion("sueldoJornal").trim().replace("\\n", "")
						.replace("\\n", "");

				Long timeInMillis = Calendar.getInstance().getTimeInMillis();

				Integer legajo = Integer.parseInt(textoLegajo);
				BigDecimal neto = new BigDecimal(textoNeto.replace(",", ""));
				BigDecimal sueldoJornal = new BigDecimal(textoSueldoJornal.replace(",", ""));

				ReciboSueldoDTO recibo = new ReciboSueldoDTO(legajo, nombre, periodo, neto, tipo, descripcion,
						timeInMillis.toString(), sueldoJornal);

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

				String key = recibo.getLegajo() + "_" + year + month + "_" + tipo + "_" + recibo.getTimeInMilis();

				String outputDir = reciboPath + "\\" + year + month;

				Files.createDirectories(Paths.get(outputDir));

				File out = new File(outputDir, key + ".pdf");
				pdfdoc.save(out);
				pdfdoc.close();

				recibos.add(recibo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recibos;
	}

	@Override
	public void rechazarRecibos(List<ReciboSueldoDTO> lista) {
		String msj = bodyRechazo.replace("%detalle%", generarMensajeRechazo(lista));
		sender.sendMail(emailFromContador, emailToContador, subjectRechazo, msj, null);
	}

	private String generarMensajeRechazo(List<ReciboSueldoDTO> lista) {
		String body = "";
		for (ReciboSueldoDTO recibo : lista) {
			body += "<tr>";
			body += "<td>" + recibo.getLegajo() + "</td>";
			body += "<td>" + recibo.getNombreCompleto() + "</td>";
			body += "<td>" + recibo.getPeriodo() + "</td>";
			body += "<td>" + recibo.getTipo() + "</td>";
			body += "<td>" + recibo.getNeto() + "</td>";
			body += "<td>" + recibo.getObservaciones() + "</td>";
			body += "</tr>";
		}
		return body;
	}

	@Override
	public List<RegistroReciboPorUsuarioDTO> listarRecibosPorUsuario() {

		// Obtengo el usuario logueado
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// Obtengo el usuario SAP
		String usuarioSAP = usuarioService.getUsuarioSAPByUsername(username);

		// Si tiene usuario sap
		if (StringUtils.isBlank(usuarioSAP)) {
			throw new ErrorValidationException("El usuario no tiene asociado usuario sap", null);
		}

		// Busco los attachments del usuario sap
		List<ProjectManagementTimeSheetAttachDTO> registros = this.timeSheetService
				.listTimeSheetByUsuarioSap(Long.parseLong(usuarioSAP));

		List<RegistroReciboPorUsuarioDTO> recibos = new ArrayList<>();

		// Por cada registro
		registros.stream().forEach(registro -> {

			// Armo el periodo
			int month = Integer.parseInt(registro.getDateFrom().split("-")[1]);
			int year = Integer.parseInt(registro.getDateFrom().split("-")[0]);

			Calendar instance = Calendar.getInstance();
			instance.set(Calendar.MONTH, month - 1);

			SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM", new Locale("es", "ES"));
			String monthString = formatoMes.format(instance.getTime());

			// Por cada attachment que tenga el periodo (seria un recibo por attachment)
			registro.getAttachments2().getLines().stream().forEach(tipo -> {

				// Armo el registro
				RegistroReciboPorUsuarioDTO r = new RegistroReciboPorUsuarioDTO();
				r.setAttachmentEntry(registro.getAttachmentEntry());
				r.setAbsEntry(tipo.getAbsoluteEntry());
				r.setMonth(month);
				r.setYear(year);
				r.setMonthString(monthString);
				r.setFilePath(tipo.getTargetPath() + "\\" + tipo.getFileName() + "." + tipo.getFileExtension());
				String freeText = tipo.getFreeText();
				int idx = freeText.indexOf('|');
				String reciboTipo = (idx >= 0) ? freeText.substring(0, idx) : freeText;
				String descripcion = (idx >= 0) ? freeText.substring(idx + 1, freeText.length()) : "";
				r.setDescripcion(descripcion);
				r.setTipo(reciboTipo);
				r.setFirmado(tipo.getFirmado() != null && tipo.getFirmado().equals("tYES"));
				r.setLineNum(tipo.getLine());
				recibos.add(r);

			});

		});
		return recibos;
	}

	@Override
	public byte[] obtenerReciboPDF(RegistroReciboPorUsuarioDTO recibo) throws IOException {
		Path path = Paths.get(recibo.getFilePath().replace("\\", "\\\\"));
		byte[] contenido = Files.readAllBytes(path);
		return contenido;
	}

	@Override
	public void firmarReciboPDF(RegistroReciboPorUsuarioDTO recibo) throws IOException {

		Logger logger = Logger.getLogger(ReciboSueldoServiceImpl.class);
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		String usuarioSAP = this.usuarioService.getUsuarioSAP(name);
		EmployeesInfoReponseSapDTO empleado = this.employeeService.getById(Long.parseLong(usuarioSAP));

		ResponseAttachmentGetPost attachmentFirma = attachmentService.getAttachment(empleado.getAttachmentEntry());
		Optional<AttachmentLine> firstFirma = attachmentFirma.getAttachments2Lines().stream()
				.filter(x -> x.getFreeText() != null && x.getFreeText().contains("firma")).findFirst();
		if (firstFirma.isEmpty()) {
			throw new ErrorValidationException("Falta cargar tu firma en el sistema para poder firmar el recibo", null);
		}
		
		AttachmentLine attFirma = firstFirma.get();
		String pathFirma = attFirma.getTargetPath() + "\\" + attFirma.getFileName() + "." + attFirma.getFileExtension();
		logger.info("Path firma: " + pathFirma);

		ResponseAttachmentGetPost attachmentRecibo = attachmentService.getAttachment(recibo.getAttachmentEntry());
		Optional<AttachmentLine> firstRecibo = attachmentRecibo.getAttachments2Lines().stream()
				.filter(x -> x.getLine().equals(recibo.getLineNum())).findFirst();
		AttachmentLine attLineRecibo = firstRecibo.get();

		Path pathRecibo = Paths
				.get((attLineRecibo.getSourcePath() + "\\" + attLineRecibo.getFileName() + "." + attLineRecibo.getFileExtension())
						.replace("\\", "\\\\"));

		File pdfFile = pathRecibo.toFile();

		try {

			PDDocument document = PDDocument.load(pdfFile);
			float pageHeight = document.getPage(0).getMediaBox().getHeight();

			Rectangle rectFirma = new Rectangle(280, 530, 100, 29);

			for (int i = 0; i < document.getNumberOfPages(); i++) {

				PDPage page = document.getPage(i);

				try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
						PDPageContentStream.AppendMode.APPEND, true, true)) {
					PDImageXObject firma = PDImageXObject.createFromFile(pathFirma, document);
					float x = rectFirma.x;
					float y = pageHeight - rectFirma.y - rectFirma.height;

					logger.info("Dibujando firma en X=" + x + " Y=" + y + " W=" + rectFirma.width + " H="
							+ rectFirma.height);

					contentStream.drawImage(firma, x, y, rectFirma.width, rectFirma.height);

					contentStream.stroke();

				}

			}
			document.save(pdfFile);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR durante la firma del PDF", e);
			throw new ErrorValidationException("Error al firmar el recibo", null);
		}

		try {

			logger.info("Iniciando actualización de EDocSign");

			Map<String, Object> attPatchMap = new HashMap<>();

			Map<String, Object> line = new HashMap<>();
			line.put("LineNum", recibo.getLineNum());
			line.put("EDocSign", "tYES");
			line.put("FileName", attLineRecibo.getFileName());

			List<Map<String, Object>> attaLines = new ArrayList<>();
			attaLines.add(line);

			attPatchMap.put("Attachments2_Lines", attaLines);

			logger.info("Llamando attachmentService.update()");

			attachmentService.update(recibo.getAttachmentEntry(), attPatchMap);

			logger.info("EDocSign actualizado correctamente");

		} catch (Exception e) {

			logger.error("ERROR actualizando EDocSign", e);

			throw new ErrorValidationException("Error al marcar el recibo como firmado", null);
		}

		logger.info("========== FIN firmarReciboPDF ==========");
	}

}
