package com.project.busep.controller;

import com.project.busep.dto.*;
import com.project.busep.model.Certificate;
import com.project.busep.service.CertificateService;
import com.project.busep.service.IMCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/api/cert")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @Autowired
    private IMCertificateService imCertificateService;

    @PostMapping(value = "/createRootCert", consumes = "application/json")
    public ResponseEntity<KeyPairDto> createRootCertificate(@RequestBody CreateCertificateDto certDto){
        return ResponseEntity.ok(certificateService.createRootCertificate(certDto));
    }

    @GetMapping(value = "/getRoot/{id}")
    public ResponseEntity<Optional<Certificate>> getRootCertificate(@PathVariable String id){
        return ResponseEntity.ok(certificateService.getRootCertificate(id));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/getCertificates")
    public ResponseEntity<ArrayList<Certificate>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAll());
    }

    @GetMapping(value = "/getImIssuers")
    public ResponseEntity<ArrayList<Certificate>> getPossibleImIssuerCertificates() {
        return ResponseEntity.ok(imCertificateService.getPossibleCertificates());
    }
    @PostMapping(value = "/createCert", consumes = "application/json")
    public ResponseEntity<KeyPairDto> createCertificate(@RequestBody CreateCertificateDto certDto){
        return ResponseEntity.ok(imCertificateService.createCertificate(certDto));
    }
}
