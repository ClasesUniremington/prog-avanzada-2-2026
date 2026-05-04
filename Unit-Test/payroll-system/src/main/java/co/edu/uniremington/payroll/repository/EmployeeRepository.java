package co.edu.uniremington.payroll.repository;

import co.edu.uniremington.payroll.model.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para Employee.
 * (Patrón Repository en Java Puro)
 */
public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(Long id);
    List<Employee> findAll();
    void deleteById(Long id);
}
