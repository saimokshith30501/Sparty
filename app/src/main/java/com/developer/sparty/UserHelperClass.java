package com.developer.sparty;

public class UserHelperClass {
    String FULLNAME,EMAIL,PHONE,PASSWORD;

    public UserHelperClass() {
    }

    public UserHelperClass(String FULLNAME,String EMAIL, String PHONE, String PASSWORD) {
        this.FULLNAME = FULLNAME;
        this.EMAIL = EMAIL;
        this.PHONE = PHONE;
        this.PASSWORD = PASSWORD;
    }

    public String getFULLNAME() {
        return FULLNAME;
    }

    public void setFULLNAME(String FULLNAME) {
        this.FULLNAME = FULLNAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }
}
