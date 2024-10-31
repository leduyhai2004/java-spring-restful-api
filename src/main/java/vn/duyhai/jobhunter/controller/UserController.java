package vn.duyhai.jobhunter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.service.UserService;
import vn.duyhai.jobhunter.service.error.IdInvalidException;



@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User postmanUser){
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
    public ResponseEntity<List<User>> getAllUsers() {
       return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUsers());
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser( @RequestBody User user) {
        User user1 =  this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(user1);
    }
    
}
