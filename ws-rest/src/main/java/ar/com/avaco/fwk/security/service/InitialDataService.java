package ar.com.avaco.fwk.security.service;

public interface InitialDataService {
	void init() throws Exception;

	boolean isDatosGenerados();
}
