
package com.cavox.RetroModels.CreatePaymentIntentWithSwitchRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PaymentMethodOptions {

    @SerializedName("card")
    @Expose
    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("card", card).toString();
    }

}
