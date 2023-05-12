package com.mlmfreya.ferya2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

    private String privateKey;
    private String publicKey;
    private String base58Address;
    private String hexAddress;


}
