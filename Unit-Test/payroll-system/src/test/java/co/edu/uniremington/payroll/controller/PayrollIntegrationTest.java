package co.edu.uniremington.payroll.controller;

import co.edu.uniremington.payroll.model.Employee;
import co.edu.uniremington.payroll.repository.EmployeeRepository;
import co.edu.uniremington.payroll.repository.InMemoryEmployeeRepository;
import co.edu.uniremington.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        // Punto 1:
        // Se crea un empleado por horas con tarifa de $25.00 y 40 horas trabajadas.
        // Se usa el constructor vacio y los metodos set, igual que en el ejemplo del tutor.
        // Como no se asigna ID, el repositorio en memoria debe asignarle automaticamente el ID 1.
        Employee employee = new Employee();
        employee.setName("Juanito");
        employee.setEmail("juanito@email.com");
        employee.setContractType("HOURLY");
        employee.setBaseSalary(new BigDecimal("25.00"));
        employee.setHoursWorked(40);
        employee.setAntiquityYears(0);

        // Punto 2:
        // Se envia el empleado al controlador para probar el recorrido completo:
        // Controller -> Service -> Repository.
        // La respuesta debe indicar que el empleado fue creado exitosamente.
        String creationResponse = controller.createEmployee(employee);
        assertTrue(creationResponse.contains("exitosamente"),
                "La respuesta debe confirmar que el empleado fue creado exitosamente.");
        assertEquals(1L, employee.getId(),
                "El repositorio en memoria debe asignar automaticamente el ID 1 al primer empleado.");
        assertTrue(creationResponse.contains("ID: " + employee.getId()),
                "La respuesta debe mostrar el ID asignado por el repositorio en memoria.");

        // Validacion adicional del punto 2:
        // Se consulta la lista completa para comprobar que el empleado realmente quedo guardado.
        List<Employee> employees = controller.getAllEmployees();
        assertEquals(1, employees.size(),
                "Debe existir exactamente un empleado guardado en el repositorio.");
        assertEquals("Juanito", employees.getFirst().getName(),
                "El empleado guardado debe conservar el nombre enviado desde la prueba.");

        // Punto 3:
        // Se solicita al controlador el salario neto del empleado creado.
        // Para contratos HOURLY, el servicio calcula salario base por horas trabajadas.
        String salaryResponse = controller.getNetSalary(employee.getId());
        assertNotNull(salaryResponse,
                "La respuesta del salario no debe ser null.");
        assertNotEquals("Ocurrió un error inesperado al calcular el salario.", salaryResponse,
                "La respuesta no debe ser el mensaje de error inesperado.");

        // Punto 4:
        // Se verifica que el resultado sea $1000.00, porque 25.00 * 40 = 1000.00.
        assertTrue(salaryResponse.contains("1000.00"),
                "El salario neto debe ser 1000.00 para un empleado HOURLY de $25.00 por 40 horas.");
    }
}
