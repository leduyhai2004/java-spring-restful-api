package vn.duyhai.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.duyhai.jobhunter.domain.User;


@Repository
public interface  UserRepository extends JpaRepository<User,Long>{
    
}
