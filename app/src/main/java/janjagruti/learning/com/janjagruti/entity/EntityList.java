package janjagruti.learning.com.janjagruti.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntityList<T> {

    @SerializedName("count")
    private int total;

    @SerializedName("rows")
    private List<T> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
