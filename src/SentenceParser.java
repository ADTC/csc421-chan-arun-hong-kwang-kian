/**
 * @author CHANDRASEHAR S/O RAJASEHARAN
 * 
 */

import java.io.File;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import edu.stanford.nlp.ling.TaggedWord;

public class SentenceParser
{
	private ArrayList<ArrayList<TaggedWord>> tagSent = new ArrayList<ArrayList<TaggedWord>>();
	private OPANNExtractor OPANN = new OPANNExtractor();
	private SentimentClassifier sentClass = new SentimentClassifier();
	private Instances instanceSet = null;
	public ArrayList<String> exfeat = new ArrayList<String>();
	public ArrayList<String> opword = new ArrayList<String>();
	public ArrayList<String> context = new ArrayList<String>();
	public ArrayList<String> fsentiment = new ArrayList<String>();
	private ArrayList<Attribute> Attributes = new ArrayList<Attribute>();
	
	public void processDirParser()
	{
		setUpSentenceFromDirectory();
		setUpCorpus();
		parseSentiment();
		parseData();
	}
	
	public void processFileParser(String filename)
	{
		setUpSentenceByFilename(filename);
		setUpCorpus();
		parseSentiment();
		parseData();
	}
	
	public void setUpSentenceFromDirectory()
	{
		File dirCorpus = new File("Corpus/");
		
		if(dirCorpus.isDirectory() && dirCorpus.list().length == 0)
			return;
		
		boolean firstTime = true;
		
		for(String filename : dirCorpus.list()) 
		{
			ArrayList<String> output = OPANN.generateFileToOPANN("Corpus/"+filename);
			ArrayList<ArrayList<TaggedWord>> temp = OPANN.convertToTaggedWord(output);
			tagSent.addAll(temp); //sets up sentences from all files in directory
			if (firstTime) {
				System.out.print("Reading " + dirCorpus.list().length + " corpus files ");
				firstTime = false;
			}
			System.out.print(".");
		}
		System.out.println(" done.");
	}
	
	public void setUpSentenceByFilename(String filename)
	{
		if(!new File("Corpus/"+filename).exists())
			return;
		
		ArrayList<String> output = OPANN.generateFileToOPANN("Corpus/"+filename);
		tagSent = OPANN.convertToTaggedWord(output);
	}
	
	public void setUpCorpus()
	{
		CorpusBuilder corpus = new CorpusBuilder();
		corpus.init("CorpusAnnotated");
		exfeat = corpus.exfeat;
		opword = corpus.opword;
		context = corpus.context;
		fsentiment = corpus.fsentiment;
	}
	
	public void parseSentiment()
	{
		if(fsentiment==null) return;
		for(String sentiment : fsentiment)
		{
			if(sentiment.toLowerCase().startsWith("pos"))
				fsentiment.set(fsentiment.indexOf(sentiment),"positive");
			else if(sentiment.toLowerCase().startsWith("neg"))
				fsentiment.set(fsentiment.indexOf(sentiment),"negative");
			else if(sentiment.toLowerCase().startsWith("neu"))
				fsentiment.set(fsentiment.indexOf(sentiment),"neutral");
		}
	}
	
	public void parseData()
	{
		Attributes = new ArrayList<Attribute>(sentClass.buildAttributeList(exfeat, opword, context));
		
		if(((Attributes == null || tagSent == null) || (exfeat == null || opword==null)) || context==null)
			return;
		
		instanceSet = new Instances("Rel", Attributes, exfeat.size());
		instanceSet.setClassIndex(3);
		Instance instance = new DenseInstance(4);
		int position = 0;
		String word = "";
		
		for(ArrayList<TaggedWord> tagWord : tagSent)
		{
			for(TaggedWord tag : tagWord)
			{
				if((position = getPosition(word = getWord(tag.value())))!=-1)
				{
					instance.setValue((Attribute)Attributes.get(0), "phone");
					instance.setValue((Attribute)Attributes.get(1), word.toLowerCase().trim());
					instance.setValue((Attribute)Attributes.get(2), context.get(position));
					instance.setValue((Attribute)Attributes.get(3), fsentiment.get(position));
					instanceSet.add(instance);
					instance = new DenseInstance(4);
				}
			}
		}
	}
	
	public int getPosition(String value)
	{
		for(String word : opword)
			if(word.trim().equalsIgnoreCase(value.trim()))
				return opword.indexOf(word);
		return -1;
	}
	
	public String getWord(String value)
	{
		String word = "";
		for(int i = 0;i < value.length();i++)
			if(Character.isLetter(value.charAt(i)))
				word += value.charAt(i);
		return word;
	}

	public Instances getInstanceSet()
	{
		return (instanceSet!=null)?instanceSet:null;
	}
	
	public ArrayList<Attribute> getAttributeList()
	{
		return (Attributes!=null)?Attributes:null;
	}
	
	/*
	 Implementation style at SentimentClassifier :
	 
	 //No need to initialize and call CorpusBuilder.Instead you can do this
	If using single file
	   sp.processFileParser(filename);
	Else if using directory
	   sp.processDirParser();
	   
	ArrayList<String> exfeat = sp.exfeat;
	ArrayList<String> opword = sp.opword;
	ArrayList<String> context = sp.context;
	ArrayList<String> fsentiment = sp.fsentiment;
	 
	alAttributes = sp.getAttributeList();
	
	if(sp.getInstanceSet()!=null)
	 	TestingSet = sp.getInstanceSet();
	 	
	 */
}