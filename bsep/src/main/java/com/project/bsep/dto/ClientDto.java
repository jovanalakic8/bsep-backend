package com.project.bsep.dto;

import com.project.bsep.model.Client;
import com.project.bsep.model.ClientType;
import com.project.bsep.model.ServicePackageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String address;
    private String city;
    private String country;
    private String phone;
    private Boolean enabled;
    private ClientType clientType;
    private String companyName;
    private String tin;
    private ServicePackageType servicePackage;
    public Boolean using2FA;

    public ClientDto(Client client) {
        this.id = client.getId();
        this.username = client.getUsername();
        this.password = null;
        this.name = client.getName();
        this.surname = client.getSurname();
        this.address = client.getAddress();
        this.city = client.getCity();
        this.country = client.getCountry();
        this.phone = client.getPhone();
        this.enabled = client.isEnabled();
        this.clientType = client.getClientType();
        this.companyName = client.getCompanyName();
        this.tin = client.getTin();
        this.servicePackage = client.getServicePackage();
    }
}
