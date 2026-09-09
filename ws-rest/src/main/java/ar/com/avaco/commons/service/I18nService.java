/**
 * 
 */
package ar.com.avaco.commons.service;

import javax.persistence.EntityNotFoundException;

import ar.com.avaco.commons.domain.I18n;
import ar.com.avaco.fwk.core.component.bean.service.NJService;

/**
 * 
 *
 */
public interface I18nService  extends NJService<Long, I18n> {
	
	public I18n getByNameAndLang(String name, String lang) throws EntityNotFoundException;

}
