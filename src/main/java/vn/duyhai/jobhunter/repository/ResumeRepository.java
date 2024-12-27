package vn.duyhai.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.duyhai.jobhunter.domain.Resume;

@Repository
public interface  ResumeRepository extends JpaRepository<Resume,Long>,JpaSpecificationExecutor<Resume>  {
    
}
