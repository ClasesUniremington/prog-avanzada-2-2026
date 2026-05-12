package co.edu.uniremington.payroll.repository;

import co.edu.uniremington.payroll.model.Employee;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación en memoria del repositorio de empleados utilizando Java Puro.
 * Ideal para la Unidad 1 donde el enfoque es pruebas unitarias sin base de datos real.
 */
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<Long, Employee> database = new HashMap<>();
    private long currentId = 1L;

    @Override
    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(currentId++);
        }
        database.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public void deleteById(Long id) {
        database.remove(id);
    }
}
