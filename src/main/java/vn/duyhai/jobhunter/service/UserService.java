package vn.duyhai.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.domain.dto.Meta;
import vn.duyhai.jobhunter.domain.dto.ResCreateUserDTO;
import vn.duyhai.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.duyhai.jobhunter.domain.dto.ResUserDTO;
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

    public boolean isEmailExist(String email){
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user){
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        return res;
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
        //remove sensitive data
        List<ResUserDTO> listUser = pageUsers.getContent().stream().map(item -> new ResUserDTO(
            item.getId(),
            item.getName(),
            item.getEmail(),
            item.getAge(),
            item.getGender(),
            item.getAddress(),
            item.getCreatedAt(),
            item.getUpdatedAt()))
            .collect(Collectors.toList());
        
        rs.setResult(listUser);

        return rs;
    }

    public ResUserDTO convertToResUserDTO(User user){
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateDTO(User user){
       
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge()) ;
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public User handleUpdateUser(User user){
        User currentUser = this.fetchUserById(user.getId());
        if(currentUser != null){
            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());
            currentUser.setAge(user.getAge());
            currentUser.setAddress(user.getAddress());
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }
    public User handleGetUserByUserName(String username){
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token,String email){
        User currentUser = this.handleGetUserByUserName(email);
        if(currentUser != null){
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }
}
