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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para el servicio utilizando Mockito y JUnit 6 en Java Puro.
 * INSTRUCCIONES PARA EL ESTUDIANTE: Complete las pruebas vacías.
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
        // TODO: Estudiante:
        // 1. Arrange: Crear un empleado FULL_TIME y configurar el mock del repository.findById()
        Employee employee = new Employee(3L, "Pedro Perez", "pedro.perezgmail.com", "FULL_TIME", new BigDecimal(3000), 48, 5);

        when(repository.findById(3L)).thenReturn(Optional.of(employee));
        // 2. Act: Llamar a service.calculateNetSalary()
        BigDecimal salary = service.calculateNetSalary(employee.getId());
        // 3. Assert: Verificar con assertEquals que el salario neto es el correcto (Base - 8%).
        // 3. Assert: Verificar salario (3000 - 8% = 2760)
        assertEquals(new BigDecimal("2760.00"), salary);
        // 4. Assert: Verificar que el método findById() del repositorio fue llamado con verify().
        verify(repository, Mockito.times(1)).findById(3L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el empleado no existe")
    void calculateNetSalary_NotFound() {
        // TODO: Estudiante:
        // 1. Arrange: Configurar el mock para que findById devuelva Optional.empty()
        when(repository.findById(99L)).thenReturn(Optional.empty());
        // 2. Act & Assert: Usar assertThrows(IllegalArgumentException.class, ...)
        assertThrows(IllegalArgumentException.class,()->{
            service.calculateNetSalary(99L);

        });
    }

    // =========================================================================
    // NUEVAS FUNCIONALIDADES PARA PRACTICAR MOCKITO Y ASERCIONES
    // =========================================================================

    @Test
    @DisplayName("Bono de Antigüedad: Debe calcular 5% por año para FULL_TIME")
    void calculateAntiquityBonus_FullTime() {
        // TODO: Estudiante:
        // 1. Arrange: Empleado FULL_TIME, 3 años, salario 1000
        Employee employee = new Employee(1L, "Ana", "ana@email.com", "FULL_TIME", BigDecimal.valueOf(1000), 40, 3);
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        // 3. Act
        BigDecimal bonus = service.calculateAntiquityBonus(employee.getId());

        // 4. Assert: 5% de 1000 es 50. Por 3 años = 150.
        assertEquals(new BigDecimal("150.00"), bonus);

    }

    @Test
    @DisplayName("Bono de Antigüedad: Debe retornar 0 para empleados HOURLY")
    void calculateAntiquityBonus_Hourly_ReturnsZero() {
        // 1. Arrange: Empleado HOURLY
        Employee employee = new Employee(2L, "Juan", "juan@email.com", "HOURLY", BigDecimal.valueOf(1000), 40, 3);
        when(repository.findById(2L)).thenReturn(Optional.of(employee));

        // 2. Act
        BigDecimal bonus = service.calculateAntiquityBonus(2L);

        // 3. Assert: Debe ser 0
        assertEquals(BigDecimal.ZERO, bonus); // BigDecimal.ZERO es lo mismo que BigDecimal.valueOf(0)

        // Opcional: Verificar que se buscó en el repo
        verify(repository).findById(2L);
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
