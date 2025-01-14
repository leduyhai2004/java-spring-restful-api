package vn.duyhai.jobhunter.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.duyhai.jobhunter.domain.Job;
import vn.duyhai.jobhunter.domain.Skill;
import vn.duyhai.jobhunter.domain.Subscriber;
import vn.duyhai.jobhunter.domain.response.email.ResEmailJob;
import vn.duyhai.jobhunter.repository.JobRepository;
import vn.duyhai.jobhunter.repository.SkillRepository;
import vn.duyhai.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository,
            EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }
    public boolean isExistsByEmail(String email){
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber findById(long id){
        Optional<Subscriber> subOptional = this.subscriberRepository.findById(id);
        if(subOptional.isPresent()){
            return subOptional.get();
        }
        return null;
    }
    public Subscriber create(Subscriber sub){
        //check skill
        if(sub.getSkills() != null){
            List<Long> reqSkills = sub.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            sub.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(sub);
    }
    public Subscriber update(Subscriber subDB, Subscriber subReq){
        //check skill
        if(subReq.getSkills() != null){
            List<Long> reqSkills = subReq.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subDB);
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
 
                        List<ResEmailJob> arr = listJobs.stream().map(
                        job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
 
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    } 
                } 
            } 
        } 
    }

    public ResEmailJob convertJobToSendEmail(Job job) { 
        ResEmailJob res = new ResEmailJob(); 
        res.setName(job.getName()); 
        res.setSalary(job.getSalary()); 
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName())); 
        List<Skill> skills = job.getSkills(); 
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new 
        ResEmailJob.SkillEmail(skill.getName())) 
                .collect(Collectors.toList()); 
        res.setSkills(s); 
        return res; 
    }

    public Subscriber findByEmail(String email){
        return this.subscriberRepository.findByEmail(email);
    }

}
