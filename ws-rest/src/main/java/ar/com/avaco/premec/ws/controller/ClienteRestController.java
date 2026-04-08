package ar.com.avaco.premec.ws.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.avaco.commons.exception.BusinessException;
import ar.com.avaco.premec.dto.ClienteDTO;
import ar.com.avaco.premec.ws.service.ClienteEPService;
import ar.com.avaco.service.NotificacionReclamoService;
import ar.com.avaco.ws.rest.controller.AbstractDTORestController;
import ar.com.avaco.ws.rest.dto.JSONResponse;

@RestController
public class ClienteRestController extends AbstractDTORestController<ClienteDTO, Long, ClienteEPService> {

	@Autowired
	private NotificacionReclamoService notificacionService;
	
	@Override
	@RequestMapping(value = "/cliente", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONResponse> list() {
		return super.list();
	}

	@RequestMapping(value = "/cliente/nofiticacion", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONResponse> notificar() {
		notificacionService.enviarNotificaciones();
		return null;
	}
	
	@Override
	@RequestMapping(value = "/cliente", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONResponse> create(@RequestBody ClienteDTO dto) throws BusinessException {
		return super.create(dto);
	}
	
	@Override
	@Resource(name = "clienteEPService")
	public void setService(ClienteEPService service) {
		this.service = service;
	}

}
