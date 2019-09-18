package com.example.childproject.Model;

public class ChildUser {
    private String cEmail, cName, cPassword, cPhone, cAvatarUrl, Ckey;

    public ChildUser() {
    }

    public ChildUser(String cEmail, String cName, String cPassword, String cPhone, String cAvatarUrl, String ckey) {
        this.cEmail = cEmail;
        this.cName = cName;
        this.cPassword = cPassword;
        this.cPhone = cPhone;
        this.cAvatarUrl = cAvatarUrl;
        Ckey = ckey;
    }

    public String getcEmail() {
        return cEmail;
    }

    public void setcEmail(String cEmail) {
        this.cEmail = cEmail;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcPassword() {
        return cPassword;
    }

    public void setcPassword(String cPassword) {
        this.cPassword = cPassword;
    }

    public String getcPhone() {
        return cPhone;
    }

    public void setcPhone(String cPhone) {
        this.cPhone = cPhone;
    }

    public String getcAvatarUrl() {
        return cAvatarUrl;
    }

    public void setcAvatarUrl(String cAvatarUrl) {
        this.cAvatarUrl = cAvatarUrl;
    }

    public String getCkey() {
        return Ckey;
    }

    public void setCkey(String ckey) {
        Ckey = ckey;
    }
}
