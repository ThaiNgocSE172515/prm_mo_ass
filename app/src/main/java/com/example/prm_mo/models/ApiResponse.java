package com.example.prm_mo.models;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ErrorResponse error;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public ErrorResponse getError() { return error; }
    public void setError(ErrorResponse error) { this.error = error; }

    public static class ErrorResponse {
        private String code;
        // You can add details list if needed
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }
}
