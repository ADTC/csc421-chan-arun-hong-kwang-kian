/**
 * @author ARUNDAS THULASIDAS CHANDRALEEBA
 *
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class SentimentClassifier {
	
	public static void main(String[] args) {
		//Note: FastVector is deprecated. Using List and ArrayList instead.
		
		if(args.length>1)
			return;
		
		String filename = (args.length==1)?args[0]:"";
		
		SentimentClassifier sc = new SentimentClassifier();
		SentenceParser sp = new SentenceParser();
		
		if(filename.isEmpty())
			sp.processDirParser();
		else
			sp.processFileParser(filename);
		
		if(sp.getInstanceSet()==null)
			return;
		
		ArrayList<String> exfeat = sp.exfeat;
		ArrayList<String> opword = sp.opword;
		ArrayList<String> context = sp.context;
		ArrayList<String> fsentiment = sp.fsentiment;

		ArrayList<Attribute> alAttributes = sp.getAttributeList();
		
		//Training set
		Instances isTrainingSet = sc.buildInstanceSet(exfeat, opword, context,
				fsentiment, alAttributes);
		
		Classifier cModel = (Classifier)new NaiveBayes();
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e) {
			System.out.println("\nTraining of classifier model failed. Aborting...\n");
			e.printStackTrace();
			return;
		}
		
		//Testing set
		Instances isTestingSet = sp.getInstanceSet();
		
		//Test the model
		Evaluation eTest;
		try {
			eTest = new Evaluation(isTrainingSet);
			eTest.evaluateModel(cModel, isTestingSet);
		} catch (Exception e) {
			System.out.println("\nTesting of model failed. Aborting...\n");
			e.printStackTrace();
			return;
		}
		
		//Print the test result summary
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		
		//Get user's choice
		int menuChoice = sc.displayMenu();
		
		//Display instances and their results
		NumberFormat fmt = new DecimalFormat("000.00");
		double classAttributes[] = {0.00,0.00,0.00};
		int i = 0;
		String headerText = "Positive Neutral Negative\tResult\t\tInstance";
		
		System.out.println(headerText); //print header at top
		for (Instance iUse:isTestingSet) {
			try {
				i = 0;
				double[] fDistribution = cModel.distributionForInstance(iUse);
				//add percentages to classAttributes array
				for (double fDist:fDistribution) {
					double fDistPercent = ((double)(int)(fDist*10000))/100;
					classAttributes[i++] += fDistPercent;
				}
				//display the instance
				if (menuChoice == 1 && sc.getSentiment(fDistribution).equalsIgnoreCase(iUse.toString(3))) {
					/* do nothing as this is a correctly classified instance
					   and only incorrectly classified instances are requested */
				} else {
					for (double fDist:fDistribution){
						double fDistPercent = ((double)(int)(fDist*10000))/100;
						String fDistPercentStr = changeLeadingZeroToSpace(fmt.format(fDistPercent));
						System.out.print(fDistPercentStr + "%\t");
					}
					System.out.println("\t" + sc.getSentiment(fDistribution) + "\t" + iUse.toString().replace(',', '\t'));
				}
			} catch (Exception e) {
				System.out.println("Exception generated for instance: " + iUse);
				e.printStackTrace();
			}
		}
		System.out.println(headerText); //reprint header at bottom
		System.out.println();
		displayClassOfTestData(classAttributes,isTestingSet.size());
	}
	//Method to display the sentiment of the document(s)
	private static void displayClassOfTestData(double fDist[],int size) {
		NumberFormat fmt = new DecimalFormat("0.00");
		int position = 0, i = 0;
		double max = 0.00;
		String classVal = "";
		for(double val : fDist)
		{
			position = (Math.max(val,max)==max)?position:i;
			max = Math.max(val,max);
		}
		classVal = (position==0)?"positive":(position==1)?"neutral":(position==2)?"negative":"";
		System.out.println("The sentiment of the document(s) is "+classVal+" with a rating of "+ changeLeadingZeroToSpace(fmt.format(max/size)) + "%");
	}
	
	//Method to display user input menu
	private int displayMenu() {
		int menuChoice;
		System.out.println ("[1] Display incorrectly classified instances only");
		System.out.println ("[2] Display all classified instances");
		System.out.print ("Enter your choice (default is 1): ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			String menuInput = reader.readLine();
			menuChoice = Integer.parseInt(menuInput.trim());
		} catch (Exception e) {
			System.out.println ("Input error! Displaying incorrectly classified instances...");
			System.out.println();
			menuChoice = 1;
		}
		System.out.println();
		return menuChoice;
	}
	
	//Method to judge sentiment from probability distribution
	private String getSentiment(double[] fDistribution) {
		String sentiment = "";
		int biggerOfFirstTwo = 0;
		if (fDistribution.length == 3) {
			sentiment = "positive";
			if (fDistribution[1] > fDistribution[0]) {
				sentiment = "neutral ";
				biggerOfFirstTwo = 1;
			}
			if (fDistribution[2] > fDistribution[biggerOfFirstTwo]) {
				sentiment = "negative";
			}
		}
		return sentiment;
	}
	
	//Method to change leading zeroes in a string (usually a formatted number) to spaces
	private static String changeLeadingZeroToSpace(String num) {
		String result = "";
		for (int i=0; i<num.length(); i++) {
			if (num.charAt(i) == '0' && num.charAt(i+1)!='.') {
				result += " ";
			} else {
				result += num.substring(i);
				break;
			}
		}
		return result;
	}

	//Method to build an attribute list
	public ArrayList<Attribute> buildAttributeList(
			ArrayList<String> exfeat, ArrayList<String> opword,
			ArrayList<String> context) {
		//exfeat
		List<String> alExfeat = extractSetAsList(exfeat);
		Attribute atExfeat = new Attribute("exfeat", alExfeat);
		
		//opword
		List<String> alOpword = extractSetAsList(opword);
		Attribute atOpword = new Attribute("opword", alOpword);
		
		//context
		List<String> alContext = extractSetAsList(context);
		Attribute atContext = new Attribute("context", alContext);
		
		//fsentiment (class attribute)
		List<String> alFsentiment = new ArrayList<String>(2);
		alFsentiment.add("positive");
		alFsentiment.add("neutral");
		alFsentiment.add("negative");
		Attribute atFsentiment = new Attribute("fsentiment", alFsentiment);
		
		//Declare the feature arraylist
		ArrayList<Attribute> alAttributes = new ArrayList<Attribute>(4);
		alAttributes.add(atExfeat);
		alAttributes.add(atOpword);
		alAttributes.add(atContext);
		alAttributes.add(atFsentiment);
		return alAttributes;
	}

	//Method to build a set of instances from given attributes
	public Instances buildInstanceSet(ArrayList<String> exfeat,
			ArrayList<String> opword, ArrayList<String> context,
			ArrayList<String> fsentiment, ArrayList<Attribute> alAttributes) {
		//Create instance set
		Instances instanceSet = new Instances("Rel", alAttributes, exfeat.size());
		instanceSet.setClassIndex(3);
		
		//Fill set with Instances
		for (int i=0; i<exfeat.size(); i++) {
			Instance instance = new DenseInstance(4);
			instance.setValue((Attribute)alAttributes.get(0), exfeat.get(i));
			instance.setValue((Attribute)alAttributes.get(1), opword.get(i));
			instance.setValue((Attribute)alAttributes.get(2), context.get(i));
			instance.setValue((Attribute)alAttributes.get(3), fsentiment.get(i));
			instanceSet.add(instance);
		}
		return instanceSet;
	}

	/**
	 * @param list a given ArrayList of Strings
	 * @return a set of Strings in a List (no duplicate items)
	 */
	public List<String> extractSetAsList(ArrayList<String> list) {
		Set<String> set = new HashSet<String>();
		for (String item:list) {
			set.add(item.toLowerCase().trim()); //filters duplicate items
		}
		List<String> returnList = new ArrayList<String>(set.size());
		for (String item:set) {
			returnList.add(item); //converts to list
		}
		return returnList;
	}

}
