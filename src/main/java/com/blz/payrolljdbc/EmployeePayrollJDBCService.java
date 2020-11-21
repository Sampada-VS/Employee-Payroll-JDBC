package com.blz.payrolljdbc;

import java.util.List;

public class EmployeePayrollJDBCService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	EmployeePayrollDBService employeePayrollDBService = new EmployeePayrollDBService();

	public List<EmployeePayrollData> readEmployeePayrollDataFromDB(IOService ioService) {
		this.employeePayrollList = employeePayrollDBService.readData();
		return this.employeePayrollList;
	}
}
