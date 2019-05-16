package com.eimt.usermanagement;

import com.eimt.usermanagement.model.Department;
import com.eimt.usermanagement.model.Role;
import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.service.DepartmentService;
import com.eimt.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class UserManagementApplication {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.user.admin.email}")
    private String adminEmail;

    @Value("${app.user.admin.password}")
    private String adminPassword;

    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
    }

    @PostConstruct
    public void init() {
//        User admin = new User(
//                adminEmail,
//                passwordEncoder.encode(adminPassword),
//                null,
//                null,
//                null,
//                null,
//                LocalDateTime.now(),
//                Role.ADMIN,
//                null
//        );
//        admin.activate();
//        this.userService.createUser(admin);
//
//        User manager = new User(
//                "manager123@manager",
//                passwordEncoder.encode("manager123"),
//                null,
//                null,
//                null,
//                null,
//                LocalDateTime.now(),
//                Role.MANAGER,
//                null
//        );
//        manager.activate();
//        this.userService.createUser(manager);
//
//        this.departmentService.makeNewDepartment("FINKI", manager);
//
//        Department department = departmentService.getDepartmentByManager(manager.getEmail()).get();
//
//        User user1 = new User(
//                "user1@user",
//                passwordEncoder.encode("user1"),
//                "user1-firstName",
//                "user1-lastName",
//                "MALE",
//                LocalDate.of(1998, 07, 27),
//                LocalDateTime.now(),
//                Role.EMPLOYEE,
//                department
//        );
//        user1.activate();
//        this.userService.createUser(user1);
//
//        User user2 = new User(
//                "user2@user",
//                passwordEncoder.encode("user2"),
//                "user2-firstName",
//                "user2-lastName",
//                "FEMALE",
//                LocalDate.of(2001, 03, 13),
//                LocalDateTime.now(),
//                Role.EMPLOYEE,
//                department
//        );
//        user2.activate();
//        this.userService.createUser(user2);
//
//        User user3 = new User(
//                "user3@user",
//                passwordEncoder.encode("user3"),
//                "user3-firstName",
//                "user3-lastName",
//                "MALE",
//                LocalDate.of(1995, 02, 11),
//                LocalDateTime.now(),
//                Role.EMPLOYEE,
//                department
//        );
//        user3.activate();
//        this.userService.createUser(user3);
//
//        User user4 = new User(
//                "user4@user",
//                passwordEncoder.encode("user4"),
//                "user4-firstName",
//                "user4-lastName",
//                "MALE",
//                LocalDate.of(1992, 05, 16),
//                LocalDateTime.now(),
//                Role.EMPLOYEE,
//                null
//        );
//        user4.activate();
//        this.userService.createUser(user4);
    }
}
