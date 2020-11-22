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
import java.util.List;
import java.util.Properties;
import java.sql.Connection;

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
		String sql = "SELECT * FROM employee_payroll;";
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
				double salary = resultSet.getDouble("Salary");
				LocalDate startDate = resultSet.getDate("StartDate").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			throw new PayrollServiceException(e.getMessage());
		}
		return employeePayrollList;
	}

	private void preparedStatementForEmployeeData() throws PayrollServiceException {
		try {
			Connection connection = getConnect();
			String sql = "SELECT * FROM employee_payroll WHERE EmployeeName=?";
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
			String sql = "UPDATE employee_payroll SET Salary=? WHERE EmployeeName=?";
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

}
