package io.shaner.dto;

public class Pod {
    
    private String name;
    private String ipAddress;

    public Pod(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }
}
