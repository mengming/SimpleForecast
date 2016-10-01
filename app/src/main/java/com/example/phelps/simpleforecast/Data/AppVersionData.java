package com.example.phelps.simpleforecast.Data;

import java.io.Serializable;

/**
 * Created by Phelps on 2016/10/1.
 */

public class AppVersionData implements Serializable{

    /**
     * name : test
     * version : 1.0
     * change_log : none
     * install_url : test.com
     */

    private String name;
    private String version;
    private String change_log;
    private String install_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChange_log() {
        return change_log;
    }

    public void setChange_log(String change_log) {
        this.change_log = change_log;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }
}
