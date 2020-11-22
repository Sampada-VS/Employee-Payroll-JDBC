package com.blz.payrolljdbc;

import java.util.List;

public class EmployeePayrollJDBCService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayrollJDBCService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmployeePayrollJDBCService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public List<EmployeePayrollData> readEmployeePayrollDataFromDB(IOService ioService) throws PayrollServiceException {
		this.employeePayrollList = employeePayrollDBService.readData();
		return this.employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double salary) throws PayrollServiceException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.salary = salary;
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		EmployeePayrollData employeePayrollData;
		employeePayrollData = this.employeePayrollList.stream().filter(dataItem -> dataItem.name.equals(name))
				.findFirst().orElse(null);
		return employeePayrollData;
	}

	public boolean checkEmployeePayrollSyncWithDB(String name) throws PayrollServiceException {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeeData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

}
