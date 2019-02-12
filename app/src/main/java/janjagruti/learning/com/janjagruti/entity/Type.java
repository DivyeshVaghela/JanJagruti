package janjagruti.learning.com.janjagruti.entity;

import janjagruti.learning.com.janjagruti.config.ApiConfig;

public class Type {

    private int id;
    private String name;
    private String value;
    private String details;
    private String logoPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDetails() {
        return details;
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
}
