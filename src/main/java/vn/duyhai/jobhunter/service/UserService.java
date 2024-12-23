package vn.duyhai.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.duyhai.jobhunter.domain.Company;
import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.domain.response.ResCreateUserDTO;
import vn.duyhai.jobhunter.domain.response.ResUpdateUserDTO;
import vn.duyhai.jobhunter.domain.response.ResUserDTO;
import vn.duyhai.jobhunter.domain.response.ResultPaginationDTO;
import vn.duyhai.jobhunter.repository.UserRepository;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    public UserService(UserRepository userRepository,CompanyService companyService){
        this.userRepository = userRepository;
        this.companyService = companyService;
    }
    public User handleCreateUser(User user){
        //check exist company
        if(user.getCompany() != null){
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
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
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());

        if(user.getCompany() != null){
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompanyUser(com);
        }

        return res;
    }

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable){
        Page<User> pageUsers = this.userRepository.findAll(spec,pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

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
            item.getUpdatedAt(),
            new ResUserDTO.CompanyUser(
                item.getCompany() != null ? item.getCompany().getId() : 0,
                item.getCompany() != null ? item.getCompany().getName() : null)))
        .collect(Collectors.toList());
        
        rs.setResult(listUser);

        return rs;
    }

    public ResUserDTO convertToResUserDTO(User user){
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();

        if(user.getCompany() != null){
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompanyUser(com);
        }
        
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

    public User getUserByRefreshTokenAndEmail(String token, String email){
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
