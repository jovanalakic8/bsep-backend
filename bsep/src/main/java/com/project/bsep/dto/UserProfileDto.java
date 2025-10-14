package com.project.bsep.dto;

import com.project.bsep.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String address;
    private String city;
    private String country;
    private String phone;

    public UserProfileDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.address = user.getAddress();
        this.city = user.getCity();
        this.country = user.getCountry();
        this.phone = user.getPhone();
    }
}
