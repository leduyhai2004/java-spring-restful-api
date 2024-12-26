package vn.duyhai.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.duyhai.jobhunter.domain.Company;
import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.domain.response.ResultPaginationDTO;
import vn.duyhai.jobhunter.repository.CompanyRepository;
import vn.duyhai.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    public CompanyService(CompanyRepository companyRepository,UserRepository userRepository){
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }
     public Company handleCreateCompany(Company company){
         return this.companyRepository.save(company);
    }

    public void hanldeDeleteCompany(long id){
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if(comOptional.isPresent()){
            Company com = comOptional.get();
            //fetch all user belong to this company
            List<User> listUser = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(listUser);
        }
        this.companyRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllCompany(Specification<Company> spec,Pageable pageable){
        Page<Company> pageCompanies = this.companyRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO. Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCompanies.getTotalPages());
        mt.setTotal(pageCompanies.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageCompanies.getContent());

        //pageCompanies.getContent() return list (read in library)

        return rs;
    }

    public Company fetchCompanyById(long id){
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if(companyOptional.isPresent()){
            return  companyOptional.get();
        }
        return null;
    }

    public Company handleUpdateCompany(Company company){
        Company currentCompany = this.fetchCompanyById(company.getId());
        if(currentCompany != null){
            currentCompany.setName(company.getName());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            currentCompany.setDescription(company.getDescription());
            currentCompany = this.companyRepository.save(currentCompany);
        }
        return currentCompany;
    }
    public Optional<Company> findById(long id){
        return this.companyRepository.findById(id);
    }
}
