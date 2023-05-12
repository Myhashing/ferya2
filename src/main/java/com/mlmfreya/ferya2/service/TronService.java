package com.mlmfreya.ferya2.service;


import com.mlmfreya.ferya2.dto.AccountDto;
import org.springframework.stereotype.Service;
import org.tron.trident.core.key.KeyPair;
@Service
public class TronService{

    public AccountDto createAccount(){
        KeyPair keyPair = KeyPair.generate();
        //cast into AccountDto
        return AccountDto.builder()
                .privateKey(keyPair.toPrivateKey())
                .publicKey(keyPair.toPublicKey())
                .base58Address(keyPair.toBase58CheckAddress())
                .hexAddress(keyPair.toHexAddress())
                .build();
    }



}