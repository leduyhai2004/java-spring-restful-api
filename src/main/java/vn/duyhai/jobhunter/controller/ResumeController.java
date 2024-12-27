package vn.duyhai.jobhunter.controller;

import java.util.Optional;

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
import vn.duyhai.jobhunter.domain.Resume;
import vn.duyhai.jobhunter.domain.response.ResultPaginationDTO;
import vn.duyhai.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.duyhai.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.duyhai.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.duyhai.jobhunter.service.JobService;
import vn.duyhai.jobhunter.service.ResumeService;
import vn.duyhai.jobhunter.service.UserService;
import vn.duyhai.jobhunter.util.anotation.ApiMessage;
import vn.duyhai.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final JobService jobService;
    public ResumeController(ResumeService resumeService,UserService userService,JobService jobService){
        this.resumeService = resumeService;
        this.userService = userService;
        this.jobService = jobService;
    }
    @PostMapping("/resumes")
    @ApiMessage("Create a new resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvalidException{
        // check id exists
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException("User id/Job id không tồn tại");
        }
         return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.handleCreateSkill(resume));
    }
    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvalidException {
        // check id exist
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(resume.getId());
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Resume với id = " + resume.getId() + " không tồn tại");
        }

        Resume reqResume = reqResumeOptional.get();
        reqResume.setStatus(resume.getStatus());

        return ResponseEntity.ok().body(this.resumeService.update(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException{
        Optional<Resume> resume = this.resumeService.fetchById(id);
        if(resume.isEmpty()){
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }
       this.resumeService.delete(id);
        return ResponseEntity.ok(null);
    }


    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch a resume by id")
    public ResponseEntity<ResFetchResumeDTO> fetchById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(this.resumeService.getDetailResume(reqResumeOptional.get()));
    }


    @GetMapping("/resumes")
    @ApiMessage("fetch all resume")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(
        @Filter Specification<Resume> spec, Pageable pageable ) {
        
         
    return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.fetchAllResumes(spec,pageable));
    }
}
