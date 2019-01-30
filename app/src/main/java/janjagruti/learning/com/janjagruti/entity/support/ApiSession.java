package janjagruti.learning.com.janjagruti.entity.support;

public class ApiSession {

    private String token;
    private Long issuedAt;
    private Long expire;

    public ApiSession() { }

    public ApiSession(String token, Long issuedAt, Long expire) {
        this.token = token;
        this.issuedAt = issuedAt;
        this.expire = expire;
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

    @Override
    public String toString() {
        return "ApiSession{" +
                "token='" + token + '\'' +
                ", issuedAt=" + issuedAt +
                ", expire=" + expire +
                '}';
    }
}
