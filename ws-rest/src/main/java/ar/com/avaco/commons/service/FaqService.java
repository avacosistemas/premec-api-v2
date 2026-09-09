/**
 * 
 */
package ar.com.avaco.commons.service;

import java.util.List;

import ar.com.avaco.commons.domain.Faq;
import ar.com.avaco.fwk.core.component.bean.service.NJService;

/**
 * 
 *
 */
public interface FaqService  extends NJService<Long, Faq> {

	/**
	 * List faqs by category and subcategory.
	 * @param category
	 * @param subcategory
	 * @return
	 */
	public List<Faq> listFaqs(String category, String subcategory);

	public List<Faq> listFaqs(String category);

	public List<Faq> listFavouriteFaqs(String category, String subcategory);
	
}
