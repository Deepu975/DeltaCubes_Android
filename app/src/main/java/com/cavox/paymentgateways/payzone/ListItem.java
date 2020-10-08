package com.cavox.paymentgateways.payzone;

public class ListItem
{
    private String m_name;
    private String m_value;
    private boolean m_isSelected;
	    
    public String getName()
    {
        return this.m_name;
    }
	    
    public String getValue()
    {
        return this.m_value;
    }
	   
    public boolean getIsSelected()
    {
        return this.m_isSelected;
    }
	   	    
    public ListItem(String name, String value, boolean isSelected)
    {
        this.m_name = name;
        this.m_value = value;
        this.m_isSelected = isSelected;
    }
}