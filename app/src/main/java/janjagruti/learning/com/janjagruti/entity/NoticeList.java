package janjagruti.learning.com.janjagruti.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoticeList {

    @SerializedName("count")
    private int totalNotice;

    @SerializedName("rows")
    private List<Notice> notices;

    public int getTotalNotice() {
        return totalNotice;
    }

    public void setTotalNotice(int totalNotice) {
        this.totalNotice = totalNotice;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }
}
