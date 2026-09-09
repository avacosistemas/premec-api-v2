package ar.com.avaco.fwk.security.repository.impl;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ar.com.avaco.fwk.core.component.bean.repository.NJBaseRepository;
import ar.com.avaco.fwk.security.domain.Permiso;
import ar.com.avaco.fwk.security.repository.PermisoRepository;

@Repository("permisoRepository")
public class PermisoRepositoryImpl extends NJBaseRepository<Long, Permiso>
		implements PermisoRepository {

	public PermisoRepositoryImpl(EntityManager em) {
		super(Permiso.class, em);
	}

	@Override
	public Permiso getPermisoPorCodigo(String codigo) {
		return (Permiso) getCurrentSession()
				.createCriteria(this.getHandledClass())
				.add(Restrictions.eq("codigo", codigo)).uniqueResult();
	}
}