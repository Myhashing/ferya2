package com.mlmfreya.ferya2.controller;


import org.springframework.beans.factory.annotation.Autowired;
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

/*    @GetMapping("/create-account")
    public ResponseEntity<AccountDto> createAccount() throws Exception {
        AccountDto accountDto = accountService.generateKey();
        return ResponseEntity.ok(accountDto);
    }*/
}