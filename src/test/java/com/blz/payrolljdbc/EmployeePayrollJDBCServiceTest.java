package com.blz.payrolljdbc;

import static org.junit.Assert.*;

import java.time.LocalDate;
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
		LocalDate startDate = LocalDate.of(2019, 01, 01);
		LocalDate endDate = LocalDate.now();
		employeePayrollData = employeePayrollJDBCService
				.readEmployeePayrollForDateRange(EmployeePayrollJDBCService.IOService.DB_IO, startDate, endDate);
		assertEquals(3, employeePayrollData.size());
		System.out.println("Employee count match for given date range.");
	}

	@Test
	public void givenEmployeePayroll_WhenSumOfSalaryRetrievedByGender_ShouldReturnProperValue()
			throws PayrollServiceException {
		Map<String, Double> sumOfSalaryByGender = employeePayrollJDBCService
				.readSumSalaryByGender(EmployeePayrollJDBCService.IOService.DB_IO);
		assertTrue(sumOfSalaryByGender.get("F").equals(7000000.00) && sumOfSalaryByGender.get("M").equals(4000000.00));
		System.out.println("Total salary value by gender matched with database.");
	}

	@Test
	public void givenEmployeePayroll_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue()
			throws PayrollServiceException {
		Map<String, Double> averageSalaryByGender = employeePayrollJDBCService
				.readAverageSalaryByGender(EmployeePayrollJDBCService.IOService.DB_IO);
		assertTrue(
				averageSalaryByGender.get("F").equals(3500000.00) && averageSalaryByGender.get("M").equals(2000000.00));
		System.out.println("Average salary value by gender matched with database.");
	}

	@Test
	public void givenEmployeePayroll_WhenMinimumOfSalaryRetrievedByGender_ShouldReturnProperValue()
			throws PayrollServiceException {
		Map<String, Double> minSalaryByGender = employeePayrollJDBCService
				.readMinSalaryByGender(EmployeePayrollJDBCService.IOService.DB_IO);
		assertTrue(minSalaryByGender.get("F").equals(3000000.00) && minSalaryByGender.get("M").equals(1000000.00));
		System.out.println("Minimum salary value by gender matched with database.");
	}

	@Test
	public void givenEmployeePayroll_WhenMaximumOfSalaryRetrievedByGender_ShouldReturnProperValue()
			throws PayrollServiceException {
		Map<String, Double> maxSalaryByGender = employeePayrollJDBCService
				.readMaxSalaryByGender(EmployeePayrollJDBCService.IOService.DB_IO);
		assertTrue(maxSalaryByGender.get("F").equals(4000000.00) && maxSalaryByGender.get("M").equals(3000000.00));
		System.out.println("Maximum salary value by gender matched with database.");
	}

	@Test
	public void givenEmployeePayroll_WhenCountOfEmployeeRetrievedByGender_ShouldReturnProperValue()
			throws PayrollServiceException {
		Map<String, Integer> countEmployeeByGender = employeePayrollJDBCService
				.readEmployeeByGender(EmployeePayrollJDBCService.IOService.DB_IO);
		assertTrue(countEmployeeByGender.get("F").equals(2) && countEmployeeByGender.get("M").equals(2));
		System.out.println("Count of employee value by gender matched with database.");
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws PayrollServiceException {
		employeePayrollJDBCService.addEmployeeToPayroll("Gunjan", "Sales", "F", 4000000.00, LocalDate.now());
		boolean result = employeePayrollJDBCService.checkEmployeePayrollSyncWithDB("Gunjan");
		assertTrue(result);
		System.out.println("Employee added and simultaneously payroll details are added by handling transactions.");
	}

	@Test
	public void givenEmployee_WhenDeleted_ShouldMatchCount() throws PayrollServiceException {
		employeePayrollJDBCService.deleteEmployee("Mark");
		employeePayrollData = employeePayrollJDBCService
				.readEmployeePayrollDataFromDB(EmployeePayrollJDBCService.IOService.DB_IO);
		assertEquals(4, employeePayrollData.size());
		System.out
				.println("After deletion, tTotal employee in employee payroll database :" + employeePayrollData.size());
	}
}
