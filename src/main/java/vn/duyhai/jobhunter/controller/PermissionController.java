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
import vn.duyhai.jobhunter.domain.Permission;
import vn.duyhai.jobhunter.domain.response.ResultPaginationDTO;
import vn.duyhai.jobhunter.service.PermissionService;
import vn.duyhai.jobhunter.util.anotation.ApiMessage;
import vn.duyhai.jobhunter.util.error.IdInvalidException;




@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;
    public PermissionController(PermissionService permissionService){
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission p) throws IdInvalidException {
        //check exist
        if(permissionService.isPermissionExist(p)){
            throw new IdInvalidException("Permission is existed");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(p));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission p) throws IdInvalidException {
        //check exist by Id
        if(this.permissionService.fetchById(p.getId()) == null){
            throw new IdInvalidException("Permission with id = " + p.getId() + " isn't exist.");
        }
        // check exist by module, apiPath and method
        if(permissionService.isPermissionExist(p)){
            //check name
            if(this.permissionService.isSameName(p)){
                throw new IdInvalidException("Permission is existed");
            }
        }
        
        return ResponseEntity.ok().body(this.permissionService.update(p));
    }
    
    @GetMapping("/permissions")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.getPermissions(spec, pageable));
    }
    
    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException{
        //check exist
        if(this.permissionService.fetchById(id) == null){
            throw new IdInvalidException("Permission with id = " + id + " isn't exist.");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    
}
