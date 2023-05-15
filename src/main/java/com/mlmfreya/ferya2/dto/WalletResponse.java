package com.mlmfreya.ferya2.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletResponse {
    private String status;
    private WalletData data;

    // getters and setters
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalletData {
        private String privateKey;
        private String publicKey;
        private WalletAddress address;

        // getters and setters
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalletAddress {
        private String base58;
        private String hex;

        // getters and setters
    }
}