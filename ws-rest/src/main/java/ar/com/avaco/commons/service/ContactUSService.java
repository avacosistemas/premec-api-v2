/**
 * 
 */
package ar.com.avaco.commons.service;

import ar.com.avaco.commons.domain.ContactUS;
import ar.com.avaco.fwk.core.component.bean.service.NJService;

/**
 * 
 *
 */
public interface ContactUSService  extends NJService<Long, ContactUS> {
	public ContactUS send(ContactUS entity);
}
