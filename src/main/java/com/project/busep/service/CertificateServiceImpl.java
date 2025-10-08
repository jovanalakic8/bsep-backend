package com.project.busep.service;

import com.project.busep.data.IssuerData;
import com.project.busep.dto.*;
import com.project.busep.model.CertType;
import com.project.busep.model.Certificate;
import com.project.busep.data.SubjectData;
import com.project.busep.model.User;
import com.project.busep.repository.CertificateRepository;
import com.project.busep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {
    private CertificateGenerator generator = new CertificateGenerator();

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    public KeyPairDto createRootCertificate(CreateCertificateDto certDto) {
        KeyPair keyPair = null;
        keyPair = generator.generateKeyPair();

        Optional<User> rootUser = userRepository.findByUsername(certDto.getSubjectUsername());
        if (rootUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message: Could not find the root user!");
        }

        SubjectData subjectData = generator.generateSubjectData(rootUser.get(), keyPair.getPublic());

        IssuerData issuerData = generator.generateIssuerData(rootUser.get(), keyPair.getPrivate());

        X509Certificate cert = generator.generateCertificate(subjectData, issuerData);

        certificateRepository.save(createDatabaseRootCertificate(rootUser.get(), keyPair.getPublic()));

        return new KeyPairDto(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()),
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
    }

    private Certificate createDatabaseRootCertificate(User rootUser, PublicKey pubKey){
        Certificate cert = new Certificate();

        cert.setSubject(rootUser);
        cert.setIssuer(rootUser);
        cert.setPublicKey(Base64.getEncoder().encodeToString(pubKey.getEncoded()));

        Date today = new Date();
        Date endDate = generator.generateEndDate(today, CertType.ROOT);

        cert.setStartDate(today);
        cert.setEndDate(endDate);
        cert.setType(CertType.ROOT);
        return cert;
    }

    @Override
    public Optional<Certificate> getRootCertificate(String subjectId) {

        Optional<Certificate> cert = certificateRepository.findBySubjectId(Long.valueOf(subjectId));
        if (cert.isEmpty()) {
            return null;
        }
        return cert;
    }

    @Override
    public ArrayList<Certificate> getAll() {
        return (ArrayList<Certificate>) certificateRepository.findAll();
    }
}
