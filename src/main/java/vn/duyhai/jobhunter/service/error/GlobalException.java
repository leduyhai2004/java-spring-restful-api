package vn.duyhai.jobhunter.service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.duyhai.jobhunter.domain.RestResponse;

@RestControllerAdvice
public class GlobalException {   
    @ExceptionHandler(value = IdInvalidException.class) 
    public ResponseEntity<RestResponse<Object>> handleBlogAlreadyExistsException(IdInvalidException idException) { 
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(idException.getMessage());
        res.setMessage("error id");
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res); 
    }
}
