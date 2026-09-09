/**
 * 
 */
package ar.com.avaco.commons.repository;

import ar.com.avaco.commons.domain.ContactUS;
import ar.com.avaco.fwk.core.component.bean.repository.NJRepository;

/**
 * @author avaco
 *
 */
public interface ContactUSRepository extends NJRepository<Long,ContactUS>, ContactUSRepositoryCustom {
	
}
