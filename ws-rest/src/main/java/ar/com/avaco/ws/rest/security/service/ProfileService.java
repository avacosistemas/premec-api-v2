/**
 * 
 */
package ar.com.avaco.ws.rest.security.service;

import ar.com.avaco.fwk.security.domain.Perfil;
import ar.com.avaco.ws.rest.security.dto.Profile;
import ar.com.avaco.ws.service.ConvertService;

/**
 * @author avaco
 *
 */
public interface ProfileService extends ConvertService<Profile, Long, Perfil>{
	
}
