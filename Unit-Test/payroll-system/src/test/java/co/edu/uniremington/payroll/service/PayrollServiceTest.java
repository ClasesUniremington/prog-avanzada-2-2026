package co.edu.uniremington.payroll.service;

import co.edu.uniremington.payroll.model.Employee;
import co.edu.uniremington.payroll.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        // 1. Arrange: Crear empleado FULL_TIME
        Employee employee = new Employee(
                3L,
                "pedro perez",
                "pedro@gmail.com",
                "FULL_TIME",
                new BigDecimal("3000"),  // Salario base
                30,
                5
        );

        // Configurar el mock
        when(repository.findById(3L)).thenReturn(Optional.of(employee));

        // 2. Act: Llamar al servicio
        BigDecimal salary = service.calculateNetSalary(employee.getId());

        // 3. Assert: Salario neto = 3000 - 8% = 2760
        assertEquals(new BigDecimal("2760.00"), salary);

        // 4. Verificar que se llamó al repositorio
        verify(repository, times(1)).findById(3L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el empleado no existe")
    void calculateNetSalary_NotFound() {
        // 1. Arrange: Configurar mock para que devuelva Optional.empty()
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // 2. Act & Assert: Verificar que lanza excepción
        assertThrows(RuntimeException.class, () -> {
            service.calculateNetSalary(99L);
        });

        // Verificar que se intentó buscar
        verify(repository).findById(99L);
    }

    // =========================================================================
    // NUEVAS FUNCIONALIDADES PARA PRACTICAR MOCKITO Y ASERCIONES
    // =========================================================================

    @Test
    @DisplayName("Bono de Antigüedad: Debe calcular 5% por año para FULL_TIME")
    void calculateAntiquityBonus_FullTime() {
        // 1. Arrange: Crear empleado FULL_TIME con 3 años y salario 1000
        Employee employee = new Employee(
                1L,
                "juan perez",
                "juan@gmail.com",
                "FULL_TIME",
                new BigDecimal("1000"),
                30,
                3  // 3 años de antigüedad
        );

        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        // 2. Act: Llamar al método de bono
        BigDecimal bonus = service.calculateAntiquityBonus(1L);

        // 3. Assert: 5% por año * 3 años = 15% de 1000 = 150
        assertEquals(new BigDecimal("150.00"), bonus);

        // 4. Verify
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Bono de Antigüedad: Debe retornar 0 para empleados HOURLY")
    void calculateAntiquityBonus_Hourly_ReturnsZero() {
        // 1. Arrange: Crear empleado HOURLY
        Employee employee = new Employee(
                2L,
                "maria rodriguez",
                "maria@gmail.com",
                "HOURLY",
                new BigDecimal("0"),  // Los HOURLY no tienen salario base fijo
                45,  // horas trabajadas
                5    // años de antigüedad (no importa para HOURLY)
        );

        when(repository.findById(2L)).thenReturn(Optional.of(employee));

        // 2. Act
        BigDecimal bonus = service.calculateAntiquityBonus(2L);

        // 3. Assert: Debe ser CERO
        assertEquals(BigDecimal.ZERO, bonus);

        // 4. Verify
        verify(repository).findById(2L);
    }

    @Test
    @DisplayName("Penalidad: Debe restar $15.00 por día de retraso")
    void applyLatePenalty_CalculatesCorrectly() {
        // 1. Arrange: Salario inicial $100, 2 días de retraso
        BigDecimal salarioInicial = new BigDecimal("100.00");
        int diasRetraso = 2;

        // 2. Act: Aplicar penalidad
        BigDecimal resultado = service.applyLatePenalty(salarioInicial, diasRetraso);

        // 3. Assert: 100 - (15 * 2) = 100 - 30 = 70
        assertEquals(new BigDecimal("70.00"), resultado);
    }

    @Test
    @DisplayName("Penalidad: El salario nunca debe ser menor a cero")
    void applyLatePenalty_NeverBelowZero() {
        // 1. Arrange: Salario bajo $20, 5 días de retraso (penalidad 75)
        BigDecimal salarioInicial = new BigDecimal("20.00");
        int diasRetraso = 5;

        // 2. Act
        BigDecimal resultado = service.applyLatePenalty(salarioInicial, diasRetraso);

        // 3. Assert: No puede ser negativo, debe ser 0
        // 20 - 75 = -55, pero el método debe devolver 0
        assertEquals(BigDecimal.ZERO, resultado);

        // También probar que no es negativo
        assertTrue(resultado.compareTo(BigDecimal.ZERO) >= 0);
    }
}