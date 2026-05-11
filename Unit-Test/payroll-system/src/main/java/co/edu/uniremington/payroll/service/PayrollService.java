package co.edu.uniremington.payroll.service;

import co.edu.uniremington.payroll.model.Employee;
import co.edu.uniremington.payroll.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Servicio que contiene la lógica de negocio (Java Puro).
 */
public class PayrollService {

    private final EmployeeRepository repository;

    // Inyección de dependencias a través del constructor
    public PayrollService(EmployeeRepository repository) {

        this.repository = repository;
    }

    public Employee saveEmployee(Employee employee) {
        return repository.save(employee);
    }

    public List<Employee> findAllEmployees() {
        return repository.findAll();
    }

    /**
     * Calcula el salario neto utilizando Java 21 Switch Expression.
     */
    public BigDecimal calculateNetSalary(Long employeeId) {
        Employee employee = repository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID: " + employeeId));

        BigDecimal base = employee.getBaseSalary() != null ? employee.getBaseSalary() : BigDecimal.ZERO;
        String type = employee.getContractType() != null ? employee.getContractType().toUpperCase() : "UNKNOWN";


        return switch (type) {
            case "FULL_TIME" -> {
                // Descuento del 8% (salud y pensión)
                BigDecimal deductions = base.multiply(new BigDecimal("0.08"));

                yield base.subtract(deductions).setScale(2, RoundingMode.HALF_UP);
            }
            case "HOURLY" -> {
                // Pago por horas trabajadas
                int hours = employee.getHoursWorked() != null ? employee.getHoursWorked() : 0;
                yield base.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
            }
            case "FREELANCE" -> base.setScale(2, RoundingMode.HALF_UP);
            default -> BigDecimal.ZERO;
        };
    }

    /**
     * Calcula el bono por antigüedad.
     * Solo aplica para empleados FULL_TIME. Reciben un 5% de su base por cada año de antigüedad.
     */
    public BigDecimal calculateAntiquityBonus(Long employeeId) {
        Employee employee = repository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        if (!"FULL_TIME".equalsIgnoreCase(employee.getContractType())) {
            return BigDecimal.ZERO; // Otros contratos no reciben bono de antigüedad
        }

        int years = employee.getAntiquityYears() != null ? employee.getAntiquityYears() : 0;
        if (years < 1) {
            return BigDecimal.ZERO;
        }

        // 5% del salario base por cada año
        BigDecimal bonusPerYear = employee.getBaseSalary().multiply(new BigDecimal("0.05"));
        return bonusPerYear.multiply(BigDecimal.valueOf(years)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Aplica una penalidad por llegadas tarde.
     * Se descuentan $15.00 por cada falta reportada. El salario nunca puede ser menor a 0.
     */
    public BigDecimal applyLatePenalty(BigDecimal currentSalary, int lateDays) {
        if (lateDays <= 0) {
            return currentSalary;
        }

        BigDecimal penalty = new BigDecimal("15.00").multiply(BigDecimal.valueOf(lateDays));
        BigDecimal result = currentSalary.subtract(penalty);

        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el bono navideño (Método para que los estudiantes creen la prueba desde cero).
     * Regla de negocio:
     * - Si es FULL_TIME, recibe el 50% de su salario base.
     * - Cualquier otro tipo de contrato recibe un bono fijo de $50.00.
     */
    public BigDecimal calculateChristmasBonus(Long employeeId) {
        Employee employee = repository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado para bono navideño"));

        if ("FULL_TIME".equalsIgnoreCase(employee.getContractType())) {
            BigDecimal bonus = employee.getBaseSalary().multiply(new BigDecimal("0.50"));
            return bonus.setScale(2, RoundingMode.HALF_UP);
        } else {
            return new BigDecimal("50.00");
        }
    }
}
