package ar.com.avaco.fwk.security.repository;

import ar.com.avaco.fwk.security.domain.ParametroGeneral;

public interface ParametroGeneralRepository {
	ParametroGeneral getParametroGeneral(String param);
	
	void save(String param);	
}