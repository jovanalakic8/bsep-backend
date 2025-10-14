package com.project.busep.service;

import com.project.busep.dto.CreateCertificateDto;
import com.project.busep.dto.KeyPairDto;
import com.project.busep.model.Certificate;

import java.util.ArrayList;

public interface IMCertificateService {
    ArrayList<Certificate> getPossibleCertificates();
    KeyPairDto createCertificate(CreateCertificateDto certDto);
}
