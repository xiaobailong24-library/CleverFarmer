package com.qtech.cleverfarmer.Update;

/**
 * Created by xiaobailong24 on 2016/5/14.
 * Class:
 * Describe:
 * Version:
 */
public class UpdateInfo {
    private String version;
    private String url;
    private String description;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return "UpdataInfo [version=" + version + ", url=" + url
                + ", description=" + description + "]";
    }
}
