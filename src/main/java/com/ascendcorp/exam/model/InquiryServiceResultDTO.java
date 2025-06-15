package com.ascendcorp.exam.model;

import java.io.Serializable;

public class InquiryServiceResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private java.lang.String tranID;

    private String namespace;

    private java.lang.String reasonCode;

    private java.lang.String reasonDesc;

    private java.lang.String balance;

    private java.lang.String ref_no1;

    private java.lang.String ref_no2;

    private java.lang.String amount;

    private String accountName = null;

    public java.lang.String getTranID() {
        return tranID;
    }

    public void setTranID(java.lang.String tranID) {
        this.tranID = tranID;
    }

    public java.lang.String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(java.lang.String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public java.lang.String getReasonDesc() {
        return reasonDesc;
    }

    public void setReasonDesc(java.lang.String reasonDesc) {
        this.reasonDesc = reasonDesc;
    }

    public java.lang.String getBalance() {
        return balance;
    }

    public void setBalance(java.lang.String balance) {
        this.balance = balance;
    }

    public java.lang.String getRef_no1() {
        return ref_no1;
    }

    public void setRef_no1(java.lang.String ref_no1) {
        this.ref_no1 = ref_no1;
    }

    public java.lang.String getRef_no2() {
        return ref_no2;
    }

    public void setRef_no2(java.lang.String ref_no2) {
        this.ref_no2 = ref_no2;
    }

    public java.lang.String getAmount() {
        return amount;
    }

    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }



    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "InquiryServiceResultDTO [tranID=" + tranID + ",namespace = "+namespace + ", reasonCode="
                + reasonCode + ", reasonDesc=" + reasonDesc + ", balance="
                + balance + ", ref_no1=" + ref_no1 + ", ref_no2=" + ref_no2
                + ", amount=" + amount + " ,account_name="+accountName+"  ]";
    }



}
