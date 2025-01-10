package vn.duyhai.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.duyhai.jobhunter.domain.Permission;
import vn.duyhai.jobhunter.domain.Role;
import vn.duyhai.jobhunter.domain.User;
import vn.duyhai.jobhunter.service.UserService;
import vn.duyhai.jobhunter.util.SecurityUtil;
import vn.duyhai.jobhunter.util.error.PermissionException;


public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired UserService userService;

    @Override
    @Transactional
    public boolean preHandle( 
            HttpServletRequest request, 
            HttpServletResponse response, Object handler) 
            throws Exception { 
 
      String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE); 
        String requestURI = request.getRequestURI(); 
        String httpMethod = request.getMethod(); 
        System.out.println(">>> RUN preHandle"); 
        System.out.println(">>> path= " + path); 
        System.out.println(">>> httpMethod= " + httpMethod); 
        System.out.println(">>> requestURI= " + requestURI); 

        //check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true 
        ? SecurityUtil.getCurrentUserLogin().get() : "";
        if(email != null && !email.isEmpty()){
            User user = userService.handleGetUserByUserName(email);
            if(user != null){
                Role role = user.getRole();
                if(role != null){
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllowed = permissions.stream().anyMatch(item -> 
                    item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if(isAllowed == false){
                        throw new PermissionException("you do not have permission");
                    }
                     System.out.println("------>is allow:"+isAllowed);
                }else{
                    throw new PermissionException("you do not have permission");
                }
                
            }
        }

        return true; 
    } 
} 
