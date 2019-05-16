package com.eimt.usermanagement.service.impl;

import com.eimt.usermanagement.model.Department;
import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.repository.DepartmentRepository;
import com.eimt.usermanagement.repository.UserRepository;
import com.eimt.usermanagement.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Optional<Department> findDepartmentById(Long id) {
        return this.departmentRepository.findById(id);
    }

    @Override
    public Department makeNewDepartment(String name, User manager) {
        Department department = new Department(name, manager);
        return this.departmentRepository.save(department);
    }

    @Override
    public Page<User> getEmployeesByDepartment(Long departmentId, Pageable pageable) {
        return this.userRepository.findByDepartmentId(departmentId, pageable);
    }

    @Override
    public Optional<Department> getDepartmentByManager(String email) {
        return this.departmentRepository.findByManagerEmail(email);
    }
}
