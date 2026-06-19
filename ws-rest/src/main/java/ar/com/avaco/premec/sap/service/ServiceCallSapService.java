package ar.com.avaco.premec.sap.service;

import java.util.List;

import ar.com.avaco.arc.core.domain.filter.ReclamoFilterDTO;
import ar.com.avaco.premec.sap.dto.ServiceCallActivityDTO;
import ar.com.avaco.premec.sap.dto.ServiceCallReclamoListDTO;

public interface ServiceCallSapService {

	List<ServiceCallReclamoListDTO> getServiceCalls(ReclamoFilterDTO filter); 
	
	List<ServiceCallActivityDTO> getActivitiesByServiceCall(Long serviceCallId);

}
