package vn.duyhai.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.duyhai.jobhunter.domain.Subscriber;
import vn.duyhai.jobhunter.service.SubscriberService;
import vn.duyhai.jobhunter.util.SecurityUtil;
import vn.duyhai.jobhunter.util.anotation.ApiMessage;
import vn.duyhai.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a new subcriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber sub) throws IdInvalidException{
        //check email
        boolean isExist = this.subscriberService.isExistsByEmail(sub.getEmail());
        if(isExist == true){
            throw new IdInvalidException("Email "+ sub.getEmail()+" is existed");
        }
         return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(sub));
    }


    @PutMapping("/subscribers")
    @ApiMessage("Update a Subcriber")
    public ResponseEntity<Subscriber> updateSubcribers( @RequestBody Subscriber subscriberReq) throws IdInvalidException {
        //check id
        Subscriber subscriberDB = this.subscriberService.findById(subscriberReq.getId());
        if(subscriberDB == null){
            throw new IdInvalidException("Subcibers with this id : "+ subscriberReq.getId()+" is not  existed");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.update(subscriberDB,subscriberReq));
    }
    @PostMapping("/subscribers/skills")
    @ApiMessage("Get a Subcriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ?
        SecurityUtil.getCurrentUserLogin().get() : "";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }
    

}
