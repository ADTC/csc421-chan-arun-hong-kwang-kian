/**
 * @author TOH KIAN HUI
 *
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class OPANNExtractor {

	/**
	 * @param args
	 */
	
	private MaxentTagger tagger = null; //avoids reloading of tagger for each corpus file
	
	//To access the files: an example of the path is Corpus/0.txt
	
	public static void main(String[] args) {
		
		//=== For testing purpose ===
/*		int fileNumber = 0;
		while (fileNumber < 89) {
			ArrayList<String> output = generateFileToOPANN("Corpus/" + fileNumber + ".txt");
			
			
//			for(String s:output)
//			{
//				System.out.println(s);
//			}
//			
//			System.out.println("\n\n\n");
			System.out.println("\n---------Corpus/" + fileNumber + ".txt---------------------------------------");
			
			//Using covertToTaggedWord(ArrayList<String> input);
			ArrayList<ArrayList<TaggedWord>> tw = convertToTaggedWord(output);
			for(ArrayList<TaggedWord> s : tw)
			{
				for(TaggedWord w : s)
				{
					System.out.print(w.toString()+" ");
				}
				System.out.println("");
			}
			fileNumber++;
		}*/
		
	}
	
	//Method to generate OPANN
	//Return null if file did not exist or tagging error
	public ArrayList<String> generateFileToOPANN(String filepath)
	{
		ArrayList<String> generated = new ArrayList<String>();
		
		//First check if the file exist
		if(!checkIfFileExist(filepath))
			return null;
		
		//Second extract the raw corpus file
		generated = getRawFile(filepath);
		
		//Third remove all unnecessary tags from the raw corpus
		 generated = cleanFileFromCorpus(generated);
		
		 //Fourth POS tag each sentence, may return null
		 ArrayList<ArrayList<TaggedWord>>tw = addPOSTags(generated);
		 
		 //Fifth Select only the JJs and NNs Tags
		 if(tw !=null)
		 {
			 generated = extractNNJJInEachSentence( tw) ;
		 }
		 else
		 {
			 generated = null;
		 }
		 
		return generated;
	}
	
	//Method to covert the tagged sentence to tagged word
	public ArrayList<ArrayList<TaggedWord>> convertToTaggedWord(ArrayList<String> file)
	{
		ArrayList<ArrayList<TaggedWord>> generated = new ArrayList<ArrayList<TaggedWord>>();
		
		for(String sentence:file)
		{
			ArrayList<TaggedWord> tw = new ArrayList<TaggedWord>();
			String []temp = sentence.split(" ");
			
			for(String s:temp)
			{
				if(s.contains("/") && s.length()>3)
				{
					String x[] = s.split("/");
					tw.add(new TaggedWord(x[0],x[1]));
				}
			}
			generated.add(tw);
		}
		 
		return generated;
	}
	
	
	//Method to check if the file exist
	private boolean checkIfFileExist(String filepath)
	{
		File f = new File(filepath);
		return f.exists();
	}
	
	//Method to read in the file
	//Each element in the list is one sentence
	private ArrayList<String> getRawFile(String filepath)
	{
		ArrayList<String> output = new ArrayList<String>();
		
		try {
			
			FileInputStream fstream = new FileInputStream(filepath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String temp;
			while((temp=br.readLine())!=null)
			{
				output.add(temp);
			}
			
		} catch (Exception e) {
		}
		
		return output;
	}
	
	//Method to remove other items contain only strings
	//Only need to remove first word from of each sentence
	private ArrayList<String> cleanFileFromCorpus(ArrayList<String> file)
	{
		ArrayList<String> output = new ArrayList<String>();
		
		//Get the first occurrence of space
		for(String sentence:file)
		{
			int firstSpace = sentence.indexOf(' ');
			sentence = sentence.substring(firstSpace);
			output.add(sentence);
		}
		
		return output;
	}
	
	//Method to add POS tags to the file 
	/*
	 * ---> bidirectional-distsim-wsj-0-18.tagger
		Trained on WSJ sections 0-18 using a bidirectional architecture and
		including word shape and distributional similarity features.
		Penn Treebank tagset.
		Performance:
		97.28% correct on WSJ 19-21
		(90.46% correct on unknown words)
		
		---> left3words-wsj-0-18.tagger
		Trained on WSJ sections 0-18 using the left3words architecture and
		includes word shape features.  Penn tagset.
		Performance:
		96.97% correct on WSJ 19-21
		(88.85% correct on unknown words)
		
		---> left3words-distsim-wsj-0-18.tagger
		Trained on WSJ sections 0-18 using the left3words architecture and
		includes word shape and distributional similarity features. Penn tagset.
		Performance:
		97.01% correct on WSJ 19-21
		(89.81% correct on unknown words)
	 */
	private ArrayList<ArrayList<TaggedWord>> addPOSTags(ArrayList<String> file)
	{
		ArrayList<ArrayList<TaggedWord>> output = new ArrayList<ArrayList<TaggedWord>>();
		try {
			//Loading the tagger model;
//			MaxentTagger tagger = new MaxentTagger("lib/model/bidirectional-distsim-wsj-0-18.tagger");
			if (tagger==null) {
				loadTaggerModel(null);
			}
			
			for(String sentence:file)
			{
				ArrayList<TaggedWord> tw = new ArrayList<TaggedWord>();
				String []temp = sentence.split(" ");
				
				for(String s:temp)
				{
					s = tagger.tagString(s);
					if(s.contains("/") && s.length()>3)
					{
						String x[] = s.split("/");
						tw.add(new TaggedWord(x[0],x[1]));
					}
				}
				output.add(tw);
			}
			
		} catch (Exception e) {
			return null;
		} 
		
		
		return output;
	}
	
	//Method to load tagger model
	private void loadTaggerModel(String filePath) {
		if (filePath==null || !checkIfFileExist(filePath)) {
			filePath = "lib/model/bidirectional-distsim-wsj-0-18.tagger";
		}
		try {
			tagger = new MaxentTagger(filePath);
		} catch(Exception e) {
			tagger = null;
		}
	}
	
	//Method to extract only the NN JJ in each Sentence
	private ArrayList<String> extractNNJJInEachSentence(ArrayList<ArrayList<TaggedWord>> file)
	{
		ArrayList<String> output = new ArrayList<String>();
		for(int i =0;i<file.size();i++)
		{
			String sentence = "";
			
			ArrayList<TaggedWord> temp = file.get(i);

			for(int x=0;x<temp.size();x++)
			{
				String tag = temp.get(x).tag().trim();
				if(tag.toUpperCase().startsWith("NN") || tag.toUpperCase().startsWith("JJ"))
				{
					sentence = sentence+temp.get(x).toString();
				}	
			}
			if(sentence.length()>0)
				output.add(sentence);
		}
		return output;
	}
	
	/*
	 * Possible tags
	 * 1.CC Coordinating conjunction 
		2.CD Cardinal number 
		3.DT Determiner 
		4.EX Existential there 
		5.FW Foreign word 
		6.IN Preposition or subordinating conjunction 
		7.JJ Adjective 
		8.JJR Adjective, comparative 
		9.JJS Adjective, superlative 
		10.LS List item marker 
		11.MD Modal 
		12.NN Noun, singular or mass 
		13.NNS Noun, plural 
		14.NNP Proper noun, singular 
		15.NNPS Proper noun, plural 
		16.PDT Predeterminer 
		17.POS Possessive ending 
		18.PRP Personal pronoun 
		19.PRP$ Possessive pronoun 
		20.RB Adverb 
		21.RBR Adverb, comparative 
		22.RBS Adverb, superlative 
		23.RP Particle 
		24.SYM Symbol 
		25.TO to 
		26.UH Interjection 
		27.VB Verb, base form 
		28.VBD Verb, past tense 
		29.VBG Verb, gerund or present participle 
		30.VBN Verb, past participle 
		31.VBP Verb, non­3rd person singular present 
		32.VBZ Verb, 3rd person singular present 
		33.WDT Wh­determiner 
		34.WP Wh­pronoun 
		35.WP$ Possessive wh­pronoun 
		36.WRB Wh­adverb 
	 */

}
