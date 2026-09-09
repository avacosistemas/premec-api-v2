/**
 * 
 */
package ar.com.avaco.commons.repository;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import ar.com.avaco.commons.domain.Word;
import ar.com.avaco.fwk.core.component.bean.repository.NJBaseRepository;

/**
 * 
 *
 */
@Repository("wordRepository")
public class WordRepositoryImpl extends NJBaseRepository<Long, Word> implements WordRepositoryCustom {

	public WordRepositoryImpl(EntityManager entityManager) {
		super(Word.class, entityManager);
	}
}

