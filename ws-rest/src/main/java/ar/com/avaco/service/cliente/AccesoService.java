package ar.com.avaco.service.cliente;

import java.util.List;

import ar.com.avaco.fwk.core.component.bean.service.NJService;
import ar.com.avaco.fwk.security.domain.Acceso;


public interface AccesoService extends NJService<Long, Acceso> {

	List<Acceso> list(Long usuarioId);


}
