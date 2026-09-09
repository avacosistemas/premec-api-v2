/**
 * 
 */
package ar.com.avaco.commons.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.avaco.commons.domain.Word;
import ar.com.avaco.commons.repository.WordRepository;
import ar.com.avaco.commons.service.WordService;
import ar.com.avaco.fwk.core.component.bean.service.NJBaseService;

/**
 * @author avaco
 */

@Transactional
@Service("wordService")
public class WordServiceImpl extends NJBaseService<Long, Word, WordRepository> implements WordService {

	@Override
	public Word getByKey(String key) {
		return repository.findByKeyEquals(key);
	}
	
	@Resource(name = "wordRepository")
	public void setWordRepository(WordRepository wordRepository) {
		this.repository = wordRepository;
	}

}
