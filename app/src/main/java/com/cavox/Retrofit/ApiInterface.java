package com.cavox.Retrofit;





import com.cavox.RetroModels.CreatePaymentIntentRes.CreatePaymentIntentRes;
import com.cavox.RetroModels.CreatePaymentIntentWithSwitchRes.CreatePaymentIntentWithSwitchRes;
import com.cavox.RetroModels.verifystripepaymentWithSwitchRes.VerifystripepaymentWithSwitchRes;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


    @POST("v1/payment_intents")
    @FormUrlEncoded
    Call<CreatePaymentIntentRes> createPaymentIntentReq(@Field("amount") String username,
                                                     @Field("currency") String password
                        );
   /*

    {
        "id": "pi_1FM1HoHjMNmZQQqsPhVWOMar",
            "object": "payment_intent",
            "allowed_source_types": [
        "card"
  ],
        "amount": 50,
            "amount_capturable": 0,
            "amount_received": 0,
            "application": null,
            "application_fee_amount": null,
            "canceled_at": null,
            "cancellation_reason": null,
            "capture_method": "automatic",
            "charges": {
        "object": "list",
                "data": [

    ],
        "has_more": false,
                "total_count": 0,
                "url": "/v1/charges?payment_intent=pi_1FM1HoHjMNmZQQqsPhVWOMar"
    },
        "client_secret": "pi_1FM1HoHjMNmZQQqsPhVWOMar_secret_YUJvxU04SofAK20YyTxY6zRuL",
            "confirmation_method": "automatic",
            "created": 1569282584,
            "currency": "usd",
            "customer": null,
            "description": null,
            "invoice": null,
            "last_payment_error": null,
            "livemode": true,
            "metadata": {
    },
        "next_action": null,
            "next_source_action": null,
            "on_behalf_of": null,
            "payment_method": null,
            "payment_method_options": {
        "card": {
            "request_three_d_secure": "automatic"
        }
    },
        "payment_method_types": [
        "card"
  ],
        "receipt_email": null,
            "review": null,
            "setup_future_usage": null,
            "shipping": null,
            "source": null,
            "statement_descriptor": null,
            "statement_descriptor_suffix": null,
            "status": "requires_source",
            "transfer_data": null,
            "transfer_group": null
    }

    */





    @POST("api/createstripepaymentintent")
    @FormUrlEncoded
    Call<CreatePaymentIntentWithSwitchRes> createPaymentIntentWithSwitch(@Field("username") String username,
                                                                         @Field("emailid") String emailid,
                                                                         @Field("amount") String amount,
                                                                         @Field("currency") String currency
    );

/*
{"data":{"code":200,"message":{"id":"pi_1FWj40HjMNmZQQqsfGPodDqu","object":"payment_intent","allowed_source_types":["card"],"amount":100,"amount_capturable":0,"amount_received":0,"application":null,"application_fee_amount":null,"canceled_at":null,"cancellation_reason":null,"capture_method":"automatic","charges":{"object":"list","data":[],"has_more":false,"total_count":0,"url":"/v1/charges?payment_intent=pi_1FWj40HjMNmZQQqsfGPodDqu"},"client_secret":"pi_1FWj40HjMNmZQQqsfGPodDqu_secret_eiUZx0IKEu8b2OfENStfHk42Q","confirmation_method":"automatic","created":1571834144,"currency":"usd","customer":null,"description":null,"invoice":null,"last_payment_error":null,"livemode":false,"metadata":{"emailid":"testmail@gmail.com","userid":"66","username":" 919553632604"},"next_action":null,"next_source_action":null,"on_behalf_of":null,"payment_method":null,"payment_method_options":{"card":{"installments":null,"request_three_d_secure":"automatic"}},"payment_method_types":["card"],"receipt_email":null,"review":null,"setup_future_usage":null,"shipping":null,"source":null,"statement_descriptor":null,"statement_descriptor_suffix":null,"status":"requires_source","transfer_data":null,"transfer_group":null}}}
 */


    @POST("api/verifystripepayment")
    @FormUrlEncoded
    Call<VerifystripepaymentWithSwitchRes> verifystripepaymentWithSwitch(@Field("username") String username,
                                                                         @Field("paymentid") String paymentid,
                                                                         @Field("emailid") String emailid,
                                                                         @Field("amount") String amount

    );

}
