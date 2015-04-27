package domain.entity;

import java.util.Date;

public class UserSession {
    private int  id;
    private String token;
    private Date expiration;
    private int userId;

    public UserSession() {
    }

    public UserSession(String token, Date expiration, int userId) {
        this.token = token;
        this.expiration = expiration;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public boolean isExpired() {
        return (new Date()).after(expiration);
    }
}