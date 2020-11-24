package com.blz.payrolljdbc;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
		Instant start = Instant.now();
		employeePayrollJDBCService.addEmployeeToPayroll(Arrays.asList(arrayOfEmployees));
		Instant end = Instant.now();
		System.out.println("Duration without thread :" + Duration.between(start, end));
		Instant threadStart=Instant.now();
		employeePayrollJDBCService.addEmployeeToPayrollWithThreads(Arrays.asList(arrayOfEmployees));
		Instant threadEnd=Instant.now();
		System.out.println("Duration with thread :"+Duration.between(threadStart, threadEnd));
		assertEquals(9,employeePayrollJDBCService.countEntries(EmployeePayrollJDBCService.IOService.DB_IO));
	}
}
