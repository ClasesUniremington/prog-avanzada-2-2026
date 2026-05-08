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

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private PayrollService service;

    @Test
    @DisplayName("Debe calcular correctamente el salario para contrato FULL_TIME")
    void calculateNetSalary_FullTime() {
        Employee employee = new Employee(
                3L, "Pedro_Perez", "ejemplo@gmail.com", "FULL_TIME",
                new BigDecimal(3000), 48, 5
        );
        Mockito.when(repository.findById(3L)).thenReturn(Optional.of(employee));
        BigDecimal salary = service.calculateNetSalary(employee.getId());
        assertEquals(new BigDecimal("2760.00"), salary);
        Mockito.verify(repository, Mockito.times(1)).findById(3L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el empleado no existe")
    void calculateNetSalary_NotFound() {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.calculateNetSalary(99L));
    }

    @Test
    @DisplayName("Bono de Antigüedad: Debe calcular 5% por año para FULL_TIME")
    void calculateAntiquityBonus_FullTime() {
        Employee employee = new Employee(
                3L, "Pedro_Perez", "ejemplo@gmail.com", "FULL_TIME",
                new BigDecimal(1000), 48, 3
        );
        Mockito.when(repository.findById(3L)).thenReturn(Optional.of(employee));
        BigDecimal bonus = service.calculateAntiquityBonus(employee.getId());
        assertEquals(new BigDecimal("150.00"), bonus);
    }

    @Test
    @DisplayName("Bono de Antigüedad: Debe retornar 0 para empleados HOURLY")
    void calculateAntiquityBonus_Hourly_ReturnsZero() {
        // 1. Arrange
        Employee employee = new Employee(
                5L, "Ana_Lopez", "ana@gmail.com", "HOURLY",
                new BigDecimal("1000"), 40, 3
        );
        Mockito.when(repository.findById(5L)).thenReturn(Optional.of(employee));
        // 2. Act
        BigDecimal bonus = service.calculateAntiquityBonus(employee.getId());
        // 3. Assert
        assertEquals(new BigDecimal("0"), bonus);
        // 4. Verify
        Mockito.verify(repository, Mockito.times(1)).findById(5L);
    }

    @Test
    @DisplayName("Penalidad: Debe restar $15.00 por día de retraso")
    void applyLatePenalty_CalculatesCorrectly() {
        // 1. Arrange
        BigDecimal salario = new BigDecimal("100.00");
        int diasRetraso = 2;
        // 2. Act
        BigDecimal resultado = service.applyLatePenalty(salario, diasRetraso);
        // 3. Assert: 100 - (15 * 2) = 70.00
        assertEquals(new BigDecimal("70.00"), resultado);
    }

    @Test
    @DisplayName("Penalidad: El salario nunca debe ser menor a cero")
    void applyLatePenalty_NeverBelowZero() {
        // 1. Arrange
        BigDecimal salario = new BigDecimal("20.00");
        int diasRetraso = 5;
        // 2. Act
        BigDecimal resultado = service.applyLatePenalty(salario, diasRetraso);
        // 3. Assert
        assertEquals(new BigDecimal("0"), resultado);
    }
}