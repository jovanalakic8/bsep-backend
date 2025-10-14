package com.project.bsep.service;

import com.project.bsep.dto.ClientDto;
import com.project.bsep.exception.UsernameAlreadyExistsException;
import com.project.bsep.model.Client;
import com.project.bsep.model.Role;
import com.project.bsep.model.User;
import com.project.bsep.repository.ClientRepository;
import com.project.bsep.repository.RoleRepository;
import com.project.bsep.repository.UserRepository;
import dev.samstevens.totp.exceptions.QrGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService implements IClientService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger= LoggerFactory.getLogger(UserService.class);

    @Override
    public String create(ClientDto clientDto) throws QrGenerationException {
        {
            Optional<User> userExists = userRepository.findByUsername(clientDto.getUsername());
            if (userExists.isPresent()) {
                logger.error("Klijent "+clientDto.getUsername()+" vec postoji!");
                throw new UsernameAlreadyExistsException("Username already exists.");
            }

            Client client = new Client(clientDto);
            client.setPassword(passwordEncoder.encode(clientDto.getPassword()));
            Role clientRole = roleRepository.findByName("client")
                    .orElseThrow(() -> new RuntimeException("Role client not found"));
            List<Role> roles = new ArrayList<>();
            roles.add(clientRole);
            client.setRoles(roles);
            Client savedClient = clientRepository.save(client);
            logger.info("Klijent "+clientDto.getUsername()+" je uspesno kreiran!");

            return "";
        }
    }

}
