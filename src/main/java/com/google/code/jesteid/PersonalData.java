package com.google.code.jesteid;

public class PersonalData {
    
    private String idCode;
    
    private String givenName;
    private String givenName2;
    private String surName;
    private String sex;
    private String dateOfBirth;
    private String placeOfBirth;
    private String citizenship;
    private String rightType;
    
    private String documentNumber;
    private String dateOfIssue;
    private String dateOfExpiry;
    
    private String memo1;
    private String memo2;
    private String memo3;
    private String memo4;
    
    public String getIdCode() {
        return idCode;
    }
    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }
    
    public String getGivenName() {
        return givenName;
    }
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    
    public String getGivenName2() {
        return givenName2;
    }
    public void setGivenName2(String givenName2) {
        this.givenName2 = givenName2;
    }
    
    public String getSurName() {
        return surName;
    }
    public void setSurName(String surName) {
        this.surName = surName;
    }
    
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }
    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }
    
    public String getCitizenship() {
        return citizenship;
    }
    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }
    
    public String getRightType() {
        return rightType;
    }
    public void setRightType(String rightType) {
        this.rightType = rightType;
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    public String getDateOfIssue() {
        return dateOfIssue;
    }
    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }
    
    public String getDateOfExpiry() {
        return dateOfExpiry;
    }
    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }
    
    public String getMemo1() {
        return memo1;
    }
    public void setMemo1(String memo1) {
        this.memo1 = memo1;
    }
    
    public String getMemo2() {
        return memo2;
    }
    public void setMemo2(String memo2) {
        this.memo2 = memo2;
    }
    
    public String getMemo3() {
        return memo3;
    }
    public void setMemo3(String memo3) {
        this.memo3 = memo3;
    }
    
    public String getMemo4() {
        return memo4;
    }
    public void setMemo4(String memo4) {
        this.memo4 = memo4;
    }
    
    public String toString() {
        return idCode + ": " + givenName + (givenName2 != null ? " " + givenName2 : "") + " " + surName;
    }
    
}
