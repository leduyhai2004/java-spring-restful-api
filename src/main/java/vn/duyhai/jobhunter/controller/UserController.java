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

import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.domain.dto.ResultPaginationDTO;
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
    public ResponseEntity<User> createNewUser(@RequestBody User postmanUser){
        String hashPassWord = this.passwordEncoder.encode(postmanUser.getPassword());
        postmanUser.setPassword(hashPassWord);
        System.out.println(hashPassWord);
        User newUser = this.userService.handleCreateUser(postmanUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException{
        if(id > 1500){
            throw new IdInvalidException("ID too large");
        }
       this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted user");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserDetail(@PathVariable("id") long id) {
       User user =  this.userService.fetchUserById(id);
       return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
        @Filter Specification<User> spec, Pageable pageable ) {

       return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUsers(spec,pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser( @RequestBody User user) {
        User user1 =  this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(user1);
    }
    
}
