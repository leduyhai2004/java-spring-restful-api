package vn.duyhai.jobhunter.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.domain.request.ReqLoginDTO;
import vn.duyhai.jobhunter.domain.response.ResCreateUserDTO;
import vn.duyhai.jobhunter.domain.response.ResLoginDTO;
import vn.duyhai.jobhunter.service.UserService;
import vn.duyhai.jobhunter.util.SecurityUtil;
import vn.duyhai.jobhunter.util.anotation.ApiMessage;
import vn.duyhai.jobhunter.util.error.IdInvalidException;




@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${duyhai.jwt.refresh-token-validity-in-seconds}") 
    private long refreshTokenExpiration;
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                                SecurityUtil securityUtil,UserService userService,
                                PasswordEncoder passwordEncoder){
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> postMethodName(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if(isEmailExist){
            throw new IdInvalidException("email "+ user.getEmail()+" is existed, please use different email");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }
    

    @PostMapping("auth/login")
    public ResponseEntity<ResLoginDTO> login( @Valid @RequestBody ReqLoginDTO loginDTO){
        //Nạp input gồm username/password vào Security 
        UsernamePasswordAuthenticationToken authenticationToken  
        = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()); 
        
        //xác thực người dùng => cần viết hàm loadUserByUsername 
        Authentication authentication = 
        authenticationManagerBuilder.getObject().authenticate(authenticationToken); 

        
        // set thông tin người dùng đăng nhập vào context(có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication); 

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());
        if(currentUserDB != null){
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(),
                                            currentUserDB.getEmail(),currentUserDB.getName(),
                                            currentUserDB.getRole());
            res.setUser(userLogin);  
        }
        //create access token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(),res);
        res.setAccessToken(access_token);

        //create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        //update user
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        //set cookies
        ResponseCookie responseCookie = ResponseCookie
        .from("response_cookies",refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();

        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(res);
    }

    @GetMapping("auth/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDB = this.userService.handleGetUserByUserName(email); // null
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if(currentUserDB != null){
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());

            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }
    
    @GetMapping("auth/refresh")
    @ApiMessage("Get User By refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
        @CookieValue(name = "response_cookies") String refresh_token
    ) throws IdInvalidException {
        //check valid
       Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
       String email = decodedToken.getSubject();

       //check user by Token + email
       User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
       if(currentUser == null){
        throw new IdInvalidException("refresh token is invalid"); 
       }

       // create new token and set as cookies
       ResLoginDTO res = new ResLoginDTO();
       User currentUserDB = this.userService.handleGetUserByUserName(email);
       if(currentUserDB != null){
           ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(),
                                           currentUserDB.getEmail(),currentUserDB.getName(),
                                           currentUserDB.getRole());
           res.setUser(userLogin);  
       }
       String access_token = this.securityUtil.createAccessToken(email,res);
       res.setAccessToken(access_token);

       //create refresh token
       String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
       //update user
       this.userService.updateUserToken(new_refresh_token, email);

       //set cookies
       ResponseCookie responseCookie = ResponseCookie
       .from("response_cookies",new_refresh_token)
       .httpOnly(true)
       .secure(true)
       .path("/")
       .maxAge(refreshTokenExpiration)
       .build();

       return ResponseEntity.ok()
       .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
       .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
       String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
       if(email.equals("")){
        throw new IdInvalidException("Access token không hợp lệ");
       }
        //update refresh token = null
        this.userService.updateUserToken(null, email);

        //removes refresh token cookies
        ResponseCookie deleteSpringCookie = ResponseCookie
        .from("response_cookies",null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build(); 
       return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(null);
    }
    
    
}
