package com.project.bsep.model;

import com.project.bsep.dto.ClientDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "client_id")
public class Client extends User{
    @Column(name = "company_name")
    private String companyName;

    @Column(name = "tin")
    private String tin;

    @Column(name = "client_type")
    private ClientType clientType;

    @Column(name = "service_package")
    private ServicePackageType servicePackage;

    @Column(name = "verification")
    private String verificationToken;


    public Client(Long id, String name, String surname, String address, String city, String country, String phone, String username, String password, boolean enabled,boolean activationPending, LocalDateTime blocked, ArrayList<Role> roles, String companyName, String tin, ClientType clientType, ServicePackageType servicePackage, boolean using2FA, String secret2FA) {
        super(id,
                name,
                surname,
                address,
                city,
                country,
                phone,
                username,
                password,
                enabled,
                activationPending,
                blocked,
                using2FA,
                secret2FA,
                roles);
        this.companyName = companyName;
        this.tin = tin;
        this.clientType = clientType;
        this.servicePackage = servicePackage;
    }

    public Client(ClientDto clientDto) {
        super(null,
                clientDto.getName(),
                clientDto.getSurname(),
                clientDto.getAddress(),
                clientDto.getCity(),
                clientDto.getCountry(),
                clientDto.getPhone(),
                clientDto.getUsername(),
                null,
                false,
                false,
                null,
                clientDto.getUsing2FA(),
                null,
                null);
        this.companyName = clientDto.getCompanyName();
        this.tin = clientDto.getTin();
        this.clientType = clientDto.getClientType();
        this.servicePackage = clientDto.getServicePackage();
    }
}