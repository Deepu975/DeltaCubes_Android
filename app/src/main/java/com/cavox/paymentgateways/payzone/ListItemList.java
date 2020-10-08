package com.cavox.paymentgateways.payzone;

import java.util.ArrayList;

public class ListItemList
{
    private ArrayList m_listItemList;
				
    public int getCount()
    {
        return (this.m_listItemList.size());
    }
		
    public ListItem getAt(int index) throws Exception
    {  
        if (index < 0 || index >= m_listItemList.size())
        {
            throw  new Exception("Array index out of bounds");
        }
        return ((ListItem)m_listItemList.get(index));
    }
		
    public void add(String name, String value, boolean isSelected)
    {
	m_listItemList.add(new ListItem(name, value, isSelected));
    }

    public String toString()
    {
        int count;
        StringBuffer returnString;
        ListItem listItem;
			
        returnString = new StringBuffer();

        for (count = 0; count < this.m_listItemList.size(); count++)
        {
            listItem = (ListItem)this.m_listItemList.get(count);
				
            returnString.append("<option");

            if (listItem.getValue() != null &&
                !listItem.getValue().equals(""))
            {
                returnString.append(" value=\"");
                returnString.append(listItem.getValue());
                returnString.append("\"");
            }

            if (listItem.getIsSelected() == true)
            {
                returnString.append(" selected=\"selected\"");
            }

            returnString.append(">");
            returnString.append(listItem.getName());
            returnString.append("</option>\n");
        }

        return (returnString.toString());
    }

    public ListItemList()
    {
        this.m_listItemList = new ArrayList();
    }
}