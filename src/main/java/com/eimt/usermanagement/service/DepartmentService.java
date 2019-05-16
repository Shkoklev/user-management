package com.eimt.usermanagement.service;

import com.eimt.usermanagement.model.Department;
import com.eimt.usermanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DepartmentService {

    Optional<Department> findDepartmentById(Long id);

    Page<User> getEmployeesByDepartment(Long departmentId, Pageable pageable);

    Optional<Department> getDepartmentByManager(String email);

    Department makeNewDepartment(String name, User manager);
}
