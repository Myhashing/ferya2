package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.exception.ResourceNotFoundException;
import com.mlmfreya.ferya2.model.InvestmentPackage;
import com.mlmfreya.ferya2.repository.InvestmentPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvestmentPackageService {

    private final InvestmentPackageRepository investmentPackageRepository;

    @Autowired
    public InvestmentPackageService(InvestmentPackageRepository investmentPackageRepository) {
        this.investmentPackageRepository = investmentPackageRepository;
    }

    public List<InvestmentPackage> getAllPackages() {
        return investmentPackageRepository.findAll();
    }

    public InvestmentPackage savePackage(InvestmentPackage investmentPackage) {
        return investmentPackageRepository.save(investmentPackage);
    }

    public InvestmentPackage getPackage(Long id) {
        return investmentPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvestmentPackage", "id", id));
    }

    public InvestmentPackage updatePackage(Long id, InvestmentPackage investmentPackage) {
        InvestmentPackage existingPackage = getPackage(id);
        existingPackage.setName(investmentPackage.getName());
        existingPackage.setDescription(investmentPackage.getDescription());
        existingPackage.setDuration(investmentPackage.getDuration());
        existingPackage.setReturnOnInvestment(investmentPackage.getReturnOnInvestment());
        return investmentPackageRepository.save(existingPackage);
    }

    public void deletePackage(Long id) {
        InvestmentPackage existingPackage = getPackage(id);
        investmentPackageRepository.delete(existingPackage);
    }

    public Optional<InvestmentPackage> findById(long id) {
        return investmentPackageRepository.findById(id);
    }
}
