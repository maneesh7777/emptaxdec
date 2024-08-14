package com.image.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.image.dao.EmployeeRepository;
import com.image.model.Employee;
import com.image.model.TaxDeductionResponse;

import ch.qos.logback.classic.Logger;

@Service
public class EmployeeService {

	private static final Logger lOG = (Logger) LoggerFactory.getLogger(EmployeeService.class);

	@Autowired
	private EmployeeRepository empRepo;

	public void addEmployee(Employee employee) {

		empRepo.save(employee);
	}

	public List<TaxDeductionResponse> calculateTaxDeduction() {


		try {

			return employees.stream()
					.map(emp -> {
						LocalDate doj = LocalDate.parse(emp.getDoj().toString());

						int monthsWorked = (int) Period.between(doj, financialYearEnd.plusDays(1)).toTotalMonths();

						double totalSalary = emp.getSalary().doubleValue() * monthsWorked;
						double tax = calculateTax(totalSalary);
						double cess = (totalSalary > 2500000) ? 0.02 * (totalSalary - 2500000) : 0;

						TaxDeductionResponse response = new TaxDeductionResponse();
						response.setEmployeeCode(emp.getEmployeeId());
						response.setFirstName(emp.getFirstName());
						response.setLastName(emp.getLastName());
						response.setYearlySalary(totalSalary);
						response.setTaxAmount(tax);
						response.setCessAmount(cess);
						return response;
					})
					.collect(Collectors.toList());
		} catch (Exception ex) {
			lOG.error("exception in employee service : calculateTaxDeduction:{}", ex);
		}



	}

	private double calculateTax(double yearlySalary) {
		if (yearlySalary <= 250000) {
			return 0;
		} else if (yearlySalary <= 500000) {
			return 0.05 * (yearlySalary - 250000);
		} else if (yearlySalary <= 1000000) {
			return 0.1 * (yearlySalary - 500000) + 12500;
		} else {
			return 0.2 * (yearlySalary - 1000000) + 12500 + 50000;
		}
	}


}
