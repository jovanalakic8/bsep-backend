package com.project.busep.service;

import com.project.busep.dto.*;
import com.project.busep.model.Certificate;
import java.util.Optional;

import java.util.ArrayList;

public interface CertificateService {
    KeyPairDto createRootCertificate(CreateCertificateDto certDto);
    Optional<Certificate> getRootCertificate(String id);
    ArrayList<Certificate> getAll();
}
