package vn.duyhai.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.duyhai.jobhunter.domain.Company;
import vn.duyhai.jobhunter.service.CompanyService;
@RestController
public class CompanyController {
    private final CompanyService companyService;
    public CompanyController(CompanyService companyService){
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company postmanCompany){
        Company newCompany = this.companyService.handleCreateCompany(postmanCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }
}
