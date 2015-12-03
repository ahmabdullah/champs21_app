package com.classtune.spellingbee;

import android.util.Log;

import com.classtune.schoolapp.model.SpellingbeeDataModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class SAXXMLHandler extends DefaultHandler {

 
    private List<SpellingbeeDataModel> data;
    private String tempVal;
    private SpellingbeeDataModel tempEmp;
 
    public SAXXMLHandler() {
        data = new ArrayList<SpellingbeeDataModel>();
    }
 
    public List<SpellingbeeDataModel> getData() {
        return data;
    }
 
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("data")) {
            // create a new instance of employee
            tempEmp = new SpellingbeeDataModel();
            
            int id = Integer.parseInt(attributes.getValue("id"));
            tempEmp.setId(id);
        	
        }
        
    }
 
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }
 
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    	
    	
		if (qName.equalsIgnoreCase("data"))
        {
	         // add it to the list
	        if(tempEmp.getId() != 0 && tempEmp.getWord() != null && tempEmp.getBanglaMeaning()!= null && tempEmp.getDefinition()!= null &&
                    tempEmp.getSentence()!= null && tempEmp.getwType() != null && tempEmp.getLevel()!= null)
	        	data.add(tempEmp);
	        
	        else
	        {
	        	Log.e("NULL_PREF", "data: "+tempEmp.getId());
	        	Log.e("NULL_PREF", "data: "+tempEmp.getWord());
                Log.e("NULL_PREF", "data: "+tempEmp.getWordTwo());
	        	Log.e("NULL_PREF", "data: "+tempEmp.getBanglaMeaning());
	        	Log.e("NULL_PREF", "data: "+tempEmp.getDefinition());
	        	Log.e("NULL_PREF", "data: "+tempEmp.getSentence());
                Log.e("NULL_PREF", "data: "+tempEmp.getwType());
                Log.e("NULL_PREF", "data: "+tempEmp.getLevel());
	        	
	        	Log.e("NULL_PREF", "_______________________________________");
	        }
	        
	       
	        	
	    } 
		
		else if(qName.equalsIgnoreCase("word"))
	    {
	     	tempEmp.setWord(tempVal);
	    }

        else if(qName.equalsIgnoreCase("word2"))
        {
            tempEmp.setWordTwo(tempVal);
        }

        else if(qName.equalsIgnoreCase("bangla_meaning"))
        {
        	tempEmp.setBanglaMeaning(tempVal);
        }
        else if(qName.equalsIgnoreCase("definition"))
        {
        	tempEmp.setDefinition(tempVal);
        }
        else if(qName.equalsIgnoreCase("sentence"))
        {
        	tempEmp.setSentence(tempVal);
        }
        else if(qName.equalsIgnoreCase("wtype"))
        {
            tempEmp.setwType(tempVal);
        }
        else if(qName.equalsIgnoreCase("level"))
        {
            tempEmp.setLevel(tempVal);
        }

    }
}