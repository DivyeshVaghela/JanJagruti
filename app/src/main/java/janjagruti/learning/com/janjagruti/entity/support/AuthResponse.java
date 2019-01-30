package janjagruti.learning.com.janjagruti.entity.support;

import java.util.Map;

import janjagruti.learning.com.janjagruti.entity.User;

public class AuthResponse {

    private boolean success;
    private String message;
    private Map<String, String> fieldErrors;
    private String token;
    private Long issuedAt;
    private Long expire;
    private User userProfile;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public User getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(User userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", fieldErrors=" + fieldErrors +
                ", token='" + token + '\'' +
                ", issuedAt=" + issuedAt +
                ", expire=" + expire +
                ", userProfile=" + userProfile +
                '}';
    }
}
