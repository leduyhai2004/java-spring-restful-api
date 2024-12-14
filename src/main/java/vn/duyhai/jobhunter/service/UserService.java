package vn.duyhai.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.domain.dto.Meta;
import vn.duyhai.jobhunter.domain.dto.ResultPaginationDTO;
import vn.duyhai.jobhunter.repository.UserRepository;
@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public User handleCreateUser(User user){
         return this.userRepository.save(user);
    }
    public void handleDeleteUser(long id){
        this.userRepository.deleteById(id);
    }
    public User fetchUserById(long id){
        Optional<User> userOptional = this.userRepository.findById(id);
        if(userOptional.isPresent()){
            return  userOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable){
        Page<User> pageUsers = this.userRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUsers.getTotalPages());
        mt.setTotal(pageUsers.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageUsers.getContent());

        return rs;
    }

    public User handleUpdateUser(User user){
        User currentUser = this.fetchUserById(user.getId());
        if(currentUser != null){
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPassword(user.getPassword());
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }
    public User handleGetUserByUserName(String username){
        return this.userRepository.findByEmail(username);
    }
}
