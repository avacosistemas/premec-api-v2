/**
 * 
 */
package ar.com.avaco.commons.repository;

import ar.com.avaco.commons.domain.Word;
import ar.com.avaco.fwk.core.component.bean.repository.NJRepository;

/**
 * @author avaco
 *
 */
public interface WordRepository extends NJRepository<Long, Word>, WordRepositoryCustom {

	Word findByKeyEquals(String key);

}
