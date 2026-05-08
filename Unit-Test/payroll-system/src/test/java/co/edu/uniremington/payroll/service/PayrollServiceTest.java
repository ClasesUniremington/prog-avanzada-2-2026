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
import java.math.BigInteger;
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
        Employee employee = new Employee(3L, "pedro perez", "pedro@gmail.com", "FULL_TIME", new BigDecimal(3000),30,5);
        Mockito.when(repository.findById(3L)).thenReturn(Optional.of(employee));
        // 2. Act: Llamar a service.calculateNetSalary()
        BigDecimal salary = service.calculateNetSalary(employee.getId());
        // 3. Assert: Verificar con assertEquals que el salario neto es el correcto (Base - 8%).
        assertEquals(new BigDecimal("2760.00"),salary);
        // 4. Assert: Verificar que el médo findById() del repositorio fue llamado con verify().
        verify(repository, Mockito.times(1)).findById(3L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el empleado no existe")
    void calculateNetSalary_NotFound() {
        // TODO: Estudiante:
        // 1. Arrange: Configurar el mock para que findById devuelva Optional.empty()
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());
        // 2. Act & Assert: Usar assertThrows(IllegalArgumentException.class, ...)
        assertThrows(IllegalArgumentException.class, () -> service.calculateNetSalary(99L));
    }

    // =========================================================================
    // NUEVAS FUNCIONALIDADES PARA PRACTICAR MOCKITO Y ASERCIONES
    // =========================================================================

    @Test
    @DisplayName("Bono de Antigüedad: Debe calcular 5% por año para FULL_TIME")
    void calculateAntiquityBonus_FullTime() {
        // TODO: Estudiante:
        // 1. Arrange: Crear un empleado FULL_TIME con 3 años de antigüedad y salario base 1000.
        Employee employee = new Employee(4L, "pedro perez", "pedro@gmail.com", "FULL_TIME", new BigDecimal(1000),30,3);

        // 2. Configurar el mock del repositorio para que retorne este empleado.
        Mockito.when(repository.findById(4L)).thenReturn(Optional.of(employee));
        // 3. Act: Llamar a calculateAntiquityBonus()
        BigDecimal bonus = service.calculateAntiquityBonus(employee.getId());

        // 4. Assert: Verificar que el bono sea $150.00 (5% de 1000 = 50 * 3 años).
        assertEquals(new BigDecimal("150.00"), bonus);

        verify(repository, Mockito.times(1)).findById(4L);
    }

    @Test
    @DisplayName("Bono de Antigüedad: Debe retornar 0 para empleados HOURLY")
    void calculateAntiquityBonus_Hourly_ReturnsZero() {
        // TODO: Estudiante:
        // 1. Arrange: Crear empleado HOURLY y configurar el mock.
        Employee employee = new Employee(3L, "pedro perez", "pedro@gmail.com", "HOURLY", new BigDecimal(1000),30,3);

        // 2. Act & Assert: Verificar que el bono sea cero.
        Mockito.when(repository.findById(3L)).thenReturn(Optional.of(employee));

        BigDecimal bonus = service.calculateAntiquityBonus(employee.getId());
        // 3. Opcional: Usar Mockito verify() para asegurar que sí se buscó el empleado en el repositorio.
        assertEquals(new BigDecimal("0"), bonus);
    }

    @Test
    @DisplayName("Penalidad: Debe restar $15.00 por día de retraso")
    void applyLatePenalty_CalculatesCorrectly() {
        // TODO: Estudiante:
        // 1. Arrange: (No se necesita mock aquí porque el método no usa el repositorio)
        //    Definir un salario inicial de $100.00 y 2 días de retraso.
        // 2. Act: Llamar a applyLatePenalty(salario, dias)
        BigDecimal result = service.applyLatePenalty(new BigDecimal("100.00"), 2);
        // 3. Assert: El resultado debe ser $70.00.
        assertEquals(new BigDecimal("70.00"), result);
    }

    @Test
    @DisplayName("Penalidad: El salario nunca debe ser menor a cero")
    void applyLatePenalty_NeverBelowZero() {
        // TODO: Estudiante:
        // 1. Probar qué pasa si el empleado gana $20.00 pero llegó tarde 5 días (penalidad de 75).
        BigDecimal result = service.applyLatePenalty(new BigDecimal("20.00"), 5);
        // 2. Verificar que el resultado sea 0.00, no un valor negativo.
        assertEquals(new BigDecimal("0"), result);
    }
}
