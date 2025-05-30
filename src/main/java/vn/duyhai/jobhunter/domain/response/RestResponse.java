package vn.duyhai.jobhunter.domain.response;

public class RestResponse<T> {
    private int statusCode;
    private String error;
    // message có thể là string, hoặc arrayList 
    private Object message; 
    private T data;

    

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
