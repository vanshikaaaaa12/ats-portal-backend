package com.ats.ats.system.repository;

import com.ats.ats.system.model.student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<student, Integer> {
}