package com.project.busep.service;

import com.project.busep.data.IssuerData;
import com.project.busep.data.SubjectData;
import com.project.busep.dto.CreateCertificateDto;
import com.project.busep.dto.KeyPairDto;
import com.project.busep.model.CertType;
import com.project.busep.model.Certificate;
import com.project.busep.model.Role;
import com.project.busep.model.User;
import com.project.busep.repository.CertificateRepository;
import com.project.busep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class IMCertificateServiceImpl implements IMCertificateService{
    private CertificateGenerator generator = new CertificateGenerator();

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public ArrayList<Certificate> getPossibleCertificates() {
        ArrayList<CertType> types = new ArrayList<>();
        types.add(CertType.INTERMEDIATE);
        types.add(CertType.ROOT);
        return certificateRepository.findImIssuers(types);
    }

    @Override
    public KeyPairDto createCertificate(CreateCertificateDto certDto) {
        PublicKey subjectPubKey = null;
        PrivateKey subjectPrivKey = null;
        if(certDto.getPrivateKey().isEmpty() || certDto.getPublicKey().isEmpty()){
            KeyPair keyPair = generator.generateKeyPair();
            subjectPubKey = keyPair.getPublic();
            subjectPrivKey = keyPair.getPrivate();
        }
        if(!certDto.getPrivateKey().isEmpty() && !certDto.getPublicKey().isEmpty()){
            subjectPrivKey = stringToPrivateKey(certDto.getPrivateKey());
            subjectPubKey = stringToPublicKey(certDto.getPublicKey());

        }

        Optional<User> subject = userRepository.findByUsername(certDto.getSubjectUsername());
        if (subject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message: Could not find the subject!");
        }

        Optional<User> issuer = userRepository.findByUsername(certDto.getIssuerUsername());
        if (issuer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message: Could not find the issuer!");
        }

        SubjectData subjectData = generator.generateSubjectData(subject.get(), subjectPubKey);

        IssuerData issuerData = generator.generateIssuerData(issuer.get(), subjectPrivKey);

        X509Certificate cert = generator.generateCertificate(subjectData, issuerData);

        certificateRepository.save(createDatabaseCertificate(subject.get(), issuer.get(), subjectPubKey));

        return new KeyPairDto(Base64.getEncoder().encodeToString(subjectPrivKey.getEncoded()),
                Base64.getEncoder().encodeToString(subjectPubKey.getEncoded()));
    }

    private PublicKey stringToPublicKey(String pubKey){
        try {
            pubKey = pubKey.replaceAll("\\s", "");
            byte[] publicBytes = Base64.getDecoder().decode(pubKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private PrivateKey stringToPrivateKey(String privKey){
        try {
            privKey = privKey.replaceAll("\\s", "");
            byte[] privateBytes = Base64.getDecoder().decode(privKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private Certificate createDatabaseCertificate(User subject, User issuer, PublicKey pubKey){
        Certificate cert = new Certificate();

        cert.setSubject(subject);
        cert.setIssuer(issuer);
        cert.setPublicKey(Base64.getEncoder().encodeToString(pubKey.getEncoded()));

        Date today = new Date();
        cert.setStartDate(today);

        if(subject.getRole().equals(Role.INTERMEDIARY_CA)) {
            Date endDate = generator.generateEndDate(today, CertType.INTERMEDIATE);
            cert.setEndDate(endDate);

            cert.setType(CertType.INTERMEDIATE);
        }
        if(subject.getRole().equals(Role.END_ENTITY)) {
            Date endDate = generator.generateEndDate(today, CertType.END_ENTITY);
            cert.setEndDate(endDate);

            cert.setType(CertType.END_ENTITY);
        }


        return cert;
    }
}
