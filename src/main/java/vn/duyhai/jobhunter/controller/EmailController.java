package vn.duyhai.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.duyhai.jobhunter.service.EmailService;
import vn.duyhai.jobhunter.service.SubscriberService;
import vn.duyhai.jobhunter.util.anotation.ApiMessage;


@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;
    public EmailController(EmailService emailService,SubscriberService subscriberService){
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }
    @GetMapping("/email")
    @ApiMessage("Send email")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public String sendEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("duyha0207@gmail.com", "test send email",
        // "<h1><b>hello</b></h1>", false, true);
        // this.emailService.sendEmailFromTemplateSync("duyha0207@gmail.com", "test send email", "job");
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }
    
}
