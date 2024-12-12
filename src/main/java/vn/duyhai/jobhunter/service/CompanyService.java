package vn.duyhai.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.duyhai.jobhunter.domain.Company;
import vn.duyhai.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    public CompanyService(CompanyRepository companyRepository){
        this.companyRepository = companyRepository;
    }
     public Company handleCreateCompany(Company company){
         return this.companyRepository.save(company);
    }
}
