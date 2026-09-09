/**
 * 
 */
package ar.com.avaco.commons.repository;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import ar.com.avaco.commons.domain.GrupoTipoActividad;
import ar.com.avaco.fwk.core.component.bean.repository.NJBaseRepository;

@Repository("grupoTipoActividadRepository")
public class GrupoTipoActividadRepositoryImpl extends NJBaseRepository<Long, GrupoTipoActividad>
		implements GrupoTipoActividadRepositoryCustom {

	public GrupoTipoActividadRepositoryImpl(EntityManager entityManager) {
		super(GrupoTipoActividad.class, entityManager);
	}
}
