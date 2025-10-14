package com.project.busep.repository;

import com.project.busep.model.CertType;
import com.project.busep.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.Optional;

public interface CertificateRepository  extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findBySubjectId(Long id);

    @Query("SELECT c FROM Certificate c WHERE c.type  IN (:types) ")
    ArrayList<Certificate> findImIssuers(ArrayList<CertType> types);
}
