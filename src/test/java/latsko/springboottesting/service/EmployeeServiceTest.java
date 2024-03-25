package latsko.springboottesting.service;

import latsko.springboottesting.exception.ResourceNotFoundException;
import latsko.springboottesting.model.Employee;
import latsko.springboottesting.repository.EmployeeRepository;
import latsko.springboottesting.service.impl.EmployeeServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    private Employee employee;

    @BeforeEach
    public void setUp() {
        employee = createEmployee();
    }

    @DisplayName("Junit test for saveEmployee method")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObject() {
        //given
        given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.empty());
        given(employeeRepository.save(employee)).willReturn(employee);

        //when
        Employee savedEmployee = employeeService.saveEmployee(employee);

        //then
        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("Junit test for saveEmployee method which throws exception (Method #1)")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenThrowException() {
        //given
        given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.of(employee));

        //when
        ThrowableAssert.ThrowingCallable callable = () -> employeeService.saveEmployee(employee);

        //then
        assertThatThrownBy(callable).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee already exist with given email: simple@gmail.com");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @DisplayName("Junit test for saveEmployee method which throws exception (Method #2)")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenThrowExceptionInAssertThrows() {
        //given
        given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.of(employee));

        //when
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.saveEmployee(employee);
        });

        //then
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @DisplayName("Junit test for getAllEmployees method")
    @Test
    public void givenEmployeeList_whenGetAllEmployees_thenReturnEmployeeList() {
        //given
        given(employeeRepository.findAll()).willReturn(Collections.singletonList(employee));

        //when
        List<Employee> allEmployees = employeeService.getAllEmployees();

        //then
        assertThat(allEmployees)
                .isNotEmpty()
                .hasSize(1);
    }

    @DisplayName("Junit test for getAllEmployees method (given empty list)")
    @Test
    public void givenEmptyEmployeeList_whenGetAllEmployees_thenReturnEmptyEmployeeList() {
        //given
        given(employeeRepository.findAll()).willReturn(Collections.emptyList());

        //when
        List<Employee> allEmployees = employeeService.getAllEmployees();

        //then
        assertThat(allEmployees).isEmpty();
    }

    @DisplayName("Junit test for getEmployeeById method")
    @Test
    public void givenEmployeeObject_whenGetEmployeeById_thenReturnEmployee() {
        //given
        given(employeeRepository.findById(anyLong())).willReturn(Optional.of(employee));

        //when
        Employee foundEmployee = employeeService.getEmployeeById(1L).orElse(null);

        //then
        assertThat(foundEmployee).isNotNull();
    }

    @DisplayName("Junit test for updateEmployee method")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        //given
        given(employeeRepository.save(employee)).willReturn(employee);
        employee.setFirstName("ChangedName");
        employee.setLastName("ChangedLastName");
        employee.setEmail("changed@gmail.com");

        //when
        Employee updatedEmployee = employeeService.updateEmployee(employee);

        //then
        assertThat(updatedEmployee.getFirstName()).isEqualTo("ChangedName");
        assertThat(updatedEmployee.getLastName()).isEqualTo("ChangedLastName");
        assertThat(updatedEmployee.getEmail()).isEqualTo("changed@gmail.com");
    }

    @DisplayName("Junit test for deleteEmployee method")
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenNothing() {
        //given
        doNothing().when(employeeRepository).deleteById(anyLong());

        //when
        employeeService.deleteEmployee(1L);

        //then
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    private Employee createEmployee() {
        return Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("simple@gmail.com")
                .build();
    }

}