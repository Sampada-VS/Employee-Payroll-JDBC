package com.blz.payrolljdbc;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmployeePayrollJDBCServiceTest {

	static EmployeePayrollJDBCService employeePayrollJDBCService;

	@BeforeClass
	public static void createObj() {
		employeePayrollJDBCService = new EmployeePayrollJDBCService();
	}

	@AfterClass
	public static void nullObj() {
		employeePayrollJDBCService = null;
	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchTotalEmployeeCount() {
		List<EmployeePayrollData> employeePayrollData = employeePayrollJDBCService
				.readEmployeePayrollDataFromDB(EmployeePayrollJDBCService.IOService.DB_IO);
		assertEquals(4, employeePayrollData.size());
		System.out.println("Total employee in employee payroll database :" + employeePayrollData.size());
	}

}
