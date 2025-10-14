package com.project.busep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDto {
    private Long id;
    private UserDto subject;
    private UserDto issuer;
    private Date startDate;
    private Date endDate;
    private String publicKey;
}
