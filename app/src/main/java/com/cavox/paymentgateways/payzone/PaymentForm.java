package com.cavox.paymentgateways.payzone;

import javax.servlet.http.HttpServletRequest;

public class PaymentForm 
{
    public class FORM_MODE
    {
        public static final int PAYMENT_FORM = 0;
        public static final int RESULTS = 1;
        public static final int THREE_D_SECURE = 2;
    }
    
    private String m_cardName = "John Watson";
    private String m_cardNumber = "4976000000003436";
    private String m_expiryDateMonth = "12";
    private String m_expiryDateYear = "20";
    private String m_startDateMonth = "01";
    private String m_startDateYear = "12";
    private String m_issueNumber = "";
    private String m_CV2 = "452";
    private String m_address1 = "32 Mulberry Street";
    private String m_address2 = "Eastfort";
    private String m_address3 = "Violetdell";
    private String m_address4 = "VL14 8PA";
    private String m_city = "";
    private String m_state = "";
    private String m_postCode = "";
    private int m_countryISOCode = -1;
    private String m_countryShort = "";
    private String m_userAgent = "";
    private String m_customerIPAddress = "";
    private int m_formMode = 0;
    private String m_amount = "1";
    private String m_currencyShort = "";
    private int m_currencyISOCode = 826;
    private String m_orderID = "vjvjvjhvjh";
    private String m_orderDescription = "jnkjb";
    private String m_crossReference = "";
    private String m_paRES = "";
    private String m_paREQ = "";
    private String m_message = "";
    private boolean m_transactionSuccessful;
    private boolean m_duplicateTransaction;
    private String m_previousTransactionMessage = "";
    private boolean m_suppressFormDisplay = false;
    private String m_formAction = "";
    private String m_formAttributes = "";
    private String m_bodyAttributes = "";
        
    /** Creates a new instance of PaymentForm */
    public PaymentForm() 
    {
    }

    public void processPayment(Global global) throws Exception
    {
        net.thepaymentgateway.paymentsystem.RequestGatewayEntryPointList requestGatewayEntryPointList;
        net.thepaymentgateway.paymentsystem.CardDetailsTransaction cardDetailsTransaction;
        net.thepaymentgateway.paymentsystem.OutCardDetailsTransactionResult cardDetailsTransactionResult;
        net.thepaymentgateway.paymentsystem.OutTransactionOutputData transactionOutputData;
        boolean transactionProcessed;
        int loopIndex;

        requestGatewayEntryPointList = new net.thepaymentgateway.paymentsystem.RequestGatewayEntryPointList();
        // you need to put the correct gateway entry point urls in here
        // contact support to get the correct urls

        // The actual values to use for the entry points can be established in a number of ways
        // 1) By periodically issuing a call to GetGatewayEntryPoints
        // 2) By storing the values for the entry points returned with each transaction
        // 3) Speculatively firing transactions at https://gw1.xxx followed by gw2, gw3, gw4....
        // The lower the metric (2nd parameter) means that entry point will be attempted first,
        // EXCEPT if it is -1 - in this case that entry point will be skipped
        // NOTE: You do NOT have to add the entry points in any particular order - the list is sorted
        // by metric value before the transaction sumbitting process begins
        // The 3rd parameter is a retry attempt, so it is possible to try that entry point that number of times
        // before failing over onto the next entry point in the list

        requestGatewayEntryPointList.add("https://mms.takepaymentsonline2.com/Pages/PublicPages/PaymentForm.aspx", 100, 1);
        requestGatewayEntryPointList.add("https://mms.takepaymentsonline.com/Pages/PublicPages/PaymentForm.aspx", 100, 1);


        requestGatewayEntryPointList.add("https://gw1." + global.getPaymentProcessorFullDomain(), 100, 1);
        //requestGatewayEntryPointList.add("https://gw2." + global.getPaymentProcessorFullDomain(), 200, 1);
        //requestGatewayEntryPointList.add("https://gw3." + global.getPaymentProcessorFullDomain(), 300, 1);

        cardDetailsTransaction = new net.thepaymentgateway.paymentsystem.CardDetailsTransaction(requestGatewayEntryPointList);
        
        cardDetailsTransaction.getMerchantAuthentication().setMerchantID(global.getMerchantID());
        cardDetailsTransaction.getMerchantAuthentication().setPassword(global.getPassword());
        
        cardDetailsTransaction.getTransactionDetails().getMessageDetails().setTransactionType("SALE");

        cardDetailsTransaction.getTransactionDetails().getAmount().setValue(Integer.parseInt(m_amount));
        cardDetailsTransaction.getTransactionDetails().getCurrencyCode().setValue(m_currencyISOCode);
        cardDetailsTransaction.getTransactionDetails().setOrderID(m_orderID);
        cardDetailsTransaction.getTransactionDetails().setOrderDescription(m_orderDescription);
        
        cardDetailsTransaction.getTransactionDetails().getTransactionControl().getEchoCardType().setValue(true);
        cardDetailsTransaction.getTransactionDetails().getTransactionControl().getEchoCV2CheckResult().setValue(true);
        cardDetailsTransaction.getTransactionDetails().getTransactionControl().getEchoAVSCheckResult().setValue(true);
        cardDetailsTransaction.getTransactionDetails().getTransactionControl().getEchoAmountReceived().setValue(true);
        cardDetailsTransaction.getTransactionDetails().getTransactionControl().getThreeDSecureOverridePolicy().setValue(true);
        cardDetailsTransaction.getTransactionDetails().getTransactionControl().getDuplicateDelay().setValue(60);

        cardDetailsTransaction.getTransactionDetails().getThreeDSecureBrowserDetails().getDeviceCategory().setValue(0);
        cardDetailsTransaction.getTransactionDetails().getThreeDSecureBrowserDetails().setAcceptHeaders("*/*");
        cardDetailsTransaction.getTransactionDetails().getThreeDSecureBrowserDetails().setUserAgent(m_userAgent);

        if (!m_expiryDateMonth.equals(""))
        {
            cardDetailsTransaction.getCardDetails().getExpiryDate().getMonth().setValue(Integer.parseInt(m_expiryDateMonth));
        }
        if (!m_expiryDateYear.equals(""))
        {
            cardDetailsTransaction.getCardDetails().getExpiryDate().getYear().setValue(Integer.parseInt(m_expiryDateYear));
        }
        if (!m_startDateMonth.equals(""))
        {
            cardDetailsTransaction.getCardDetails().getStartDate().getMonth().setValue(Integer.parseInt(m_startDateMonth));
        }
        if (!m_startDateYear.equals(""))
        {
            cardDetailsTransaction.getCardDetails().getStartDate().getYear().setValue(Integer.parseInt(m_startDateYear));
        }
        cardDetailsTransaction.getCardDetails().setCardName(m_cardName);
        cardDetailsTransaction.getCardDetails().setCardNumber(m_cardNumber);
        cardDetailsTransaction.getCardDetails().setIssueNumber(m_issueNumber);
        cardDetailsTransaction.getCardDetails().setCV2(m_CV2);

        cardDetailsTransaction.getCustomerDetails().setEmailAddress("test@test.com");
        cardDetailsTransaction.getCustomerDetails().setPhoneNumber("123456789");
        cardDetailsTransaction.getCustomerDetails().setCustomerIPAddress(m_customerIPAddress);
        
        cardDetailsTransaction.getCustomerDetails().getBillingAddress().setAddress1(m_address1);
        cardDetailsTransaction.getCustomerDetails().getBillingAddress().setAddress2(m_address2);
        cardDetailsTransaction.getCustomerDetails().getBillingAddress().setAddress3(m_address3);
        cardDetailsTransaction.getCustomerDetails().getBillingAddress().setAddress4(m_address4);
        cardDetailsTransaction.getCustomerDetails().getBillingAddress().setCity(m_city);
        cardDetailsTransaction.getCustomerDetails().getBillingAddress().setState(m_state);
        cardDetailsTransaction.getCustomerDetails().getBillingAddress().setPostCode(m_postCode);
        
        if (m_countryISOCode != -1)
        {
            cardDetailsTransaction.getCustomerDetails().getBillingAddress().getCountryCode().setValue(m_countryISOCode);
        }

        cardDetailsTransactionResult = new net.thepaymentgateway.paymentsystem.OutCardDetailsTransactionResult();
        transactionOutputData = new net.thepaymentgateway.paymentsystem.OutTransactionOutputData();
        transactionProcessed = cardDetailsTransaction.processTransaction(cardDetailsTransactionResult, transactionOutputData);

        if (!transactionProcessed)
        {
            // could not communicate with the payment gateway 
            m_message = "Couldn't communicate with payment gateway 1";
            m_transactionSuccessful = false;
            
            PaymentFormHelper.reportTransactionResults(m_orderID, 30, m_message, null);
        }
        else
        {
            switch (cardDetailsTransactionResult.output.getStatusCode())
            {
                case 0:
                    // status code of 0 - means transaction successful 
                    m_formMode = FORM_MODE.RESULTS;
                    m_message = cardDetailsTransactionResult.output.getMessage();
                    m_transactionSuccessful = true;
                    PaymentFormHelper.reportTransactionResults(m_orderID, cardDetailsTransactionResult.output.getStatusCode(), cardDetailsTransactionResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
                case 3:
                    // status code of 3 - means 3D Secure authentication required 
                    m_formMode = FORM_MODE.THREE_D_SECURE;
                    m_paREQ = transactionOutputData.output.getThreeDSecureOutputData().getPaREQ();
                    m_crossReference = transactionOutputData.output.getCrossReference();
                    m_bodyAttributes = " onload=\"document.Form.submit()\"";
                    m_formAttributes = " target=\"ACSFrame\"";
                    m_formAction = transactionOutputData.output.getThreeDSecureOutputData().getACSURL();
                    PaymentFormHelper.reportTransactionResults(m_orderID, cardDetailsTransactionResult.output.getStatusCode(), cardDetailsTransactionResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
                case 5:
                    // status code of 5 - means transaction declined 
                    m_formMode = FORM_MODE.RESULTS;
                    m_message = cardDetailsTransactionResult.output.getMessage();
                    m_transactionSuccessful = false;
                    PaymentFormHelper.reportTransactionResults(m_orderID, cardDetailsTransactionResult.output.getStatusCode(), cardDetailsTransactionResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
                case 20:
                    // status code of 20 - means duplicate transaction 
                    m_formMode = FORM_MODE.RESULTS;
                    m_message = cardDetailsTransactionResult.output.getMessage();
                    if (cardDetailsTransactionResult.output.getPreviousTransactionResult().getStatusCode().getValue() == 0)
                    {
                        m_transactionSuccessful = true;
                    }
                    else
                    {
                        m_transactionSuccessful = false;
                    }
                    m_previousTransactionMessage = cardDetailsTransactionResult.output.getPreviousTransactionResult().getMessage();
                    m_duplicateTransaction = true;
                    PaymentFormHelper.reportTransactionResults(m_orderID, cardDetailsTransactionResult.output.getPreviousTransactionResult().getStatusCode().getValue(), m_previousTransactionMessage, transactionOutputData.output.getCrossReference());
                    break;
                case 30:
                    // status code of 30 - means an error occurred 
                    m_formMode = FORM_MODE.PAYMENT_FORM;
                    m_message = cardDetailsTransactionResult.output.getMessage();
                    if (cardDetailsTransactionResult.output.getErrorMessages().getCount() > 0)
                    {
                        m_message = m_message + "<br /><ul>";

                        for (loopIndex = 0; loopIndex < cardDetailsTransactionResult.output.getErrorMessages().getCount(); loopIndex++)
                        {
                            m_message = m_message + "<li>" + cardDetailsTransactionResult.output.getErrorMessages().getAt(loopIndex) + "</li>";
                        }
                        m_message = m_message + "</ul>";
                        m_transactionSuccessful = false;
                    }
                    PaymentFormHelper.reportTransactionResults(m_orderID, cardDetailsTransactionResult.output.getStatusCode(), cardDetailsTransactionResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
                default:
                    // unhandled status code  
                    m_formMode = FORM_MODE.PAYMENT_FORM;
                    m_message = cardDetailsTransactionResult.output.getMessage();
                    m_transactionSuccessful = false;
                    PaymentFormHelper.reportTransactionResults(m_orderID, cardDetailsTransactionResult.output.getStatusCode(), cardDetailsTransactionResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
            }
        }    
    }
 
    private void threeDSecureAuthentication(Global global) throws Exception
    {
        net.thepaymentgateway.paymentsystem.RequestGatewayEntryPointList requestGatewayEntryPointList;
        net.thepaymentgateway.paymentsystem.ThreeDSecureAuthentication threeDSecureAuthentication;
        net.thepaymentgateway.paymentsystem.OutThreeDSecureAuthenticationResult threeDSecureAuthenticationResult;
        net.thepaymentgateway.paymentsystem.OutTransactionOutputData transactionOutputData;
        boolean transactionProcessed;
        int loopIndex;

        requestGatewayEntryPointList = new net.thepaymentgateway.paymentsystem.RequestGatewayEntryPointList();
        // you need to put the correct gateway entry point urls in here
        // contact support to get the correct urls

        // The actual values to use for the entry points can be established in a number of ways
        // 1) By periodically issuing a call to GetGatewayEntryPoints
        // 2) By storing the values for the entry points returned with each transaction
        // 3) Speculatively firing transactions at https://gw1.xxx followed by gw2, gw3, gw4....
        // The lower the metric (2nd parameter) means that entry point will be attempted first,
        // EXCEPT if it is -1 - in this case that entry point will be skipped
        // NOTE: You do NOT have to add the entry points in any particular order - the list is sorted
        // by metric value before the transaction sumbitting process begins
        // The 3rd parameter is a retry attempt, so it is possible to try that entry point that number of times
        // before failing over onto the next entry point in the list
        requestGatewayEntryPointList.add("https://gw1." + global.getPaymentProcessorFullDomain(), 100, 1);
        requestGatewayEntryPointList.add("https://gw2." + global.getPaymentProcessorFullDomain(), 200, 1);
        requestGatewayEntryPointList.add("https://gw3." + global.getPaymentProcessorFullDomain(), 300, 1);

	threeDSecureAuthentication = new net.thepaymentgateway.paymentsystem.ThreeDSecureAuthentication(requestGatewayEntryPointList);
        
        threeDSecureAuthentication.getMerchantAuthentication().setMerchantID(global.getMerchantID());
        threeDSecureAuthentication.getMerchantAuthentication().setPassword(global.getPassword());

        threeDSecureAuthentication.getThreeDSecureInputData().setCrossReference(m_crossReference);
        threeDSecureAuthentication.getThreeDSecureInputData().setPaRES(m_paRES);
        
        threeDSecureAuthenticationResult = new net.thepaymentgateway.paymentsystem.OutThreeDSecureAuthenticationResult();
        transactionOutputData = new net.thepaymentgateway.paymentsystem.OutTransactionOutputData();
	transactionProcessed = threeDSecureAuthentication.processTransaction(threeDSecureAuthenticationResult, transactionOutputData);

        if (!transactionProcessed)
        {
            // could not communicate with the payment gateway 
            m_message = "Couldn't communicate with payment gateway 2";
            m_transactionSuccessful = false;
            
            PaymentFormHelper.reportTransactionResults(m_orderID, 30, m_message, null);
        }
        else
        {
            switch (threeDSecureAuthenticationResult.output.getStatusCode())
            {
                case 0:
                    // status code of 0 - means transaction successful 
                    m_formMode = FORM_MODE.RESULTS;
                    m_message = threeDSecureAuthenticationResult.output.getMessage();
                    m_transactionSuccessful = true;
                    PaymentFormHelper.reportTransactionResults(m_orderID, threeDSecureAuthenticationResult.output.getStatusCode(), threeDSecureAuthenticationResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
                case 5:
                    // status code of 5 - means transaction declined 
                    m_formMode = FORM_MODE.RESULTS;
                    m_message = threeDSecureAuthenticationResult.output.getMessage();
                    m_transactionSuccessful = false;
                    PaymentFormHelper.reportTransactionResults(m_orderID, threeDSecureAuthenticationResult.output.getStatusCode(), threeDSecureAuthenticationResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
                case 20:
                    // status code of 20 - means duplicate transaction 
                    m_formMode = FORM_MODE.RESULTS;
                    m_message = threeDSecureAuthenticationResult.output.getMessage();
                    if (threeDSecureAuthenticationResult.output.getPreviousTransactionResult().getStatusCode().getValue() == 0)
                    {
                        m_transactionSuccessful = true;
                    }
                    else
                    {
                        m_transactionSuccessful = false;
                    }
                    m_previousTransactionMessage = threeDSecureAuthenticationResult.output.getPreviousTransactionResult().getMessage();
                    m_duplicateTransaction = true;
                    PaymentFormHelper.reportTransactionResults(m_orderID, threeDSecureAuthenticationResult.output.getPreviousTransactionResult().getStatusCode().getValue(), m_previousTransactionMessage, transactionOutputData.output.getCrossReference());
                    break;
                case 30:
                    // status code of 30 - means an error occurred 
                    m_formMode = FORM_MODE.RESULTS;
                    m_message = threeDSecureAuthenticationResult.output.getMessage();
                    if (threeDSecureAuthenticationResult.output.getErrorMessages().getCount() > 0)
                    {
                        m_message = m_message + "<br /><ul>";

                        for (loopIndex = 0; loopIndex < threeDSecureAuthenticationResult.output.getErrorMessages().getCount(); loopIndex++)
                        {
                            m_message = m_message + "<li>" + threeDSecureAuthenticationResult.output.getErrorMessages().getAt(loopIndex) + "</li>";
                        }
                        m_message = m_message + "</ul>";
                        m_transactionSuccessful = false;
                    }
                    PaymentFormHelper.reportTransactionResults(m_orderID, threeDSecureAuthenticationResult.output.getStatusCode(), threeDSecureAuthenticationResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
                default:
                    // unhandled status code  
                    m_formMode = FORM_MODE.RESULTS;
                    m_message = threeDSecureAuthenticationResult.output.getMessage();
                    m_transactionSuccessful = false;
                    PaymentFormHelper.reportTransactionResults(m_orderID, threeDSecureAuthenticationResult.output.getStatusCode(), threeDSecureAuthenticationResult.output.getMessage(), transactionOutputData.output.getCrossReference());
                    break;
            }
        }    
    }
    
    private void setInternalValuesFromFormFields(HttpServletRequest request, Global global)
    {
        String hashDigest = "";
        String incomingHashDigest = "";
        net.thepaymentgateway.common.OutISOCurrency outISOCurrency;
        net.thepaymentgateway.common.OutISOCountry outISOCountry;
        
        if (request.getParameter("FormMode") == null)
        {
            incomingHashDigest = request.getParameter("ShoppingCartHashDigest");
            m_amount = request.getParameter("ShoppingCartAmount");
            m_currencyShort = request.getParameter("ShoppingCartCurrencyShort");
            m_orderID = request.getParameter("ShoppingCartOrderID");
            m_orderDescription = request.getParameter("ShoppingCartOrderDescription");
            
            hashDigest = PaymentFormHelper.calculateHashDigest(PaymentFormHelper.generateStringToHash(m_amount,
                                m_currencyShort,
                                m_orderID,
                                m_orderDescription,
                                global.getSecretKey()));
        }
        else
        {
            m_formMode = getFormMode(request.getParameter("FormMode"));
            
            switch (m_formMode)
            {
                case FORM_MODE.PAYMENT_FORM:
                    incomingHashDigest = request.getParameter("HashDigest");
                    m_cardName = request.getParameter("CardName");
                    m_cardNumber = request.getParameter("CardNumber");
                    m_expiryDateMonth = request.getParameter("ExpiryDateMonth");
                    m_expiryDateYear = request.getParameter("ExpiryDateYear");
                    m_startDateMonth = request.getParameter("StartDateMonth");
                    m_startDateYear = request.getParameter("StartDateYear");
                    m_issueNumber = request.getParameter("IssueNumber");
                    m_CV2 = request.getParameter("CV2");
                    m_address1 = request.getParameter("Address1");
                    m_address2 = request.getParameter("Address2");
                    m_address3 = request.getParameter("Address3");
                    m_address4 = request.getParameter("Address4");
                    m_city = request.getParameter("City");
                    m_state = request.getParameter("State");
                    m_postCode = request.getParameter("PostCode");
                    m_countryShort = request.getParameter("CountryShort");
                    m_amount = request.getParameter("Amount");
                    m_currencyShort = request.getParameter("CurrencyShort");
                    m_orderID = request.getParameter("OrderID");
                    m_orderDescription = request.getParameter("OrderDescription");
                    m_userAgent = request.getHeader("User-Agent");
                    //m_customerIPAddress = request.getRemoteAddr();
                    hashDigest = PaymentFormHelper.calculateHashDigest(PaymentFormHelper.generateStringToHash(m_amount,
                                m_currencyShort,
                                m_orderID,
                                m_orderDescription,
                                global.getSecretKey()));
                    break;
                case FORM_MODE.THREE_D_SECURE:
                    incomingHashDigest = request.getParameter("ShoppingCartHashDigest");
                    m_crossReference = request.getParameter("CrossReference");
                    m_paRES = request.getParameter("PaRES");
                    hashDigest = PaymentFormHelper.calculateHashDigest(PaymentFormHelper.generateStringToHash(m_paRES,
                                m_crossReference,
                                global.getSecretKey()));
                    break;
                case FORM_MODE.RESULTS:
                    break;
            }
        }
        
        // check the hash digest
        if (!hashDigest.equalsIgnoreCase(incomingHashDigest))
        {
            m_message = "Variable tampering detected";
            m_suppressFormDisplay = true;
            m_formMode = FORM_MODE.RESULTS;
        }
        
        try
        {
            outISOCurrency = new net.thepaymentgateway.common.OutISOCurrency();
            
            if (global.getISOCurrencyData().getISOCurrency(m_currencyShort, outISOCurrency))
            {
                m_currencyISOCode = outISOCurrency.output.getISOCode();
            }

            outISOCountry = new net.thepaymentgateway.common.OutISOCountry();
            
            if (global.getISOCountryData().getISOCountry(m_countryShort, outISOCountry))
            {
                m_countryISOCode = outISOCountry.output.getISOCode();
            }
        }
        catch (Exception e)
        {
        }
        
    }
    
    public void preProcessPaymentForm(HttpServletRequest request, Global global)
    {
        boolean boResetFormVariables = false;
        String secretKey;
        
        setInternalValuesFromFormFields(request, global);
        
        // Is this a postback?
        if (!m_suppressFormDisplay)
        {
            if (m_formMode == -1)
            {
                boResetFormVariables = true;
                m_formMode = FORM_MODE.PAYMENT_FORM;
            }
            else
            {
                // do we try to process the payment? 
                switch (m_formMode)
                {
                    case FORM_MODE.PAYMENT_FORM:
                        // have just come from a payment form - try to process the payment
                        try
                        {
                            processPayment(global);
                        }
                        catch (Exception e)
                        {
                    
                        }
                        break;
                    case FORM_MODE.RESULTS:
                        boResetFormVariables = true;
                        m_formMode = FORM_MODE.PAYMENT_FORM;
                        break;
                    case FORM_MODE.THREE_D_SECURE:
                        // have just come from a payment form - try to process the payment
                        try
                        {
                            threeDSecureAuthentication(global);
                        }
                        catch (Exception e)
                        {
                    
                        }
                        break;
                }
            }

            // Reset the form variables if required 
            if (boResetFormVariables == true)
            {
                m_cardName = "";
                m_cardNumber = "";
                m_expiryDateMonth = "";
                m_expiryDateYear = "";
                m_startDateMonth = "";
                m_startDateYear = "";
                m_issueNumber = "";
                m_CV2 = "";
                m_address1 = "";
                m_address2 = "";
                m_address3 = "";
                m_address4 = "";
                m_city = "";
                m_state = "";
                m_postCode = "";
                m_countryShort = "";
            }  
        }
    }
    
    public String getFormMode(int formMode)
    {
        String returnMode = "";
        
        switch (formMode)
        {
            case FORM_MODE.PAYMENT_FORM:
                returnMode = "PAYMENT_FORM";
                break;
            case FORM_MODE.RESULTS:
                returnMode = "RESULTS";
                break;
            case FORM_MODE.THREE_D_SECURE:
                returnMode = "THREE_D_SECURE";
                break;
        }

        return (returnMode);
    }
    public int getFormMode(String formMode)
    {
        int returnMode = -1;
        
        if (formMode != null &&
            !formMode.equals(""))
        {
            if (formMode.equalsIgnoreCase("PAYMENT_FORM"))
            {
                returnMode = FORM_MODE.PAYMENT_FORM;
            }
            if (formMode.equalsIgnoreCase("RESULTS"))
            {
                returnMode = FORM_MODE.RESULTS;
            }
            if (formMode.equalsIgnoreCase("THREE_D_SECURE"))
            {
                returnMode = FORM_MODE.THREE_D_SECURE;
            }
        }
        return (returnMode);
    }
    
    public String getCardName() { return m_cardName; }
    public String getCardNumber() { return m_cardNumber; }
    public String getExpiryDateMonth() {  return m_expiryDateMonth; }
    public String getExpiryDateYear() { return m_expiryDateYear; }
    public String getStartDateMonth() { return m_startDateMonth; }
    public String getStartDateYear() { return m_startDateYear; }
    public String getIssueNumber() { return m_issueNumber; }
    public String getCV2() { return m_CV2; }
    public String getAddress1() { return m_address1; }
    public String getAddress2() { return m_address2; }
    public String getAddress3() { return m_address3; }
    public String getAddress4() { return m_address4; }
    public String getCity() { return m_city; }
    public String getState() { return m_state; }
    public String getPostCode() { return m_postCode; }
    public String getCountryShort()  { return m_countryShort; }
    public String getFormModeString() { return getFormMode(m_formMode); }
    public int getFormModeEnum() { return m_formMode; }
    public String getAmount() { return m_amount; }
    public String getCurrencyShort() { return m_currencyShort; }
    public String getOrderID() { return m_orderID; }
    public String getOrderDescription() { return m_orderDescription; }
    public String getCrossReference() { return m_crossReference; }
    public String getPaRES() { return m_paRES; }
    public String getPaREQ() { return m_paREQ; }
    public String getFormAction() { return m_formAction; }
    public String getFormAttributes() { return m_formAttributes; }
    public String getBodyAttributes() { return m_bodyAttributes; }
    public String getMessage() { return m_message; }
    public boolean getDuplicateTransaction() { return m_duplicateTransaction; }
    public boolean getTransactionSuccessful() { return m_transactionSuccessful; }
    public String getPreviousTransactionMessage() { return m_previousTransactionMessage; }
    public boolean getSuppressFormDisplay() { return m_suppressFormDisplay; }
}
