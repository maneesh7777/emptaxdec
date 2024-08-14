package com.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.image.model.Employee;
import com.image.model.TaxDeductionResponse;
import com.image.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new EmployeeController()).build();
    }

    @Test
    void testAddEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeId("E001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDoj(LocalDate.of(2023, Month.JANUARY, 1));
        employee.setSalary(50000);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee added successfully"));
    }

    @Test
    void testCalculateTaxDeduction() throws Exception {
        TaxDeductionResponse response1 = new TaxDeductionResponse();
        response1.setEmployeeCode("E001");
        response1.setFirstName("John");
        response1.setLastName("Doe");
        response1.setYearlySalary(150000);
        response1.setTaxAmount(5000);
        response1.setCessAmount(0);

        TaxDeductionResponse response2 = new TaxDeductionResponse();
        response2.setEmployeeCode("E002");
        response2.setFirstName("Jane");
        response2.setLastName("Smith");
        response2.setYearlySalary(300000);
        response2.setTaxAmount(25000);
        response2.setCessAmount(0);

        List<TaxDeductionResponse> taxDeductionResponses = Arrays.asList(response1, response2);

        when(employeeService.calculateTaxDeduction()).thenReturn(taxDeductionResponses);

        mockMvc.perform(get("/api/employees/tax-deduction")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].employeeCode", is("E001")))
                .andExpect(jsonPath("$[1].employeeCode", is("E002")));
    }
}
