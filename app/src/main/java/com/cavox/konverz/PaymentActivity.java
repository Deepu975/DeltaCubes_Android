package com.cavox.konverz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.deltacubes.R;
import com.cavox.RetroModels.CreatePaymentIntentRes.CreatePaymentIntentRes;
import com.cavox.Retrofit.ApiClient;
import com.cavox.Retrofit.ApiInterface;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentAuthConfig;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.Address;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cavox.utils.GlobalVariables.LOG;

public class PaymentActivity extends Activity {
    private Stripe mStripe;
    //Card card;
    CardInputWidget cardInputWidget;
    ProgressDialog mdialog;
    public static String clientsecrete = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.stripe);


         cardInputWidget = new CardInputWidget(this);

        cardInputWidget.setCardNumber("4242424242424242");
        cardInputWidget.setExpiryDate(9,2023);
        cardInputWidget.setCvcCode("638");

        final PaymentAuthConfig.Stripe3ds2UiCustomization uiCustomization =
                new PaymentAuthConfig.Stripe3ds2UiCustomization.Builder()
                        .build();
        PaymentAuthConfig.init(new PaymentAuthConfig.Builder()
                .set3ds2Config(new PaymentAuthConfig.Stripe3ds2Config.Builder()
                        // set a 5 minute timeout for challenge flow
                        .setTimeout(5)
                        // customize the UI of the challenge flow
                        .setUiCustomization(uiCustomization)
                        .build())
                .build());

        //PaymentConfiguration.init(getApplicationContext(),"pk_live_bTfeQ3JcuEE1kOzorIWw906G");
        PaymentConfiguration.init(getApplicationContext(),"pk_test_3lLl1hxW06IBd3sGcgfKMYGu");
        mStripe = new Stripe(this, PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());

        LOG.info("Payment Activity "+"mStripe:"+mStripe);

        Button button_continue = (Button) findViewById(R.id.signUpButton);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //card = cardInputWidget.getCard();
                if(cardInputWidget.getCard()!=null) {
                    //createPaymentIntent("1", "usd", "sk_live_IogXqV1QUOx9qAU7c6VBK3bB");//This should be a server call. Remove from app and call switch provided api
                     createPaymentIntent("100", "usd", "sk_test_G3EKEncwCDk2T7fFG52KpL4x");//This should be a server call. Remove from app and call switch provided api

                } else {
                    Toast.makeText(getApplicationContext(), "Pls Enter correct card details!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Optional: customize the payment authentication experience.
        // PaymentAuthConfig.init() must be called before Stripe object
        // is instantiated.

        // now retrieve the PaymentIntent that was created on your backend
            }






    private ConfirmPaymentIntentParams createConfirmPaymentIntentParams(
            @NonNull String clientSecret,
            @NonNull PaymentMethod.BillingDetails billingDetails) {
        try {
            final PaymentMethodCreateParams.Card paymentMethodParamsCard =
                    cardInputWidget.getCard().toPaymentMethodParamsCard();
            final PaymentMethodCreateParams paymentMethodCreateParams =
                    PaymentMethodCreateParams.create(paymentMethodParamsCard,
                            billingDetails);
            return ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                    paymentMethodCreateParams, clientSecret,
                    "https://www.google.com");//yourapp://post-authentication-return-url
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }









    private void confirmPayment(@NonNull ConfirmPaymentIntentParams params) {
        if(params!=null) {
            LOG.info("Yes confirm payment");
            mStripe.confirmPayment(this, params);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid details", Toast.LENGTH_SHORT).show();
        }
    }













    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mStripe.onPaymentResult(requestCode, data,
                new ApiResultCallback<PaymentIntentResult>() {
                    @Override
                    public void onSuccess(@NonNull PaymentIntentResult result) {
                        // If authentication succeeded, the PaymentIntent will
                        // have user actions resolved; otherwise, handle the
                        // PaymentIntent status as appropriate (e.g. the
                        // customer may need to choose a new payment method)

                        final PaymentIntent paymentIntent = result.getIntent();
                        final PaymentIntent.Status status = paymentIntent.getStatus();

                        LOG.info("Payment Activity onActivityResult "+"status: "+status);

                        if (status == PaymentIntent.Status.Succeeded) {
                            // show success UI
                            LOG.info("Payment Activity onActivityResult "+"status: "+PaymentIntent.Status.Succeeded);

                        } else if (PaymentIntent.Status.RequiresPaymentMethod
                                == status) {
                            // attempt authentication again or
                            // ask for a new Payment Method
                            LOG.info("Payment Activity onActivityResult "+"status: "+PaymentIntent.Status.RequiresPaymentMethod);

                        }
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        LOG.info("Payment Activity onActivityResult "+"onError ");
                        e.printStackTrace();
                        // handle error
                    }
                });
    }






    public void createPaymentIntent(String amount, final String currency,String authtoken) {
        showProgressBar();
        ApiClient.getClientWithAuth(authtoken).create(ApiInterface.class).createPaymentIntentReq(amount, currency).enqueue(new Callback<CreatePaymentIntentRes>() {
            @Override
            public void onResponse(Call<CreatePaymentIntentRes> call, Response<CreatePaymentIntentRes> response) {
                LOG.info("CreatePaymentIntentRes onResponse:"+response.toString());
                if(response.isSuccessful()) {
                    LOG.info("CreatePaymentIntentRes onResponse success");
                     clientsecrete = response.body().getClientSecret();
                    LOG.info("clientsecrete: "+clientsecrete);
                    PaymentMethod.BillingDetails billingDetails = new PaymentMethod.BillingDetails.Builder().setAddress(new Address.Builder().setCity("Hyderabad").setCountry("IN").setLine1("Line1").setLine2("Line2").setPostalCode("500018").setState("Telegana").build()).setEmail("testpayment@gmail.com").setName("srr").setPhone("+919848012345").build();

                    confirmPayment(createConfirmPaymentIntentParams(clientsecrete,billingDetails));
                    cancelProgressBar();
                       } else {
                    cancelProgressBar();
                    LOG.info("CreatePaymentIntentRes onResponse failure");
                    Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_LONG).show();
                }
                cancelProgressBar();
            }

            @Override
            public void onFailure(Call<CreatePaymentIntentRes> call, Throwable t) {
                LOG.info("Unable to submit post to API."+t.getStackTrace());
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                cancelProgressBar();
            }

        });
    }


    public void showProgressBar() {
        try {
            mdialog = new ProgressDialog(PaymentActivity.this);
            mdialog.setMessage("Please wait...");
            mdialog.setCancelable(false);
            mdialog.getWindow().setDimAmount(0.0f); mdialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cancelProgressBar() {
        try {
            if (mdialog != null) {
                mdialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }





}