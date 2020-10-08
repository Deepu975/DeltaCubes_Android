
package com.cavox.RetroModels.CreatePaymentIntentWithSwitchRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Card {

    @SerializedName("installments")
    @Expose
    private Object installments;
    @SerializedName("request_three_d_secure")
    @Expose
    private String requestThreeDSecure;

    public Object getInstallments() {
        return installments;
    }

    public void setInstallments(Object installments) {
        this.installments = installments;
    }

    public String getRequestThreeDSecure() {
        return requestThreeDSecure;
    }

    public void setRequestThreeDSecure(String requestThreeDSecure) {
        this.requestThreeDSecure = requestThreeDSecure;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("installments", installments).append("requestThreeDSecure", requestThreeDSecure).toString();
    }

}
