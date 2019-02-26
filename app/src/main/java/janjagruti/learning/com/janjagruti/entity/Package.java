package janjagruti.learning.com.janjagruti.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Package {

    protected int id;
    protected String title;
    protected String subtitle;
    protected String details;
    protected int duration;
    protected String durationScale;
    protected float rate;
    protected User createdBy;
    protected User modifiedBy;
    protected boolean isActive;

    @SerializedName("UserPackage")
    protected UserPackage userPackage;

    @SerializedName("created_at")
    protected Date createdAt;

    @SerializedName("updated_at")
    protected Date updatedAt;

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
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDurationScale() {
        return durationScale;
    }

    public void setDurationScale(String durationScale) {
        this.durationScale = durationScale;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public UserPackage getUserPackage() {
        return userPackage;
    }

    public void setUserPackage(UserPackage userPackage) {
        this.userPackage = userPackage;
    }

    @Override
    public String toString() {
        return "Package{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", details='" + details + '\'' +
                ", duration=" + duration +
                ", durationScale='" + durationScale + '\'' +
                ", rate=" + rate +
                ", createdBy=" + createdBy +
                ", modifiedBy=" + modifiedBy +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
