package co.edu.uniremington.payroll.service;

import co.edu.uniremington.payroll.model.Employee;
import co.edu.uniremington.payroll.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional; // ¡Importante para los repositorios!

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio utilizando Mockito y JUnit 6 en Java Puro.
 */
@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private PayrollService service;

    @Test
    @DisplayName("Debe calcular correctamente el salario para contrato FULL_TIME")
    void calculateNetSalary_FullTime() {
        // 1. Arrange: Crear empleado y configurar el mock
        Employee employee = new Employee(3L, "Pedro Perez", "pedro@email.com", "FULL_TIME", BigDecimal.valueOf(3000), 48, 5);

        // Le decimos a Mockito: "Cuando busquen el ID 3, devuelve a Pedro"
        when(repository.findById(3l)).thenReturn(Optional.of(employee));

        // 2. Act: Llamar al servicio
        BigDecimal salary = service.calculateNetSalary(3L);

        // 3. Assert: Verificar salario (3000 - 8% = 2760)
        assertEquals(new BigDecimal("2760.00"), salary);

        // 4. Assert: Verificar que sí se consultó la base de datos (el mock)
        verify(repository, Mockito.times(1)).findById(3L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el empleado no existe")
    void calculateNetSalary_NotFound() {
        // 1. Arrange: Configurar el mock para que devuelva vacío (Optional.empty)
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // 2. Act & Assert: Usar assertThrows para verificar que "explota" como esperamos
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateNetSalary(99L);
        });
    }

    // =========================================================================
    // NUEVAS FUNCIONALIDADES PARA PRACTICAR MOCKITO Y ASERCIONES
    // =========================================================================

    @Test
    @DisplayName("Bono de Antigüedad: Debe calcular 5% por año para FULL_TIME")
    void calculateAntiquityBonus_FullTime() {
        // 1. Arrange: Empleado FULL_TIME, 3 años, salario 1000
        Employee employee = new Employee(1L, "Ana", "ana@email.com", "FULL_TIME", BigDecimal.valueOf(1000), 40, 3);
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        // Act
        BigDecimal bonus = service.calculateAntiquityBonus(employee.getId());

        // Assert: 5% de 1000 es 50. Por 3 años = 150.
        assertEquals(new BigDecimal("150.00"), bonus);
    }

    @Test
    @DisplayName("Bono de Antigüedad: Debe retornar 0 para empleados HOURLY")
    void calculateAntiquityBonus_Hourly_ReturnsZero() {
        // 1. Arrange: Empleado HOURLY
        Employee employee = new Employee(2L, "Juan", "juan@email.com", "HOURLY", BigDecimal.valueOf(1000), 40, 3);
        when(repository.findById(2L)).thenReturn(Optional.of(employee));

        // Act
        BigDecimal bonus = service.calculateAntiquityBonus(2L);

        // Assert: Debe ser 0
        assertEquals(BigDecimal.ZERO, bonus); // BigDecimal.ZERO es lo mismo que BigDecimal.valueOf(0)

        // Opcional: Verificar que se buscó en el repo
        verify(repository, Mockito.times(1)).findById(2L);
    }

    @Test
    @DisplayName("Penalidad: Debe restar $15.00 por día de retraso")
    void applyLatePenalty_CalculatesCorrectly() {
        // 1. Arrange: Valores simples. No hay mock porque el método no consulta la BD.
        BigDecimal salarioBase = BigDecimal.valueOf(100);
        int diasRetraso = 2; // 2 * 15 = 30 de penalidad

        // 2. Act
        BigDecimal resultado = service.applyLatePenalty(salarioBase, diasRetraso);

        // 3. Assert: 100 - 30 = 70
        assertEquals(new BigDecimal("70.00"), resultado);
    }

    @Test
    @DisplayName("Penalidad: El salario nunca debe ser menor a cero")
    void applyLatePenalty_NeverBelowZero() {
        // 1. Arrange: Gana 20, pero se retrasó 5 días (penalidad de 75)
        BigDecimal salarioBase = BigDecimal.valueOf(20);
        int diasRetraso = 5;

        // 2. Act
        BigDecimal resultado = service.applyLatePenalty(salarioBase, diasRetraso);

        // 3. Assert: Como 20 - 75 daría negativo, el método debe protegerlo y devolver 0
        assertEquals(BigDecimal.ZERO, resultado);
    }
}