package com.project.bsep.repository;

import com.project.bsep.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface RequestRepository extends JpaRepository<Request, Long> {
    ArrayList<Request> findAll();
    void deleteById(Long id);
}
