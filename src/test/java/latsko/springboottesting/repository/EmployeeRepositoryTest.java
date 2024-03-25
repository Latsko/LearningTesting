package latsko.springboottesting.repository;

import latsko.springboottesting.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    public void setUp() {
        employee = createEmployee("John", "Doe", "simple@gmail.com");
    }

    @DisplayName("Junit test for save employee operation")
    @Test
    public void givenEmployeeObject_whenSave_thenReturnSavedEmployee() {
        //given
        //when
        Employee savedEmployee = employeeRepository.save(employee);

        //then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0);
    }

    @DisplayName("Junit test for get all employees operation")
    @Test
    public void givenEmployeeList_whenFindAll_thenEmployeeList() {
        //given
        Employee employee1 = createEmployee("Dave", "Johnson", "address1@gmail.com");
        Employee employee2 = createEmployee("Cassius", "Clay", "address2@gmail.com");
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        //when
        List<Employee> employees = employeeRepository.findAll();

        //then
        assertThat(employees).isNotEmpty();
        assertThat(employees).hasSize(2);
    }

    @DisplayName("Junit test for get employee by id operation")
    @Test
    public void givenEmployee_whenFindById_thenReturnEmployeeById() {
        //given
        employeeRepository.save(employee);

        //when
        Employee foundEmployee = employeeRepository.findById(1L).orElse(null);

        //then
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getId()).isEqualTo(1L);
    }

    @DisplayName("Junit test for get employee by email operation")
    @Test
    public void givenEmail_whenFindByEmail_thenReturnEmployeeByEmail() {
        //given
        employeeRepository.save(employee);

        //when
        Optional<Employee> foundEmployee = employeeRepository.findByEmail(employee.getEmail());

        //then
        assertThat(foundEmployee).isNotNull();
    }

    @DisplayName("Junit test for update employee operation")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        //given
        employeeRepository.save(employee);

        //when
        Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
        savedEmployee.setEmail("changedEmail@gmail.com");
        savedEmployee.setLastName("Changed");
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        //then
        assertThat(updatedEmployee.getEmail()).isEqualTo("changedEmail@gmail.com");
        assertThat(updatedEmployee.getLastName()).isEqualTo("Changed");
    }

    @DisplayName("Junit test for delete employee operation")
    @Test
    public void givenEmployeeObject_whenDelete_thenRemoveEmployee() {
        //given
        employeeRepository.save(employee);

        //when
        employeeRepository.deleteById(employee.getId());
        Optional<Employee> employeeOptional = employeeRepository.findById(employee.getId());

        //then
        assertThat(employeeOptional).isEmpty();
    }

    @DisplayName("Junit test for custom query using JPQL with index")
    @Test
    public void givenFirstNameAndLastName_whenFindByJPQL_thenReturnEmployeeObject() {
        //given
       employeeRepository.save(employee);
        String firstName = "John";
        String lastName = "Doe";

        //when
        Employee foundEmployee = employeeRepository.findByJPQL(firstName, lastName);

        //then
        assertThat(foundEmployee).isNotNull();
    }

    @DisplayName("Junit test for custom query using JPQL with named params")
    @Test
    public void givenFirstNameAndLastName_whenFindByJPQLNamedParams_thenReturnEmployeeObject() {
        //given
        employeeRepository.save(employee);
        String firstName = "John";
        String lastName = "Doe";

        //when
        Employee foundEmployee = employeeRepository.findByJPQLNamedParams(firstName, lastName);

        //then
        assertThat(foundEmployee).isNotNull();
    }

    @DisplayName("Junit test for custom query using native query with index")
    @Test
    public void givenFirstNameAndLastName_whenFindByNativeSQL_thenReturnEmployeeObject() {
        //given
        employeeRepository.save(employee);
        String firstName = "John";
        String lastName = "Doe";

        //when
        Employee foundEmployee = employeeRepository.findByNativeSQL(firstName, lastName);

        //then
        assertThat(foundEmployee).isNotNull();
    }

    @DisplayName("Junit test for custom query using native query with named params")
    @Test
    public void givenFirstNameAndLastName_whenFindByNativeSQLNamedParams_thenReturnEmployeeObject() {
        //given
        employeeRepository.save(employee);
        String firstName = "John";
        String lastName = "Doe";

        //when
        Employee foundEmployee = employeeRepository.findByNativeSQLNamed(firstName, lastName);

        //then
        assertThat(foundEmployee).isNotNull();
    }

    private Employee createEmployee(String firstName, String lastName, String email) {
        return Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }

}