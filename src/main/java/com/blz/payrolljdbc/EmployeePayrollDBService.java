package com.blz.payrolljdbc;

import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.sql.Connection;
import java.sql.Date;

public class EmployeePayrollDBService {
	private static EmployeePayrollDBService employeePayrollDBService;
	private PreparedStatement employeeDataStatement;

	private EmployeePayrollDBService() {

	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private static Connection getConnect() throws SQLException {
		Connection connection;
		String[] dbInfo = dbProperties();
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_service?useSSL=false", dbInfo[0],
				dbInfo[1]);
		return connection;
	}

	private static String[] dbProperties() {
		String[] dbInfo = { "", "" };
		Properties properties = new Properties();
		try (FileReader reader = new FileReader("DB.properties")) {
			properties.load(reader);
			dbInfo[0] = properties.getProperty("username");
			dbInfo[1] = properties.getProperty("password");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dbInfo;
	}

	public List<EmployeePayrollData> readData() throws PayrollServiceException {
		String sql = "SELECT * FROM employee_payroll WHERE is_Active=1;";
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	public List<EmployeePayrollData> getEmployeeData(String name) throws PayrollServiceException {
		List<EmployeePayrollData> employeePayrollList = null;
		if (this.employeeDataStatement == null)
			this.preparedStatementForEmployeeData();
		try {
			employeeDataStatement.setString(1, name);
			ResultSet resultSet = employeeDataStatement.executeQuery();
			employeePayrollList = this.getEmployeeData(resultSet);
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeeData(ResultSet resultSet) throws PayrollServiceException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("EmployeeId");
				String name = resultSet.getString("EmployeeName");
				String department = resultSet.getString("Department");
				String gender = resultSet.getString("Gender");
				double salary = resultSet.getDouble("Salary");
				LocalDate startDate = resultSet.getDate("StartDate").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, department, gender, salary, startDate));
			}
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
		return employeePayrollList;
	}

	private void preparedStatementForEmployeeData() throws PayrollServiceException {
		try {
			Connection connection = getConnect();
			String sql = "SELECT * FROM employee_payroll WHERE EmployeeName=? AND is_Active=1";
			employeeDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
	}

	public int updateEmployeeData(String name, double salary) throws PayrollServiceException {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws PayrollServiceException {
		try (Connection connection = getConnect()) {
			String sql = "UPDATE employee_payroll SET Salary=? WHERE EmployeeName=? AND is_Active=1";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
	}

	public int updateEmployeeDataUsingStatement(String name, double salary) throws PayrollServiceException {
		String sql = String.format("UPDATE employee_payroll SET Salary=%.2f WHERE EmployeeName='%s';", salary, name);
		try (Connection connection = getConnect()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
	}

	public int deleteEmployeeData(String name) throws PayrollServiceException {
		String sql = String.format("UPDATE employee_payroll SET is_Active=0 WHERE EmployeeName='%s';", name);
		try (Connection connection = getConnect()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate)
			throws PayrollServiceException {
		String sql = String.format(
				"SELECT * FROM employee_payroll WHERE StartDate BETWEEN '%s' AND '%s' AND is_Active=1;",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) throws PayrollServiceException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = getConnect()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeeData(result);
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
		return employeePayrollList;
	}

	public Map<String, Double> getSumSalaryByGender() throws PayrollServiceException {
		String sql = "SELECT Gender,SUM(Salary) as functionSalary FROM employee_payroll WHERE is_Active=1 GROUP BY Gender;";
		return this.getGenderToSalaryMap(sql);
	}

	public Map<String, Double> getAverageSalaryByGender() throws PayrollServiceException {
		String sql = "SELECT Gender,AVG(Salary) as functionSalary FROM employee_payroll  WHERE is_Active=1 GROUP BY Gender;";
		return this.getGenderToSalaryMap(sql);
	}

	public Map<String, Double> getMinSalaryByGender() throws PayrollServiceException {
		String sql = "SELECT Gender,MIN(Salary) as functionSalary FROM employee_payroll  WHERE is_Active=1 GROUP BY Gender;";
		return this.getGenderToSalaryMap(sql);
	}

	public Map<String, Double> getMaxSalaryByGender() throws PayrollServiceException {
		String sql = "SELECT Gender,MAX(Salary) as functionSalary FROM employee_payroll  WHERE is_Active=1 GROUP BY Gender;";
		return this.getGenderToSalaryMap(sql);
	}

	private Map<String, Double> getGenderToSalaryMap(String sql) throws PayrollServiceException {
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try (Connection connection = getConnect()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String gender = result.getString("Gender");
				double salary = result.getDouble("functionSalary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
		return genderToAverageSalaryMap;
	}

	public Map<String, Integer> getEmployeeCountByGender() throws PayrollServiceException {
		String sql = "SELECT Gender,COUNT(EmployeeName) FROM employee_payroll  WHERE is_Active=1 GROUP BY Gender;";
		Map<String, Integer> genderToAverageSalaryMap = new HashMap<>();
		try (Connection connection = getConnect()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String gender = result.getString("Gender");
				Integer count = result.getInt("COUNT(EmployeeName)");
				genderToAverageSalaryMap.put(gender, count);
			}
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
		return genderToAverageSalaryMap;
	}

	public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate startDate, String gender)
			throws PayrollServiceException {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll (EmployeeName,Gender,Salary,StartDate) VALUES ('%s','%s','%s','%s')",
				name, gender, salary, Date.valueOf(startDate));
		try (Connection connection = getConnect()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addEmployeeToPayroll(String name, String department, String gender, double salary,
			LocalDate startDate) throws PayrollServiceException {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = getConnect();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee_payroll (EmployeeName,Department,Gender,Salary,StartDate) VALUES ('%s','%s','%s',%.2f,'%s')",
					name, department, gender, salary, Date.valueOf(startDate));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				throw new PayrollServiceException(e1.getMessage());
			}
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double incomeTax = taxablePay * 0.1;
			double netPay = salary - incomeTax;
			String sql = String.format(
					"INSERT INTO payroll_details (EmployeeId,BasicPay,Deductions,TaxablePay,IncomeTax,NetPay) VALUES (%s,%s,%s,%s,%s,%s)",
					employeeId, salary, deductions, taxablePay, incomeTax, netPay);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1)
				employeePayrollData = new EmployeePayrollData(employeeId, name, department, gender, salary, startDate);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new PayrollServiceException(e1.getMessage());
			}
			throw new PayrollServiceException(e.getMessage());
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					throw new PayrollServiceException(e.getMessage());
				}
		}
		return employeePayrollData;
	}
}
