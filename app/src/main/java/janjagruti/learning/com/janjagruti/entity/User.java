package janjagruti.learning.com.janjagruti.entity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {

    private String id;
    private String username;
    private String email;
    private String mobileNo;
    private String password;

    @SerializedName("Packages")
    private List<Package> packages = new ArrayList<>();
    private Package activePackage;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    public User() { }

    public User(String username, String email, String mobileNo, String password) {
        this.username = username;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
        if (this.packages != null && this.packages.size() > 0)
            this.activePackage = this.packages.get(0);
    }

    public Package getActivePackage() {
        if (activePackage == null && this.packages != null && this.packages.size() > 0)
            this.activePackage = this.packages.get(0);
        return activePackage;
    }

    public void setActivePackage(Package activePackage) {
        this.activePackage = activePackage;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", password='" + password + '\'' +
                ", packages=" + packages +
                ", activePackage=" + getActivePackage() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
