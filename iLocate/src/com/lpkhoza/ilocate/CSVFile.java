package com.lpkhoza.ilocate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.widget.Toast;

public class CSVFile {
	 InputStream inputStream;

	    public CSVFile(InputStream inputStream){
	        this.inputStream = inputStream;
	    }

	    public  ArrayList<Point> read()
	    {
	    	 ArrayList<Point> resultList = new ArrayList<Point>();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	        try {
	            String csvLine;
	            while ((csvLine = reader.readLine()) != null) {
	                String[] line = csvLine.split(",");	                
	                Point pt= new Point(Double.parseDouble( line[0]), Double.parseDouble(line[1]), line[2]);	
	                resultList.add(pt);
	            }	            
	        }
	        catch (IOException ex) {
	            throw new RuntimeException("Error in reading CSV file: "+ex);
	        }
	        finally {
	            try {
	                inputStream.close();
	            }
	            catch (IOException e) {
	             
	                throw new RuntimeException("Error while closing input stream: "+e);
	            }
	        }
	        return resultList;
	    }
	    
	    
 
}
