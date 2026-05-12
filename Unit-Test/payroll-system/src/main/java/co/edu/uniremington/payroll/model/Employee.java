package co.edu.uniremington.payroll.model;

import java.math.BigDecimal;

/**
 * Entidad que representa a un empleado (Plain Old Java Object - POJO).
 * En esta etapa no usamos frameworks, solo Java puro.
 */
public class Employee {

    private Long id;
    private String name;
    private String email;
    private String contractType; // "FULL_TIME", "HOURLY", "FREELANCE"
    private BigDecimal baseSalary;
    private Integer hoursWorked;
    private Integer antiquityYears; // Años de antigüedad en la empresa

    public Employee() {}

    public Employee(Long id, String name, String email, String contractType, BigDecimal baseSalary, Integer hoursWorked, Integer antiquityYears) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contractType = contractType;
        this.baseSalary = baseSalary;
        this.hoursWorked = hoursWorked;
        this.antiquityYears = antiquityYears;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public Integer getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Integer hoursWorked) { this.hoursWorked = hoursWorked; }

    public Integer getAntiquityYears() { return antiquityYears; }
    public void setAntiquityYears(Integer antiquityYears) { this.antiquityYears = antiquityYears; }
    
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", contractType='" + contractType + '\'' +
                ", antiquity=" + antiquityYears +
                '}';
    }
}
