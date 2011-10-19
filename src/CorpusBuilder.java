/**
 * @author LEE HONG RU
 *
 */

import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;


public class CorpusBuilder {

	/**
	 * Build Corpus :
	 * 1) Read each file from CorpusAnnotated
	 * 2) At each line, use String[] temp = String.split(",") to get the array of Strings
	 * 3) Only retrieve, temp[2],temp[4],temp[5],temp[6]
	 * 4) Store them into a String in this format : "temp[2]:temp[4] temp[5]:temp[6]"
	 * 5) add the String to a ArrayList
	 * 6) If any of them happen to be empty, use the value -1
	 * 
	 * Explanation
	 * - temp[2] refers to the explicit feature which we will use to compare the nouns
	 * - temp[4] and temp[5] refers to the opinion which we will use to compare the words of type JJ
	 * - temp[6] is the sentiment of the feature which we will use for classification purposes
	 */
	
	ArrayList<String> corpusList = new ArrayList<String>();
	
	public void init(String filePath) {
		FileInputStream fs = null;
		String file;
		try {
			
			File folder = new File(filePath);  /* Folder path */
			File[] listOfFiles = folder.listFiles();  /* List of files in folder */
			System.out.println("Number of files in folder : " +  listOfFiles.length);
			for (int i = 0; i < listOfFiles.length; i++)  {  
				if (listOfFiles[i].isFile()) /* Is a file */
				   {
						file = listOfFiles[i].toString(); /* Get the path */
						System.out.println(file);
						
						fs = new FileInputStream(file); /* File IO Input Stream */
						System.out.println("Start reading file " + listOfFiles[i].getName());
						contentReading(fs); /* Read file */
				    }
			}
			System.out.println();
			System.out.println("Contents of corpusList: " + corpusList); 
		} 
		 catch (IOException e) {
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void contentReading(InputStream fileInputStream) {
		/* Read File implementation */
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
			String strLine;
			boolean IsFirstRow = true;
			while ((strLine = br.readLine()) != null)   {
				
				//System.out.println (strLine);
				
				if(IsFirstRow){ /* TO IGNORE HEADER ROW */
					
					IsFirstRow = false; /* Next Row Not Header Row */
				}
				else{
					
					String[] temp = strLine.split(",");
					
					for (int i = 0; i < temp.length; i++){
						if (temp[i].trim().length() == 0){
							temp[i] = "-1";
						}
					}
					
					/* System.out.println ("Explicit feature : " + temp[2]);
					System.out.println ("Opinion : " + temp[4] + temp[5]);
					System.out.println ("Sentiment : " + temp[6]); */
					
					String corpusStr = temp[2].trim() + ":" + temp[4].trim() + ":" + temp[5].trim() + ":" + temp[6].trim();
					//System.out.println (corpusStr);
					corpusList.add(corpusStr);
				}
				
			}
			IsFirstRow = true;
			fileInputStream.close(); /* Close file input stream */	
		}
		
		catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CorpusBuilder corpus = new CorpusBuilder();
//		corpus.init("C:\\Users\\dylan\\workspace\\csc421\\CorpusAnnotated"); //absolute path, varies from system to system
		corpus.init("CorpusAnnotated"); //relative path, it will look for CorpusAnnotated folder within the project folder (csc421) wherever the project folder is :)
	}

}
