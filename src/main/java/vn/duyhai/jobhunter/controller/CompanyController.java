package vn.duyhai.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.duyhai.jobhunter.domain.Company;
import vn.duyhai.jobhunter.domain.dto.ResultPaginationDTO;
import vn.duyhai.jobhunter.service.CompanyService;
import vn.duyhai.jobhunter.util.anotation.ApiMessage;
import vn.duyhai.jobhunter.util.error.IdInvalidException;



@RestController
@RequestMapping("/api/v1")
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

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable("id") long id) throws IdInvalidException{
        if(id > 1500){
            throw new IdInvalidException("ID too large");
        }
       this.companyService.hanldeDeleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted company");
    }

    @GetMapping("/companies")
    @ApiMessage("fetch all company")
    public ResponseEntity<ResultPaginationDTO> getAllCompany( 
        @Filter Specification<Company> spec, Pageable pageable){

        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.fetchAllCompany(spec,pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany( @RequestBody Company company) {
        Company updatedCompany = this.companyService.handleUpdateCompany(company);
        return ResponseEntity.ok(updatedCompany);
    }
    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyDetail(@PathVariable("id") long id) throws IdInvalidException{
        if(id > 1500){
            throw new IdInvalidException("ID too large");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.fetchCompanyById(id));
    }
    

}
