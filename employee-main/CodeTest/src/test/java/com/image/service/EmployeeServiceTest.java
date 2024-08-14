package com.image.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.image.dao.EmployeeRepository;
import com.image.model.Employee;
import com.image.model.TaxDeductionResponse;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository empRepo;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeId("E001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDoj(LocalDate.of(2023, Month.JANUARY, 1));
        employee.setSalary(50000);

        // Act
        employeeService.addEmployee(employee);

        // Assert
        verify(empRepo, times(1)).save(employee);
    }

    @Test
    void testCalculateTaxDeduction() {
        Employee emp1 = new Employee();
        emp1.setEmployeeId("E001");
        emp1.setFirstName("John");
        emp1.setLastName("Doe");
        emp1.setDoj(LocalDate.of(2023, Month.JANUARY, 1));
        emp1.setSalary(50000);

        Employee emp2 = new Employee();
        emp2.setEmployeeId("E002");
        emp2.setFirstName("Jane");
        emp2.setLastName("Smith");
        emp2.setDoj(LocalDate.of(2023, Month.JANUARY, 1));
        emp2.setSalary(100000);

        when(empRepo.findAll()).thenReturn(Arrays.asList(emp1, emp2));

        List<TaxDeductionResponse> results = employeeService.calculateTaxDeduction();

        assertNotNull(results);
        assertEquals(2, results.size());

        TaxDeductionResponse response1 = results.get(0);
        assertEquals("E001", response1.getEmployeeCode());
        assertEquals("John", response1.getFirstName());
        assertEquals("Doe", response1.getLastName());
        assertEquals(50000 * 3, response1.getYearlySalary()); // 3 months worked
        assertEquals(0.05 * (50000 * 3 - 250000), response1.getTaxAmount());
        assertEquals(0, response1.getCessAmount());

        TaxDeductionResponse response2 = results.get(1);
        assertEquals("E002", response2.getEmployeeCode());
        assertEquals("Jane", response2.getFirstName());
        assertEquals("Smith", response2.getLastName());
        assertEquals(100000 * 3, response2.getYearlySalary()); // 3 months worked
        assertEquals(0.1 * (100000 * 3 - 500000) + 12500, response2.getTaxAmount());
        assertEquals(0, response2.getCessAmount());
    }
}
