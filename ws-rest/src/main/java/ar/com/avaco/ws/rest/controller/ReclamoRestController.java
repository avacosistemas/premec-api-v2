package ar.com.avaco.ws.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ar.com.avaco.arc.core.domain.filter.ReclamoFilterDTO;
import ar.com.avaco.premec.sap.dto.ServiceCallActivityDTO;
import ar.com.avaco.premec.sap.dto.ServiceCallReclamoListDTO;
import ar.com.avaco.premec.ws.service.ReclamoEPService;
import ar.com.avaco.service.NotificacionVencimientoService;
import ar.com.avaco.ws.rest.dto.JSONResponse;
import ar.com.avaco.ws.service.filter.PageResponse;

@Controller
public class ReclamoRestController {

	@Autowired
	private ReclamoEPService service;
	
	@Autowired
	private NotificacionVencimientoService vencimientoService;
	
	@RequestMapping(value = "/reclamo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONResponse> list(ReclamoFilterDTO reclamoFilterDTO) {
		
		List<ServiceCallReclamoListDTO> list = this.service.list(reclamoFilterDTO);

		PageResponse pr = new PageResponse();
		pr.setPage(reclamoFilterDTO.getPage());
		pr.setPageSize(reclamoFilterDTO.getPageSize());
		pr.setTotalReg(list.isEmpty() ? 0 : list.get(0).getTotalRegistros());
		
		JSONResponse response = new JSONResponse();
		response.setStatus(JSONResponse.OK);
		response.setData(list);
		response.setPage(pr);
		return new ResponseEntity<JSONResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/reclamo/actividades", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONResponse> getActividades(@RequestParam Long idServiceCall) {
		String cuit = SecurityContextHolder.getContext().getAuthentication().getName();
		List<ServiceCallActivityDTO> listActividades = this.service.listActividades(idServiceCall);
		JSONResponse response = new JSONResponse();
		response.setStatus(JSONResponse.OK);
		response.setData(listActividades);
		return new ResponseEntity<JSONResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/enviarVencimientos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONResponse> enviarVencimientos() {
		vencimientoService.enviarNotificaciones();
		JSONResponse response = new JSONResponse();
		response.setStatus(JSONResponse.OK);
		response.setData(null);
		return new ResponseEntity<JSONResponse>(response, HttpStatus.OK);
	}
	
}
