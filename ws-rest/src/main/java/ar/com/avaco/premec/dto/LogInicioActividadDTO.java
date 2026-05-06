package ar.com.avaco.premec.dto;

import java.time.LocalDateTime;

public class LogInicioActividadDTO {

	private Integer id;
	private Integer activityId;
	private Integer serviceCallId;
	private String customerCode;
	private String customerName;
	private String internalSerialNum;
	private String itemCode;
	private String manufacturerSerialNum;
	private String customerEmail;
	private LocalDateTime fechaEvento;
	private Integer attendEmpl;
	private String tipoEvento;

	public LogInicioActividadDTO() {
	}

	public LogInicioActividadDTO(Integer id, Integer activityId, Integer serviceCallId, String customerCode,
			String customerName, String internalSerialNum, String itemCode, String manufacturerSerialNum,
			String customerEmail, LocalDateTime fechaEvento, Integer attendEmpl, String tipoEvento) {
		this.id = id;
		this.activityId = activityId;
		this.serviceCallId = serviceCallId;
		this.customerCode = customerCode;
		this.customerName = customerName;
		this.internalSerialNum = internalSerialNum;
		this.itemCode = itemCode;
		this.manufacturerSerialNum = manufacturerSerialNum;
		this.customerEmail = customerEmail;
		this.fechaEvento = fechaEvento;
		this.attendEmpl = attendEmpl;
		this.tipoEvento = tipoEvento;
	}

	// getters & setters

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public Integer getServiceCallId() {
		return serviceCallId;
	}

	public void setServiceCallId(Integer serviceCallId) {
		this.serviceCallId = serviceCallId;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getInternalSerialNum() {
		return internalSerialNum;
	}

	public void setInternalSerialNum(String internalSerialNum) {
		this.internalSerialNum = internalSerialNum;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getManufacturerSerialNum() {
		return manufacturerSerialNum;
	}

	public void setManufacturerSerialNum(String manufacturerSerialNum) {
		this.manufacturerSerialNum = manufacturerSerialNum;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public LocalDateTime getFechaEvento() {
		return fechaEvento;
	}

	public void setFechaEvento(LocalDateTime fechaEvento) {
		this.fechaEvento = fechaEvento;
	}

	public Integer getAttendEmpl() {
		return attendEmpl;
	}

	public void setAttendEmpl(Integer attendEmpl) {
		this.attendEmpl = attendEmpl;
	}

	public String getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(String tipoEvento) {
		this.tipoEvento = tipoEvento;
	}
}