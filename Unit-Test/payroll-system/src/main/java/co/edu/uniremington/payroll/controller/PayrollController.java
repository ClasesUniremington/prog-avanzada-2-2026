package co.edu.uniremington.payroll.controller;

import co.edu.uniremington.payroll.model.Employee;
import co.edu.uniremington.payroll.service.PayrollService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador puro de Java. Actúa como intermediario entre la Vista (Main) y el Servicio.
 * (MVC sin frameworks).
 */
public class PayrollController {

    private final PayrollService service;

    public PayrollController(PayrollService service) {
        this.service = service;
    }

    public String createEmployee(Employee employee) {
        try {
            Employee saved = service.saveEmployee(employee);
            return "Empleado creado exitosamente con ID: " + saved.getId();
        } catch (Exception e) {
            return "Error al crear empleado: " + e.getMessage();
        }
    }

    public List<Employee> getAllEmployees() {
        return service.findAllEmployees();
    }

    public String getNetSalary(Long id) {
        try {
            BigDecimal salary = service.calculateNetSalary(id);
            return "El salario neto calculado es: $" + salary;
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Ocurrió un error inesperado al calcular el salario.";
        }
    }
}
