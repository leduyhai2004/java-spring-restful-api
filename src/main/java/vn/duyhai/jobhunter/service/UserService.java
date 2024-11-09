package vn.duyhai.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.duyhai.jobhunter.domain.User;
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

    public List<User> fetchAllUsers(){
        return this.userRepository.findAll();
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
