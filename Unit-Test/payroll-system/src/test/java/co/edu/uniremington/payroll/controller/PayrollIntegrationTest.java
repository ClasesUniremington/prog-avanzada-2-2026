package co.edu.uniremington.payroll.controller;

import co.edu.uniremington.payroll.model.Employee;
import co.edu.uniremington.payroll.repository.InMemoryEmployeeRepository;
import co.edu.uniremington.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pruebas de Integración - Flujo Completo")
class PayrollIntegrationTest {

    private PayrollController controller;
    private InMemoryEmployeeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEmployeeRepository();
        PayrollService service = new PayrollService(repository);
        controller = new PayrollController(service);
    }

    @Test
    @DisplayName("Flujo completo: Crear empleado FULL_TIME y calcular su salario")
    void testCompleteFlow_FullTimeEmployee() {
        Employee employee = new Employee(
                10L, "Luis Fernández", "luis@email.com", "FULL_TIME",
                new BigDecimal("2500.00"), 30, 2
        );
        controller.createEmployee(employee);
        String resultado = controller.getNetSalary(10L);
        assertEquals("El salario neto calculado es: $2300.00", resultado);
    }
}