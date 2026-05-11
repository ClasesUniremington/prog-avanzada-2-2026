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
    //EJERCICIO 1
    @Test
    @DisplayName("Debe calcular correctamente el salario para contrato FULL_TIME")
    void calculateNetSalary_FullTime() {
        // Funcionamiento: simula un empleado FULL_TIME existente y valida que el servicio calcule
        // el salario neto aplicando el descuento del 8% sobre el salario base.
        // Arrange: crear un empleado FULL_TIME con salario base de 3000.00.
        Employee employee = new Employee(
                1L,                         // id
                "Ana",                      // name
                "ana@empresa.com",          // email
                "FULL_TIME",                // contractType
                new BigDecimal("3000.00"),  // baseSalary
                0,                          // hoursWorked
                1                           // antiquityYears
        );
        // Arrange: configurar el mock para que repository.findById(1L) retorne el empleado.
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(employee));
        // Act: llamar al metodo que calcula el salario neto.
        BigDecimal salary = service.calculateNetSalary(employee.getId());
        // Assert: verificar que el salario neto sea 2760.00 despues de descontar el 8%.
        assertEquals(new BigDecimal("2760.00"), salary);
        // Assert: verificar que el metodo findById() del repositorio fue llamado con el ID 1L.
        Mockito.verify(repository, Mockito.times(1)).findById(1L);
    }
    //EJERCICIO 2
    @Test
    @DisplayName("Debe lanzar excepción si el empleado no existe")
    void calculateNetSalary_NotFound() {
        // Funcionamiento: simula una busqueda sin resultados y verifica que el servicio lance
        // IllegalArgumentException cuando no encuentra el empleado solicitado.
        // TODO: Estudiante:
        // 1. Arrange: Configurar el mock para que findById devuelva Optional.empty()
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());
        // 2. Act & Assert: Usar assertThrows(IllegalArgumentException.class, ...)
        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateNetSalary(99L)
        );
        verify(repository).findById(99L);
    }

    // =========================================================================
    // NUEVAS FUNCIONALIDADES PARA PRACTICAR MOCKITO Y ASERCIONES
    // =========================================================================
    //EJERCICIO 2
    @Test
    @DisplayName("Bono de Antigüedad: Debe calcular 5% por año para FULL_TIME")
    void calculateAntiquityBonus_FullTime() {
        // Funcionamiento: simula un empleado FULL_TIME con 3 años de antigüedad y comprueba
        // que el bono corresponda al 5% del salario base por cada año trabajado.
        // TODO: Estudiante:
        // 1. Arrange: Crear un empleado FULL_TIME con 3 años de antigüedad y salario base 1000.
        Employee employee = new Employee(
                2L,                         // id
                "Carlos Pereza",                   // name
                "carlos.Pereza@empresa.com",       // email
                "FULL_TIME",                // contractType
                new BigDecimal("1000.00"),  // baseSalary
                0,                          // hoursWorked
                3                           // antiquityYears
        );
        // 2. Configurar el mock del repositorio para que retorne este empleado.
        Mockito.when(repository.findById(2L)).thenReturn(Optional.of(employee));
        // 3. Act: Llamar a calculateAntiquityBonus()
        BigDecimal bonus = service.calculateAntiquityBonus(2L);
        // 4. Assert: Verificar que el bono sea $150.00 (5% de 1000 = 50 * 3 años).
        assertEquals(new BigDecimal("150.00"), bonus);
        Mockito.verify(repository, Mockito.times(1)).findById(2L);
    }
    //EJERCICIO 3
    @Test
    @DisplayName("Bono de Antigüedad: Debe retornar 0 para empleados HOURLY")
    void calculateAntiquityBonus_Hourly_ReturnsZero() {
        // Funcionamiento: simula un empleado HOURLY y valida que no reciba bono de antigüedad,
        // porque la regla de negocio solo aplica este beneficio a contratos FULL_TIME.
        // TODO: Estudiante:
        // 1. Arrange: Crear empleado HOURLY y configurar el mock.
        Employee employee = new Employee(
                3L,                        // id
                "Laura",                   // name
                "laura@empresa.com",       // email
                "HOURLY",                  // contractType
                new BigDecimal("25.00"),   // baseSalary
                40,                        // hoursWorked
                5                          // antiquityYears
        );
        Mockito.when(repository.findById(3L)).thenReturn(Optional.of(employee));
        // 2. Act & Assert: Verificar que el bono sea cero.
        BigDecimal bonus = service.calculateAntiquityBonus(3L);
        assertEquals(BigDecimal.ZERO, bonus);
        // 3. Opcional: Usar Mockito verify() para asegurar que sí se buscó el empleado en el repositorio.
        Mockito.verify(repository).findById(3L);
    }
    //EJERCICIO 4
    @Test
    @DisplayName("Penalidad: Debe restar $15.00 por día de retraso")
    void applyLatePenalty_CalculatesCorrectly() {
        // Funcionamiento: calcula la penalidad por retrasos sin usar repositorio y verifica
        // que se descuenten $15.00 por cada dia reportado.
        // TODO: Estudiante:
        // 1. Arrange: (No se necesita mock aquí porque el metodo no usa el repositorio)
        //    Definir un salario inicial de $100.00 y 2 días de retraso.
        BigDecimal currentSalary = new BigDecimal("100.00");
        int lateDays = 2;
        // 2. Act: Llamar a applyLatePenalty(salario, dias)
        BigDecimal penalty = service.applyLatePenalty(currentSalary, lateDays);
        // 3. Assert: El resultado debe ser $70.00.
        assertEquals(new BigDecimal("70.00"), penalty);
    }
    //EJERCICIO 5
    @Test
    @DisplayName("Penalidad: El salario nunca debe ser menor a cero")
    void applyLatePenalty_NeverBelowZero() {
        // Funcionamiento: aplica una penalidad mayor que el salario disponible y comprueba
        // que el servicio limite el resultado a cero en lugar de devolver un valor negativo.
        // TODO: Estudiante:
        // 1. Probar qué pasa si el empleado gana $20.00 pero llegó tarde 5 días (penalidad de 75).
        BigDecimal currentSalary = new BigDecimal("20.00");
        int lateDays = 5;
        BigDecimal penality = service.applyLatePenalty(currentSalary, lateDays);
        // 2. Verificar que el resultado sea 0.00, no un valor negativo.
        assertEquals(BigDecimal.ZERO, penality);
    }
}
