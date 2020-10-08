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
import com.ca.wrapper.CSDataProvider;
import com.cavox.RetroModels.CreatePaymentIntentWithSwitchRes.CreatePaymentIntentWithSwitchRes;
import com.cavox.RetroModels.verifystripepaymentWithSwitchRes.VerifystripepaymentWithSwitchRes;
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

public class PaymentActivitySandBox extends Activity {
    private Stripe mStripe;
    //Card card;
    CardInputWidget cardInputWidget;
    ProgressDialog mdialog;
    public static String clientsecrete = "";
    //public static String paymentid = "";
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

        PaymentConfiguration.init(getApplicationContext(),"pk_test_3lLl1hxW06IBd3sGcgfKMYGu");
        mStripe = new Stripe(this, PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());

        LOG.info("Payment Activity "+"mStripe:"+mStripe);

        Button button_continue = (Button) findViewById(R.id.signUpButton);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               //card = cardInputWidget.getCard();
                if(cardInputWidget.getCard()!=null) {
                    createPaymentIntentwithSwitch(CSDataProvider.getLoginID(), "venu.j@voxvalley.com","100", "usd");//This should be a server call. Remove from app and call switch provided api
                    //createDirectPaymentIntentWithStripe("50", "usd", "sk_live_IogXqV1QUOx9qAU7c6VBK3bB");//This should be a server call. Remove from app and call switch provided api
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

                        paymentIntent.getId();

                        LOG.info("Payment Activity onActivityResult "+"status: "+status);

                        if (status == PaymentIntent.Status.Succeeded) {
                            // show success UI
                            LOG.info("Payment Activity onActivityResult "+"status: "+PaymentIntent.Status.Succeeded);
                            try {
                                Thread.sleep(40000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            verifystripepaymentWithSwitch(CSDataProvider.getLoginID(), paymentIntent.getId(),"venu.j@voxvalley.com","100");

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



    public void createPaymentIntentwithSwitch(String username,  String emailid,String amount,String currency) {

        showProgressBar();
        ApiClient.getClientWithLoggingInterceptor().create(ApiInterface.class).createPaymentIntentWithSwitch(username, emailid,amount,currency).enqueue(new Callback<CreatePaymentIntentWithSwitchRes>() {
            @Override
            public void onResponse(Call<CreatePaymentIntentWithSwitchRes> call, Response<CreatePaymentIntentWithSwitchRes> response) {
                LOG.info("CreatePaymentIntentWithSwitchRes onResponse:"+response.toString());
                if(response.isSuccessful()) {
                    LOG.info("CreatePaymentIntentWithSwitchRes onResponse success");
                    clientsecrete = response.body().getData().getMessage().getClientSecret();
                    //paymentid = response.body().getData().getMessage().getId();
                    LOG.info("clientsecrete: "+clientsecrete);


                    PaymentMethod.BillingDetails billingDetails = new PaymentMethod.BillingDetails.Builder().setAddress(new Address.Builder().setCity("Hyderabad").setCountry("IN").setLine1("Line1").setLine2("Line2").setPostalCode("500018").setState("Telegana").build()).setEmail("venu.j@voxvalley.com").setName("srr").setPhone("+919848012345").build();
                    confirmPayment(createConfirmPaymentIntentParams(clientsecrete,billingDetails));
                    cancelProgressBar();
                } else {
                    cancelProgressBar();
                    LOG.info("CreatePaymentIntentWithSwitchRes onResponse failure");
                    Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_LONG).show();
                }
                cancelProgressBar();
            }

            @Override
            public void onFailure(Call<CreatePaymentIntentWithSwitchRes> call, Throwable t) {
                LOG.info("Unable to submit post to API."+t.getStackTrace());
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                cancelProgressBar();
            }

        });



    }



    public void verifystripepaymentWithSwitch(String username, String paymentid, String emailid,String amount) {

        showProgressBar();
        ApiClient.getClientWithLoggingInterceptor().create(ApiInterface.class).verifystripepaymentWithSwitch(username, paymentid,emailid,amount).enqueue(new Callback<VerifystripepaymentWithSwitchRes>() {
            @Override
            public void onResponse(Call<VerifystripepaymentWithSwitchRes> call, Response<VerifystripepaymentWithSwitchRes> response) {
                LOG.info("verifystripepaymentWithSwitchRes onResponse:"+response.toString());
                if(response.isSuccessful()) {
                    LOG.info("verifystripepaymentWithSwitchRes onResponse success");
                    //clientsecrete = response.body().getData().getMessage().getClientSecret();
                    Toast.makeText(getApplicationContext(), response.body().getData().getStatus(), Toast.LENGTH_LONG).show();

                       cancelProgressBar();
                } else {
                    cancelProgressBar();
                    LOG.info("verifystripepaymentWithSwitchRes onResponse failure");
                    Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_LONG).show();
                }
                cancelProgressBar();
            }

            @Override
            public void onFailure(Call<VerifystripepaymentWithSwitchRes> call, Throwable t) {
                LOG.info("Unable to submit post to API."+t.getStackTrace());
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                cancelProgressBar();
            }

        });



    }

    public void showProgressBar() {
        try {
            mdialog = new ProgressDialog(PaymentActivitySandBox.this);
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