package ar.com.avaco.premec.sap.dto;

import java.util.List;

public class MachineReclamoStatsRequestDTO {

	private String cuit;
	private String machine;
	private List<PeriodoDTO> periodos;

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public List<PeriodoDTO> getPeriodos() {
		return periodos;
	}

	public void setPeriodos(List<PeriodoDTO> periodos) {
		this.periodos = periodos;
	}
}
