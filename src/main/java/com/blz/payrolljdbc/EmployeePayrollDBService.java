package com.blz.payrolljdbc;

import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.Connection;

public class EmployeePayrollDBService {
	private static Connection getConnect() throws SQLException {
		Connection connection;
		String[] dbInfo = dbProperties();
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_service?useSSL=false", dbInfo[0],
				dbInfo[1]);
		System.out.println(connection);
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

	public List<EmployeePayrollData> readData() {
		String sql = "SELECT * FROM employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = getConnect();) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				int id = result.getInt("EmployeeId");
				String name = result.getString("EmployeeName");
				double salary = result.getDouble("Salary");
				LocalDate startDate = result.getDate("StartDate").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

}
