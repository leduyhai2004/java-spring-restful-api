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
import vn.duyhai.jobhunter.domain.Skill;
import vn.duyhai.jobhunter.domain.response.ResultPaginationDTO;
import vn.duyhai.jobhunter.service.SkillService;
import vn.duyhai.jobhunter.util.anotation.ApiMessage;
import vn.duyhai.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;
    public SkillController(SkillService skillService){
        this.skillService = skillService;
    }
    @PostMapping("/skills")
    @ApiMessage("Create a new skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill skill) throws IdInvalidException{
        //check name
        if(skill.getName() != null && this.skillService.isNameExist(skill.getName())){
            throw new IdInvalidException("Skill name" + skill.getName() + " is exist");
        }
         return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(skill));
    }

    @PutMapping("/skills")
    @ApiMessage("Update skill")
    public ResponseEntity<Skill> updateSkill( @RequestBody Skill skill) throws IdInvalidException {
        //check id
        Skill currentSkill = this.skillService.fetchSkillById(skill.getId());
        if(currentSkill == null){
            throw new IdInvalidException("Skill id = " + skill.getId() +" is not exist");
        }
        //check name
        if(skill.getName() != null && this.skillService.isNameExist(skill.getName())){
            throw new IdInvalidException("Skill name" + skill.getName() + " is exist");
        }
        currentSkill.setName(skill.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleUpdateSkill(skill));
    }

    
    @GetMapping("/skills")
    @ApiMessage("fetch all skill")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
        @Filter Specification<Skill> spec, Pageable pageable ) {
    return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkills(spec,pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException{  
        Skill s = this.skillService.fetchSkillById(id);
        if(s == null){
            throw new IdInvalidException("Skill with id= "+id +" is not exist");
        }
       this.skillService.deleteSkill(id);
        return ResponseEntity.ok(null);
    }

}
