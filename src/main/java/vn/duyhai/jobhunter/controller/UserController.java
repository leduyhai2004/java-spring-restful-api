package vn.duyhai.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.domain.response.ResCreateUserDTO;
import vn.duyhai.jobhunter.domain.response.ResUpdateUserDTO;
import vn.duyhai.jobhunter.domain.response.ResUserDTO;
import vn.duyhai.jobhunter.domain.response.ResultPaginationDTO;
import vn.duyhai.jobhunter.service.UserService;
import vn.duyhai.jobhunter.util.anotation.ApiMessage;
import vn.duyhai.jobhunter.util.error.IdInvalidException;



@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public UserController(UserService userService,PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postmanUser) throws IdInvalidException{
        boolean isEmailExist = this.userService.isEmailExist(postmanUser.getEmail());
        if(isEmailExist){
            throw new IdInvalidException(
                "Email "+ postmanUser.getEmail()+" is exist, please use different email"
            );
        }
        String hashPassWord = this.passwordEncoder.encode(postmanUser.getPassword());
        postmanUser.setPassword(hashPassWord);
        System.out.println(hashPassWord);
        User newUser = this.userService.handleCreateUser(postmanUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException{  
        User checkUser = this.userService.fetchUserById(id);
        if(checkUser == null){
            throw new IdInvalidException("User with id= "+id +" is not exist");
        }
       this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get detail user")
    public ResponseEntity<ResUserDTO> getUserDetail(@PathVariable("id") long id) throws IdInvalidException {
        User checkUser = this.userService.fetchUserById(id);
        if(checkUser == null){
            throw new IdInvalidException("User with id= "+id +" is not exist");
        }
       
       return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(checkUser));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
        @Filter Specification<User> spec, Pageable pageable ) {
        
         
    return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUsers(spec,pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<ResUpdateUserDTO> updateUser( @RequestBody User user) throws IdInvalidException {
        User user1 =  this.userService.handleUpdateUser(user);
        if(user1 == null){
            throw new IdInvalidException("User with id= "+user.getId() +" is not exist");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateDTO(user1));
    }
    
}
