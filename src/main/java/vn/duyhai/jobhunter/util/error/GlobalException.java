package vn.duyhai.jobhunter.util.error;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.duyhai.jobhunter.domain.response.RestResponse;

@RestControllerAdvice
public class GlobalException {   
    @ExceptionHandler(value = {
        UsernameNotFoundException.class,
        BadCredentialsException.class,
        IdInvalidException.class,
    }) // lỗi ở controller thì sẽ nhảy vào đây
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex) { 
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Exception occrur ...");
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res); 
    } // xử lý điền sai thông tin đăng nhập và mật khẩu

    @ExceptionHandler(value = MethodArgumentNotValidException.class) 
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex){
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

    }

    @ExceptionHandler(value={
        NoResourceFoundException.class
    })
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value()) ;
        res.setMessage(ex.getMessage());
        res.setError("404 Not found. URL may not exist");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
        StorageException.class,
    }) // lỗi ở controller thì sẽ nhảy vào đây
    public ResponseEntity<RestResponse<Object>> handleFileUploadException(Exception ex) { 
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Something upload file ...");
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res); 
    } // xử lý điền sai thông tin đăng nhập và mật khẩu

    @ExceptionHandler(value={
        PermissionException.class
    })
    public ResponseEntity<RestResponse<Object>> handlePermissionException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value()) ;
        res.setMessage(ex.getMessage());
        res.setError("Forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    @ExceptionHandler(Exception.class) 
    public ResponseEntity<RestResponse<Object>> handleAllException(Exception ex) { 
        RestResponse<Object> res = new RestResponse<Object>(); 
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); 
        res.setMessage(ex.getMessage()); 
        res.setError("Internal Server Error"); 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res); 
    }
}
