package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.InvestmentPackage;
import com.mlmfreya.ferya2.model.Order;
import com.mlmfreya.ferya2.model.User;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Data
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {
    private InvestmentPackage investmentPackage;
    private String name;
    private String email;
    private String mobileNumber;
    private String walletAddress;
    private String password;
    private BigDecimal InvestmentAmount;

    private Map<InvestmentPackage, Integer> packages = new HashMap<>();

    public void addPackage(InvestmentPackage investmentPackage) {
        if (packages.containsKey(investmentPackage)) {
            packages.replace(investmentPackage, packages.get(investmentPackage) + 1);
        } else {
            packages.put(investmentPackage, 1);
        }
    }

    public void removePackage(InvestmentPackage investmentPackage) {
        if (packages.containsKey(investmentPackage)) {
            if (packages.get(investmentPackage) > 1)
            packages.replace(investmentPackage, packages.get(investmentPackage) - 1);
            else if (packages.get(investmentPackage) == 1) {
                packages.remove(investmentPackage);
            }
        }
    }

    public InvestmentPackage getPackage() {
        // If your use case allows only one package at a time in the cart,
        // you can just return the first (and only) package
        return this.packages.keySet().stream().findFirst().orElse(null);
    }

    public void clear() {
        packages.clear();
    }



}
