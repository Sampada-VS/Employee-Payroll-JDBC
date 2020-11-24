package com.blz.payrolljdbc;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmployeePayrollJDBCServiceTest {

	static EmployeePayrollJDBCService employeePayrollJDBCService;
	List<EmployeePayrollData> employeePayrollData;

	@BeforeClass
	public static void createObj() throws PayrollServiceException {
		employeePayrollJDBCService = new EmployeePayrollJDBCService();
		employeePayrollJDBCService.readEmployeePayrollDataFromDB(EmployeePayrollJDBCService.IOService.DB_IO);
	}

	@AfterClass
	public static void nullObj() {
		employeePayrollJDBCService = null;
	}

	@Test
	public void givenFourEmployees_WhenAdded_ShouldMatchEmployeeEntries() throws PayrollServiceException {
		EmployeePayrollData[] arrayOfEmployees = {
				new EmployeePayrollData(0, "Bill", "Sales", "M", 1000000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Terrisa", "Marketing", "F", 2000000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Charlie", "Sales", "M", 3000000.00, LocalDate.now()),
				new EmployeePayrollData(0, "Mark", "Marketing", "M", 5000000.00, LocalDate.now()) };
		EmployeePayrollJDBCService employeePayrollJDBCService = new EmployeePayrollJDBCService();
		employeePayrollJDBCService.readEmployeePayrollDataFromDB(EmployeePayrollJDBCService.IOService.DB_IO);
		Instant threadStart = Instant.now();
		employeePayrollJDBCService.addEmployeeToPayrollWithThreads(Arrays.asList(arrayOfEmployees));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with thread :" + Duration.between(threadStart, threadEnd));
		assertEquals(5, employeePayrollJDBCService.countEntries(EmployeePayrollJDBCService.IOService.DB_IO));
	}

	@Test
	public void givenFourEmployeeSalary_WhenUpdated_ShouldMatch() throws PayrollServiceException {
		Map<String, Double> arrayOfEmployeeSalary = new HashMap<String, Double>();
		arrayOfEmployeeSalary.put("Bill", 2000000.00);
		arrayOfEmployeeSalary.put("Terrisa", 3000000.00);
		arrayOfEmployeeSalary.put("Charlie", 4000000.00);
		arrayOfEmployeeSalary.put("Mark", 6000000.00);
		employeePayrollData = employeePayrollJDBCService
				.readEmployeePayrollDataFromDB(EmployeePayrollJDBCService.IOService.DB_IO);
		employeePayrollJDBCService.updateEmployeeSalaryWithThreads(arrayOfEmployeeSalary);
		boolean result = employeePayrollJDBCService.checkEmployeePayrollSyncWithDB("Mark");
		assertTrue(result);
		System.out.println("Salary got updated for Mark.");
	}
}
