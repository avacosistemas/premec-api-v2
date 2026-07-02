package ar.com.avaco.ws.service.impl;

import java.math.BigDecimal;

import ar.com.avaco.ws.dto.employee.EmployeesInfoReponseSapDTO;

public interface EmployeeService {

	EmployeesInfoReponseSapDTO getById(Long id);

	void updateNetoSueldoJornal(Long employeeId, BigDecimal neto, BigDecimal sueldoJornal);

}
