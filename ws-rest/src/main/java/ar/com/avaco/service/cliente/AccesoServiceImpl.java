package ar.com.avaco.service.cliente;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.avaco.arc.core.component.bean.service.NJBaseService;
import ar.com.avaco.arc.sec.domain.Acceso;
import ar.com.avaco.arc.sec.domain.Usuario;
import ar.com.avaco.arc.sec.service.UsuarioService;
import ar.com.avaco.repository.cliente.AccesoRepository;

@Transactional
@Service("accesoService")
public class AccesoServiceImpl extends NJBaseService<Long, Acceso, AccesoRepository> implements AccesoService {

	
	@Autowired
	private UsuarioService usuarioService;
	
	@Resource(name = "accesoRepository")
	void setAccesoRepository(AccesoRepository accesoRepository) {
		this.repository = accesoRepository;
	}

	@Override
	public List<Acceso> list(Long usuarioId) {
		return this.getRepository().findByUsuarioId(usuarioId);
	}
	
	@Override
	public void remove(Long id) {
		Acceso one = this.repository.getOne(id);
		Usuario usuario = usuarioService.get(one.getUsuario().getId());
		usuario.getAccesos().remove(one);
		usuarioService.update(usuario);
	}

}
