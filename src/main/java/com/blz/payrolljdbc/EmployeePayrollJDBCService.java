package com.blz.payrolljdbc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void updateEmployeeSalaryWithThreads(Map<String, Double> arrayOfEmployeeSalary) {
		Map<Integer, Boolean> employeeUpdationStatus = new HashMap<Integer, Boolean>();
		Map<String, Double> employeeSalaryMap=arrayOfEmployeeSalary;
		employeeSalaryMap.forEach((empName, empSalary) -> {
			Runnable task = () -> {
				employeeUpdationStatus.put(empName.hashCode(), false);
				try {
					this.updateEmployeeSalary(empName, empSalary);
				} catch (PayrollServiceException e) {
					e.printStackTrace();
				}
				employeeUpdationStatus.put(empName.hashCode(), true);
			};
			Thread thread = new Thread(task, empName);
			thread.start();
		});
		while (employeeUpdationStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}

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

	public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate,
			LocalDate endDate) throws PayrollServiceException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getEmployeePayrollForDateRange(startDate, endDate);
		return null;
	}

	public Map<String, Double> readAverageSalaryByGender(IOService ioService) throws PayrollServiceException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getAverageSalaryByGender();
		return null;
	}

	public Map<String, Double> readSumSalaryByGender(IOService ioService) throws PayrollServiceException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getSumSalaryByGender();
		return null;
	}

	public Map<String, Double> readMinSalaryByGender(IOService ioService) throws PayrollServiceException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getMinSalaryByGender();
		return null;
	}

	public Map<String, Double> readMaxSalaryByGender(IOService ioService) throws PayrollServiceException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getMaxSalaryByGender();
		return null;
	}

	public Map<String, Integer> readEmployeeByGender(IOService ioService) throws PayrollServiceException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getEmployeeCountByGender();
		return null;
	}

	public void addEmployeeToPayroll(String name, String department, String gender, double salary, LocalDate startDate)
			throws PayrollServiceException {
		employeePayrollList
				.add(employeePayrollDBService.addEmployeeToPayroll(name, department, gender, salary, startDate));
	}

	public void addEmployeeToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
		employeePayrollDataList.forEach(employeePayrollData -> {
			try {
				this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.department,
						employeePayrollData.gender, employeePayrollData.salary, employeePayrollData.startDate);
			} catch (PayrollServiceException e) {
				e.printStackTrace();
			}
		});
	}

	public void addEmployeeToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		employeePayrollDataList.forEach(employeeData -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(employeeData.hashCode(), false);
				try {
					this.addEmployeeToPayroll(employeeData.name, employeeData.department, employeeData.gender,
							employeeData.salary, employeeData.startDate);
				} catch (PayrollServiceException e) {
					e.printStackTrace();
				}
				employeeAdditionStatus.put(employeeData.hashCode(), true);
			};
			Thread thread = new Thread(task, employeeData.name);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	public void deleteEmployee(String name) throws PayrollServiceException {
		int result = employeePayrollDBService.deleteEmployeeData(name);
		if (result == 0)
			return;
	}

	public long countEntries(IOService ioService) {
		return employeePayrollList.size();
	}

}
