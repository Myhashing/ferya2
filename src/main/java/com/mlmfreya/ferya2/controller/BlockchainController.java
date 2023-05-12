package com.mlmfreya.ferya2.controller;


import com.mlmfreya.ferya2.dto.AccountDto;
import com.mlmfreya.ferya2.service.TronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blockchain")
public class BlockchainController {

    private final TronService  accountService;

    @Autowired
    public BlockchainController(TronService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/create-account")
    public ResponseEntity<AccountDto> createAccount() {
        AccountDto accountDto = accountService.createAccount();
        return ResponseEntity.ok(accountDto);
    }
}