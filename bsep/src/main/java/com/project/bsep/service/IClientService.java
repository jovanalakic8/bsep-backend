package com.project.bsep.service;

import com.project.bsep.dto.ClientDto;
import dev.samstevens.totp.exceptions.QrGenerationException;

public interface IClientService {
    public String create(ClientDto clientDto) throws QrGenerationException;
}
