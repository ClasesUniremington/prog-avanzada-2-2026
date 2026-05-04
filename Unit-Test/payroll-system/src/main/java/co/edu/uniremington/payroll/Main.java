package co.edu.uniremington.payroll;

import co.edu.uniremington.payroll.controller.PayrollController;
import co.edu.uniremington.payroll.model.Employee;
import co.edu.uniremington.payroll.repository.EmployeeRepository;
import co.edu.uniremington.payroll.repository.InMemoryEmployeeRepository;
import co.edu.uniremington.payroll.service.PayrollService;

import java.math.BigDecimal;

/**
 * Clase principal. Actúa como inyector de dependencias manual y Vista/UI de Consola.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Inicializando Sistema de Nómina (Java Puro) ===");

        // 1. Instanciar Repositorio (Acceso a Datos)
        EmployeeRepository repository = new InMemoryEmployeeRepository();

        // 2. Instanciar Servicio e inyectar Repositorio
        PayrollService service = new PayrollService(repository);

        // 3. Instanciar Controlador e inyectar Servicio
        PayrollController controller = new PayrollController(service);

        System.out.println("=== Creando Empleados de Prueba ===");
        
        Employee emp1 = new Employee(null, "Ana", "ana@empresa.com", "FULL_TIME", new BigDecimal("3500.00"), 0, 3);
        Employee emp2 = new Employee(null, "Pedro", "pedro@empresa.com", "HOURLY", new BigDecimal("20.00"), 160, 0);
        
        System.out.println(controller.createEmployee(emp1));
        System.out.println(controller.createEmployee(emp2));

        System.out.println("\n=== Listado de Empleados ===");
        for(Employee e : controller.getAllEmployees()) {
            System.out.println(e.getId() + " - " + e.getName() + " (" + e.getContractType() + ")");
        }

        System.out.println("\n=== Calculando Salarios ===");
        System.out.println("Salario Ana: " + controller.getNetSalary(1L));
        System.out.println("Salario Pedro: " + controller.getNetSalary(2L));
        
        System.out.println("\n=== Fin de la Ejecución ===");
    }
}
