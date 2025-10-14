package com.project.bsep.repository;

import com.project.bsep.model.Commercial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface CommercialRepository extends JpaRepository<Commercial, Long> {
    ArrayList<Commercial> findAll();
}
