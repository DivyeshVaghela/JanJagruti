package janjagruti.learning.com.janjagruti.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notice {

    private int id;
    private String title;
    private String details;
    private User postedBy;

    @SerializedName("created_at")
    private Date postedDateTime;

    public Notice() {}

    public Notice(int id, String title, String details, User postedBy) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.postedBy = postedBy;
    }

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public User getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(User postedBy) {
        this.postedBy = postedBy;
    }

    public Date getPostedDateTime() {
        return postedDateTime;
    }

    public void setPostedDateTime(Date postedDateTime) {
        this.postedDateTime = postedDateTime;
    }
}
