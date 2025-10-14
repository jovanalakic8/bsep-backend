package com.project.bsep.listener;

import com.project.bsep.model.User;
import com.project.bsep.service.EncryptionService;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SensitiveDataListener {

    private static EncryptionService encryptionService;

    @Autowired
    private void init(EncryptionService encryptionService) {
        SensitiveDataListener.encryptionService = encryptionService;
    }
    @PrePersist
    @PreUpdate
    public void encryptSensitiveFields(User user) throws Exception {
        if (user.getName() != null) {
            user.setName(encryptionService.encrypt(user.getName()));
        }
        if (user.getSurname() != null) {
            user.setSurname(encryptionService.encrypt(user.getSurname()));
        }
        if (user.getAddress() != null) {
            user.setAddress(encryptionService.encrypt(user.getAddress()));
        }
        if (user.getCity() != null) {
            user.setCity(encryptionService.encrypt(user.getCity()));
        }
        if (user.getCountry() != null) {
            user.setCountry(encryptionService.encrypt(user.getCountry()));
        }
        if (user.getPhone() != null) {
            user.setPhone(encryptionService.encrypt(user.getPhone()));
        }
    }

    @PostLoad
    @PostUpdate
    public void decryptSensitiveFields(User user) throws Exception {
        if (user.getName() != null) {
            user.setName(encryptionService.decrypt(user.getName()));
        }
        if (user.getSurname() != null) {
            user.setSurname(encryptionService.decrypt(user.getSurname()));
        }
        if (user.getAddress() != null) {
            user.setAddress(encryptionService.decrypt(user.getAddress()));
        }
        if (user.getCity() != null) {
            user.setCity(encryptionService.decrypt(user.getCity()));
        }
        if (user.getCountry() != null) {
            user.setCountry(encryptionService.decrypt(user.getCountry()));
        }
        if (user.getPhone() != null) {
            user.setPhone(encryptionService.decrypt(user.getPhone()));
        }
    }
}
