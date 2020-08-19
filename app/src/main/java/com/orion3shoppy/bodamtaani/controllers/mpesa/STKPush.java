

package com.orion3shoppy.bodamtaani.controllers.mpesa;

import com.google.gson.annotations.SerializedName;

/**
 * Created  on 5/28/2017.
 */

public class STKPush {

    @SerializedName("BusinessShortCode")
    private String businessShortCode;
    @SerializedName("Password")
    private String password;
    @SerializedName("Timestamp")
    private String timestamp;
    @SerializedName("TransactionType")
    private String transactionType;
    @SerializedName("Amount")
    private String amount;
    @SerializedName("PartyA")
    private String partyA;
    @SerializedName("PartyB")
    private String partyB;
    @SerializedName("PhoneNumber")
    private String phoneNumber;
    @SerializedName("CallBackURL")
    private String callBackURL;
    @SerializedName("AccountReference")
    private String accountReference;
    @SerializedName("TransactionDesc")
    private String transactionDesc;


    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        MerchantRequestID = merchantRequestID;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public void setCheckoutRequestID(String checkoutRequestID) {
        CheckoutRequestID = checkoutRequestID;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        ResponseDescription = responseDescription;
    }

    public String getCustomerMessage() {
        return CustomerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        CustomerMessage = customerMessage;
    }

    @SerializedName("MerchantRequestID")
    private String MerchantRequestID;
    @SerializedName("CheckoutRequestID")
    private String CheckoutRequestID;
    @SerializedName("ResponseDescription")
    private String ResponseDescription;
    @SerializedName("CustomerMessage")
    private String CustomerMessage;


    public STKPush(String businessShortCode, String password, String timestamp, String transactionType, String amount, String partyA, String partyB, String phoneNumber, String callBackURL, String accountReference, String transactionDesc) {
        this.businessShortCode = businessShortCode;
        this.password = password;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.amount = amount;
        this.partyA = partyA;
        this.partyB = partyB;
        this.phoneNumber = phoneNumber;
        this.callBackURL = callBackURL;
        this.accountReference = accountReference;
        this.transactionDesc = transactionDesc;
    }
}