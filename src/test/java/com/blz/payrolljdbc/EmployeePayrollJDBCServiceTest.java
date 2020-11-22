package com.blz.payrolljdbc;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmployeePayrollJDBCServiceTest {

	static EmployeePayrollJDBCService employeePayrollJDBCService;
	List<EmployeePayrollData> employeePayrollData;

	@BeforeClass
	public static void createObj() {
		employeePayrollJDBCService = new EmployeePayrollJDBCService();
	}

	@AfterClass
	public static void nullObj() {
		employeePayrollJDBCService = null;
	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchTotalEmployeeCount() throws PayrollServiceException {
		employeePayrollData = employeePayrollJDBCService
				.readEmployeePayrollDataFromDB(EmployeePayrollJDBCService.IOService.DB_IO);
		assertEquals(4, employeePayrollData.size());
		System.out.println("Total employee in employee payroll database :" + employeePayrollData.size());
	}

	@Test
	public void givenEmployeeSalary_WhenUpdated_ShouldMatch() throws PayrollServiceException {
		employeePayrollData = employeePayrollJDBCService
				.readEmployeePayrollDataFromDB(EmployeePayrollJDBCService.IOService.DB_IO);
		employeePayrollJDBCService.updateEmployeeSalary("Terrisa", 3000000.00);
		boolean result = employeePayrollJDBCService.checkEmployeePayrollSyncWithDB("Terrisa");
		assertTrue(result);
		System.out.println("Salary got updated for Terrisa.");
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() throws PayrollServiceException {
		employeePayrollJDBCService.readEmployeePayrollDataFromDB(EmployeePayrollJDBCService.IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2019, 01, 01);
		LocalDate endDate = LocalDate.now();
		employeePayrollData = employeePayrollJDBCService
				.readEmployeePayrollForDateRange(EmployeePayrollJDBCService.IOService.DB_IO, startDate, endDate);
		assertEquals(3, employeePayrollData.size());
		System.out.println("Employee count match for given date range.");
	}
}
