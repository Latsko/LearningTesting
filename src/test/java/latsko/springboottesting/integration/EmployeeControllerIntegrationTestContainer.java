package latsko.springboottesting.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import latsko.springboottesting.model.Employee;
import latsko.springboottesting.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerIntegrationTestContainer extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private Employee employee1;
    private Employee employee2;
    private long employeeId;

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
        employeeId = 1L;
        employee1 = createEmployee("Name1", "Surname1", "email1@gmail.com");
        employee2 = createEmployee("Name2", "Surname2", "email2@gmail.com");

    }

    @DisplayName("Integration test for createEmployee REST API method")
    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        //given

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

    @DisplayName("Integration test for getAllEmployees REST API method")
    @Test
    public void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
        //given
        List<Employee> listOfEmployees = new ArrayList<>();
        listOfEmployees.add(employee1);
        listOfEmployees.add(employee2);
        employeeRepository.saveAll(listOfEmployees);

        //when
        ResultActions response = mockMvc.perform(get("/api/employees"));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(listOfEmployees.size())));
    }

    @DisplayName("Integration test for getEmployeeById REST API method (positive scenario)")
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {
        //given
        employeeRepository.save(employee1);

        //when
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee1.getId()));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(employee1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee1.getLastName())))
                .andExpect(jsonPath("$.email", is(employee1.getEmail())));
    }

    @DisplayName("Integration test for getEmployeeById REST API method (negative scenario)")
    @Test
    public void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {
        //given
        employeeRepository.save(employee1);

        //when
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Integration test for updateEmployee REST API method (positive scenario)")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnEmployeeObject() throws Exception {
        //given
        employeeRepository.save(employee1);
        Employee updatedEmployee = createEmployee("updatedName", "updatedSurname", "updated@gmail.com");

        //when
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employee1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
    }

    @DisplayName("Integration test for updateEmployee REST API method (negative scenario)")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnEmpty() throws Exception {
        //given
        employeeRepository.save(employee1);
        Employee updatedEmployee = createEmployee("updatedName", "updatedSurname", "updated@gmail.com");

        //when
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId+1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Integration test for deleteEmployee REST API method")
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnOk() throws Exception {
        //given
        employeeRepository.save(employee1);

        //when
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employee1.getId()));

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
