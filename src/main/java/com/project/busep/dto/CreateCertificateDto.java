package com.project.busep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCertificateDto {
    private String subjectUsername;
    private String issuerUsername;
    private String publicKey;
    private String privateKey;
}
