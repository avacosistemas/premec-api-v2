package ar.com.avaco.fwk.security.repository;

import ar.com.avaco.fwk.core.component.bean.repository.NJRepository;
import ar.com.avaco.fwk.security.domain.Rol;

public interface RolRepository extends NJRepository<Long, Rol>,
		RolRepositoryCustom {

}