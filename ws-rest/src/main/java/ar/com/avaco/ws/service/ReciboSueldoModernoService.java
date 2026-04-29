package ar.com.avaco.ws.service;

import java.io.IOException;
import java.util.List;

import ar.com.avaco.ws.dto.timesheet.ReciboSueldoDTO;

public interface ReciboSueldoModernoService {

	List<ReciboSueldoDTO> procesarRecibos(byte[] pdfBytes, String outputDir) throws IOException;

}
