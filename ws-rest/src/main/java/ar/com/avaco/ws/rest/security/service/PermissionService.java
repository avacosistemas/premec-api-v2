/**
 * 
 */
package ar.com.avaco.ws.rest.security.service;

import ar.com.avaco.fwk.security.domain.Permiso;
import ar.com.avaco.ws.rest.security.dto.Permission;
import ar.com.avaco.ws.service.ConvertService;

/**
 * @author avaco
 *
 */
public interface PermissionService extends ConvertService<Permission, Long, Permiso>{
	
}
