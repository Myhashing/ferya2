package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.exception.ResourceNotFoundException;
import com.mlmfreya.ferya2.model.InvestmentPackage;
import com.mlmfreya.ferya2.service.InvestmentPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/package")
public class PackageController {

    @Autowired
    private InvestmentPackageService investmentPackageService;


    @GetMapping("/delete/{id}")
    public String deletePackage(@PathVariable("id") long id) {
        InvestmentPackage investmentPackage = investmentPackageService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvestmentPackage", "id", id));

        investmentPackageService.deletePackage(investmentPackage.getId());
        return "redirect:/admin/package/list";
    }

    @PostMapping("/update")
    public String updatePackage(@ModelAttribute InvestmentPackage investmentPackage, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/update-package";
        }
        investmentPackageService.savePackage(investmentPackage);
        return "redirect:/admin/package/list";
    }

    @GetMapping("/edit/{id}")
    public String showUpdatePackageForm(@PathVariable("id") long id, Model model) {
        InvestmentPackage investmentPackage = investmentPackageService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvestmentPackage", "id", id));

        model.addAttribute("package", investmentPackage);
        return "admin/update-package";
    }

    @GetMapping("/add")
    public String showAddPackageForm(Model model) {

        model.addAttribute("package", new InvestmentPackage());
        return "admin/add-package";
    }


    @PostMapping("/add")
    public String addPackage(@ModelAttribute InvestmentPackage investmentPackage, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/add-package";
        }
        investmentPackage.setStatus(InvestmentPackage.Status.ACTIVE);
        investmentPackageService.savePackage(investmentPackage);
        return "redirect:/admin/package/list";
    }

    @GetMapping("/list")
    public String listPackages(Model model) {
        List<InvestmentPackage> packages = investmentPackageService.getAllPackages();
        model.addAttribute("packages", packages);
        return "admin/packages";
    }

}
