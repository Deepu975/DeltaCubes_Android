package com.cavox.paymentgateways.payzone;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.cavox.utils.GlobalVariables.LOG;

public class PaymentFormHelper
{
    public static String getSiteSecureBaseURL(HttpServletRequest request)
    {
        String szReturnString;
        StringBuffer sbString;
        int nIndex;
        
        sbString = request.getRequestURL();

        nIndex = sbString.lastIndexOf("/");

        szReturnString = sbString.substring(0, nIndex + 1);

        return (szReturnString);
    }
    
    public static ListItemList createExpiryDateMonthListItemList(String expiryDateMonth)
    {
        ListItemList expiryDateMonthList;
        int loopIndex;
        StringBuffer displayMonth; 
        
        expiryDateMonthList = new ListItemList();

        for (loopIndex = 1; loopIndex <= 12; loopIndex++)
        {
            displayMonth = new StringBuffer();
            
            if (loopIndex < 10)
            {
                displayMonth.append("0");
                displayMonth.append(loopIndex);
            }
            else
            {
                displayMonth.append(loopIndex);
            }
            if (expiryDateMonth != null &&
                !expiryDateMonth.equals("") &&
		expiryDateMonth.equals(displayMonth.toString()))
            {
                expiryDateMonthList.add(displayMonth.toString(), displayMonth.toString(), true);
            }
            else
            {
                expiryDateMonthList.add(displayMonth.toString(), displayMonth.toString(), false);
            }
        }

        return (expiryDateMonthList);
    }

    public static ListItemList createExpiryDateYearListItemList(String expiryDateYear)
    {
        ListItemList expiryDateYearList;
        int loopIndex;
        StringBuffer yearName;
        String yearValue;
        StringBuffer shortYear;
        int thisYear;
        int thisYearPlusTen;
        java.util.Calendar calendar;

        calendar = java.util.Calendar.getInstance();
        
        thisYear = calendar.get(java.util.Calendar.YEAR);
        thisYearPlusTen = thisYear + 10;
		
        expiryDateYearList = new ListItemList();

        for (loopIndex = thisYear; loopIndex <= thisYearPlusTen; loopIndex++)
        {
            yearName = new StringBuffer();
            
            yearName.append(loopIndex);
            yearValue = yearName.substring(yearName.length() - 2, yearName.length());
            
            if (expiryDateYear != null &&
                !expiryDateYear.equals("") &&
                expiryDateYear.equals(yearValue))
            {
                expiryDateYearList.add(yearName.toString(), yearValue, true);
            }
            else
            {
                expiryDateYearList.add(yearName.toString(), yearValue, false);
            }
        }

        return (expiryDateYearList);
    }
    
    public static ListItemList createStartDateMonthListItemList(String startDateMonth)
    {
        ListItemList startDateMonthList;
        int loopIndex;
        StringBuffer displayMonth; 
        
        startDateMonthList = new ListItemList();

        for (loopIndex = 1; loopIndex <= 12; loopIndex++)
        {
            displayMonth = new StringBuffer();
            
            if (loopIndex < 10)
            {
                displayMonth.append("0");
                displayMonth.append(loopIndex);
            }
            else
            {
                displayMonth.append(loopIndex);
            }
            if (startDateMonth != null &&
                !startDateMonth.equals("") &&
		startDateMonth.equals(displayMonth.toString()))
            {
                startDateMonthList.add(displayMonth.toString(), displayMonth.toString(), true);
            }
            else
            {
                startDateMonthList.add(displayMonth.toString(), displayMonth.toString(), false);
            }
        }

        return (startDateMonthList);
    }

    public static ListItemList createStartDateYearListItemList(String startDateYear)
    {
        ListItemList startDateYearList;
        int loopIndex;
        StringBuffer yearName;
        String yearValue;
        StringBuffer shortYear;
        int thisYear;
        java.util.Calendar calendar;

        calendar = java.util.Calendar.getInstance();
        
        thisYear = calendar.get(java.util.Calendar.YEAR);
		
        startDateYearList = new ListItemList();

        for (loopIndex = 2000; loopIndex <= thisYear; loopIndex++)
        {
            yearName = new StringBuffer();
            
            yearName.append(loopIndex);
            yearValue = yearName.substring(yearName.length() - 2, yearName.length());
            
            if (startDateYear != null &&
                !startDateYear.equals("") &&
                startDateYear.equals(yearValue))
            {
                startDateYearList.add(yearName.toString(), yearValue, true);
            }
            else
            {
                startDateYearList.add(yearName.toString(), yearValue, false);
            }
        }

        return (startDateYearList);
    }
   
    public static ListItemList createISOCountryListItemList(String countryShort, net.thepaymentgateway.common.ISOCountryList ISOCountryData)
    {
        ListItemList ISOCountryList;
        boolean firstZeroPriorityGroup;
        int loopIndex;
        
        ISOCountryList = new ListItemList();

        firstZeroPriorityGroup = true;
        
        for (loopIndex = 0; loopIndex < ISOCountryData.getCount(); loopIndex++)
        {
            try
            {
                if (ISOCountryData.getAt(loopIndex).getListPriority() == 0 &&
                    firstZeroPriorityGroup == true)
                {
                    ISOCountryList.add("--------------------", "-1", false);
                    firstZeroPriorityGroup = false;
                } 

                if (countryShort != null &&
                    !countryShort.equals("") &&
                    !countryShort.equals("-1") &&
                    countryShort.equals(ISOCountryData.getAt(loopIndex).getCountryShort()))
                {
                    ISOCountryList.add(ISOCountryData.getAt(loopIndex).getCountryName(), ISOCountryData.getAt(loopIndex).getCountryShort(), true);
                }
                else
                {
                    ISOCountryList.add(ISOCountryData.getAt(loopIndex).getCountryName(), ISOCountryData.getAt(loopIndex).getCountryShort(), false);
                }
            }
            catch (Exception e)
            {
            
            }
        }

        return (ISOCountryList);
    }
    
    public static String byteArrayToHexString(byte[] bytes)
    {
        StringBuffer returnString;
        int count;
        
        returnString = new StringBuffer();
        
        for (count = 0; count < bytes.length; count++) 
        {
            returnString.append(Integer.toString((bytes[count] & 0xff) + 0x100, 16).substring(1));
        }
        return returnString.toString();
    }

    public static String calculateHashDigest(String inputString)
    {
        byte[] bytes;
        byte[] hashDigest;
        MessageDigest messageDigest;
        String returnString;

        try
        {
            bytes = inputString.getBytes("UTF-8");

            messageDigest = MessageDigest.getInstance("MD5");
            hashDigest = messageDigest.digest(bytes);
            returnString = byteArrayToHexString(hashDigest);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException e)
        {
            returnString = e.getMessage();
        }

        return (returnString);
    }
    
    public static String generateStringToHash(String amount, 
                                              String currencyShort, 
                                              String orderID,
                                              String orderDescription,
                                              String secretKey)
    {
        StringBuffer returnString;

        returnString = new StringBuffer();

        returnString.append("Amount=");
        returnString.append(amount);
        returnString.append("&CurrencyShort=");
        returnString.append(currencyShort);
        returnString.append("&OrderID=");
        returnString.append(orderID);
        returnString.append("&OrderDescription=");
        returnString.append(orderDescription);
        returnString.append("&SecretKey=");
        returnString.append(secretKey);

        return (returnString.toString());
    }
    public static String generateStringToHash(String paRES,
                                              String crossReference,
                                              String secretKey)
    {
        StringBuffer returnString;

        returnString = new StringBuffer();

        returnString.append("PaRES=");
        returnString.append(paRES);
        returnString.append("&CrossReference=");
        returnString.append(crossReference);
        returnString.append("&SecretKey=");
        returnString.append(secretKey);

        return (returnString.toString());
    }

    // This is a "hook" function that is run when the results of the transaction are
    // known. This will be run is 2 places:
    // 1) After the initial CardDetailsTransaction
    // 2) After the ThreeDSecureAuthentication transaction (if 3DS was required)
    //
    // IMPORTANT: in case 1) the unique key is set to be the OrderID, and this will allow
    // your system to lookup the transaction (in your database) if need be, so you need to ensure
    // that your orders are reference-able by their order ids
    //
    // Also, in case 2) the order id field is not avaiable, so the cross reference of the
    // previous "3DS authentication required" transaction is used as the unique key for
    // the transactioon. In order to get this working, you must ensure that when a transaction
    // is set to "3DS auth required" (i.e. the status code is 3), that you update that transaction
    // so that it can be referenced by the cross refernce of the "3DS required" response
    public static void reportTransactionResults(String transactionUniqueKey, int statusCode, String message, String crossReference)
    {


        LOG.info("Payzone transactionUniqueKey: "+transactionUniqueKey);
        LOG.info("Payzone statusCode: "+statusCode);
        LOG.info("Payzone message: "+message);
        LOG.info("Payzone crossReference: "+crossReference);



        if (statusCode == 3)
        {
            // you must update the transaction with the crossReference field, so that when
            // this function is called after the 3DS authentication is done, the transaction
            // is referenceable by that cross reference - as the subsequent post-3DS call to this
            // function of the transaction will use that cross reference as the transaction's
            // TransactionUniqueKey
        }
    }
}
