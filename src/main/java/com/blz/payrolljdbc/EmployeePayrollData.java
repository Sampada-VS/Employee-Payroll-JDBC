package com.blz.payrolljdbc;

import java.time.LocalDate;

public class EmployeePayrollData {
	public int id;
	public String name;
	public double salary;
	public LocalDate startDate;
	public String[] department;
	public char gender;

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.startDate = startDate;
	}
	public EmployeePayrollData(int id, String name,String[] department,char gender, double salary, LocalDate startDate) {
		this(id, name, salary, startDate);
		this.department=department;
		this.gender=gender;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EmployeePayrollData that = (EmployeePayrollData) o;
		return id == that.id && Double.compare(that.salary, salary) == 0 && name.equals(that.name);
	}
}