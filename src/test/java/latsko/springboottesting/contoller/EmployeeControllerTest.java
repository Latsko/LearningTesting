package latsko.springboottesting.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import latsko.springboottesting.model.Employee;
import latsko.springboottesting.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee1;
    private Employee employee2;
    private long employeeId;


    @BeforeEach
    public void setUp() {
        employeeId = 1L;
        employee1 = createEmployee("Name1", "Surname1", "email1@gmail.com");
        employee2 = createEmployee("Name2", "Surname2", "email2@gmail.com");
    }

    @DisplayName("Junit test for createEmployee REST API method")
    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        //given
        given(employeeService.saveEmployee(any(Employee.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        //when
        ResultActions response = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee1)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(employee1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee1.getLastName())))
                .andExpect(jsonPath("$.email", is(employee1.getEmail())));
    }

    @DisplayName("Junit test for getAllEmployees REST API method")
    @Test
    public void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
        //given
        List<Employee> listOfEmployees = new ArrayList<>();
        listOfEmployees.add(employee1);
        listOfEmployees.add(employee2);
        given(employeeService.getAllEmployees()).willReturn(listOfEmployees);

        //when
        ResultActions response = mockMvc.perform(get("/api/employees"));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(listOfEmployees.size())));
    }

    @DisplayName("Junit test for getEmployeeById REST API method (positive scenario)")
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {
        //given
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee1));

        //when
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(employee1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee1.getLastName())))
                .andExpect(jsonPath("$.email", is(employee1.getEmail())));
    }

    @DisplayName("Junit test for getEmployeeById REST API method (negative scenario)")
    @Test
    public void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {
        //given
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());

        //when
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Junit test for updateEmployee REST API method (positive scenario)")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnEmployeeObject() throws Exception {
        //given
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee1));
        given(employeeService.updateEmployee(any(Employee.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        Employee updatedEmployee = createEmployee("updatedName", "updatedSurname", "updated@gmail.com");

        //when
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
    }

    @DisplayName("Junit test for updateEmployee REST API method (negative scenario)")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnEmpty() throws Exception {
        //given
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());
        Employee updatedEmployee = createEmployee("updatedName", "updatedSurname", "updated@gmail.com");

        //when
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Junit test for deleteEmployee REST API method")
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnOk() throws Exception {
        //given
        willDoNothing().given(employeeService).deleteEmployee(anyLong());

        //when
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employeeId));

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

    private Employee createEmployee(String firstName, String lastName, String email) {
        return Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }

}