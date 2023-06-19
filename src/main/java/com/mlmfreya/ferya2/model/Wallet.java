package com.mlmfreya.ferya2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Data
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String privateKey;

    private String publicKey;

    private String base58;

    private String hex;

    private String email;

}
