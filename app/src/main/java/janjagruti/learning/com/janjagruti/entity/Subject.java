package janjagruti.learning.com.janjagruti.entity;

import java.util.Date;

import janjagruti.learning.com.janjagruti.config.ApiConfig;

public class Subject {

    private int id;
    private String title;
    private String subtitle;
    private String details;
    private String logoPath;
    private boolean isActive;
    private User createdBy;
    private Date created_at;
    private Date updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDetails() {
        return (details == null || details.equals("null")) ? "" : details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public String getLogoUrl(){
        return ApiConfig.BASE_URL + getLogoPath();
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

}
