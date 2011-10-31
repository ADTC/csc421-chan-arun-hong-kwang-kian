/**
 * @author LEE HONG RU
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CorpusBuilder {
	
	ArrayList<String> exfeat     = new ArrayList<String>();
	ArrayList<String> opword     = new ArrayList<String>();
	ArrayList<String> context    = new ArrayList<String>();
	ArrayList<String> fsentiment = new ArrayList<String>();
	
	public void init(String filePath) {
		FileInputStream fs = null;
		String file;
		try {
			File folder = new File(filePath);  /* Folder path */
			File[] listOfFiles = folder.listFiles();  /* List of files in folder */
			for (int i = 0; i < listOfFiles.length; i++)  {  
				if (listOfFiles[i].isFile()) /* Is a file */
				   {
						file = listOfFiles[i].toString(); /* Get the path */
						fs = new FileInputStream(file); /* File IO Input Stream */
						contentReading(fs); /* Read file */
				    }
			}
			
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
						
				if(IsFirstRow){ /* TO IGNORE HEADER ROW */
					
					IsFirstRow = false; /* Next Row Not Header Row */
				}
				else{
					
					String[] temp = strLine.split(",");
					if(temp.length == 0) continue;
					
					for (int i = 0; i < temp.length; i++){
						if (temp[i].trim().length() == 0){
							temp[i] = "-1";
						}
					}
					
					exfeat.add(temp[2].toLowerCase().trim());
					opword.add(temp[4].toLowerCase().trim());
					context.add(temp[5].toLowerCase().trim());
					fsentiment.add(temp[6].toLowerCase().trim());
				}
				
			}
			IsFirstRow = true;
			fileInputStream.close(); /* Close file input stream */	
		}
		
		catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
		}
	}

}
