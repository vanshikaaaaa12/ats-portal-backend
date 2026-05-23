package com.ats.ats.system.controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.ats.ats.system.model.student;
import com.ats.ats.system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository repository;

    // 👉 Save student
    @PostMapping("/add")
    public student addStudent(@RequestBody student student) {
        return repository.save(student);
    }

    // 👉 Get all students
    @GetMapping("/all")
    public List<student> getAllStudents() {
        return repository.findAll();
    }
}