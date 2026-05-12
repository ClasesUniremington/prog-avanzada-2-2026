package co.edu.uniremington.payroll.controller;

import co.edu.uniremington.payroll.model.Employee;
import co.edu.uniremington.payroll.repository.EmployeeRepository;
import co.edu.uniremington.payroll.repository.InMemoryEmployeeRepository;
import co.edu.uniremington.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integración manual (Java Puro).
 * Se prueban todas las capas conectadas: Controller -> Service -> Repository (InMemory).
 * INSTRUCCIONES PARA EL ESTUDIANTE: Implemente la prueba faltante.
 */
class PayrollIntegrationTest {

    private PayrollController controller;

    @BeforeEach
    void setUp() {
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        PayrollService service = new PayrollService(repository);
        controller = new PayrollController(service);
    }

    @Test
    @DisplayName("Debe integrar todas las capas y procesar exitosamente la creación y cálculo de salario")
    void testCompleteFlow() {
        // TODO: Estudiante:
        // 1. Instanciar un empleado (ej. HOURLY con $25.00 y 40 horas).
        // 2. Llamar a controller.createEmployee() y verificar (assertTrue) que retorne "exitosamente".
        // 3. Llamar a controller.getNetSalary() para el ID correspondiente.
        // 4. Verificar que el resultado de salario contenga la suma correcta (ej. "1000.00").
    }
}
