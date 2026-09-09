/**
 * 
 */
package ar.com.avaco.commons.service;

import ar.com.avaco.commons.domain.Word;
import ar.com.avaco.fwk.core.component.bean.service.NJService;


/**
 * 
 *
 */
public interface WordService extends NJService<Long, Word>{

	Word getByKey(String key);
	
}
