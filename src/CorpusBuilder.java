/**
 * @author LEE HONG RU
 *
 */

import java.io.*;
import java.util.*;

public class CorpusBuilder {

	/**
	 * Build Corpus :
	 * 1) Read each file from CorpusAnnotated
	 * 2) At each line, use String[] temp = String.split(",") to get the array of Strings
	 * 3) Only retrieve, temp[2],temp[4],temp[5],temp[6]
	 * 4) Store them into a String in this format : "temp[2]:temp[4] temp[5]:temp[6]"
	 * 5) add the String to a ArrayList
	 * 
	 * Explanation
	 * - temp[2] refers to the explicit feature which we will use to compare the nouns
	 * - temp[4] and temp[5] refers to the opinion which we will use to compare the words of type JJ
	 * - temp[6] is the sentiment of the feature which we will use for classification purposes
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
