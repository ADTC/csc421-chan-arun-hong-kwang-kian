/**
 * @author ARUNDAS THULASIDAS CHANDRALEEBA
 *
 */

import java.util.ArrayList;
import java.util.Collection;
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

	/**
	 * Chandra's Notes:
	 * Wait for Hong Ru to complete his corpus builder.
	 * But you can simulate your classifier as follows.
	 *        e.g.
	 *        Classifier Building part
	 *        =======================
	 *          Build attribute(data) for opinion
	 *          Build class attribute which is either positive or negative
	 *         
	 *        Initializing data and their corresponding class(positive or negative)
	 *        =======================
	 *          Using Hongru's CorpusBuilder, extract the opinion and the fsentiment
	 *          For each opinion, classify it according to the fsentiment
	 * 
	 * Full details on how to use Naive Bayes classifier,
	 * how to build, train and test the classifier
	 * can be seen under "docs/Classification Docs"
	 * where a Weka API manual and a link on how to program
	 * is provided. (NOTE : the link on how to program is pretty useful)
	 * 
	 */
	public static void main(String[] args) {
		// Main method is used as a testing and development sandbox.
		// Source code in main method will be refined, generalized and
		//    moved to other methods once it is ready for deployment.
		// FastVector is deprecated. Using List and ArrayList instead.
		
		SentimentClassifier sc = new SentimentClassifier();
		SentenceParser sp = new SentenceParser();
		sp.processDirParser();
		//Training set from Corpus
		//CorpusBuilder corpus = new CorpusBuilder();
		//corpus.init("CorpusAnnotated");
		ArrayList<String> exfeat = sp.exfeat;
		ArrayList<String> opword = sp.opword;
		ArrayList<String> context = sp.context;
		ArrayList<String> fsentiment = sp.fsentiment;
		
		//Testing set from Annotation 1.csv
		String[] strArray = new String[]{"phone", "phone", "ringtones", "screen", "carrier"};
		ArrayList<String> exfeatTest = new ArrayList<String>();
		for(String item : strArray) {
			exfeatTest.add(item);
		}
		strArray = new String[]{"big", "complicated", "beautiful", "quality", "find"};
		ArrayList<String> opwordTest = new ArrayList<String>();
		for(String item : strArray) {
			opwordTest.add(item);
		}
		strArray = new String[]{"too", "too", "-1", "high", "not"};
		ArrayList<String> contextTest = new ArrayList<String>();
		for(String item : strArray) {
			contextTest.add(item);
		}
		strArray = new String[]{"negative", "negative", "positive", "positive", "negative"};
		ArrayList<String> fsentimentTest = new ArrayList<String>();
		for(String item : strArray) {
			fsentimentTest.add(item);
		}
		
//STEP 1: Express the problem with features

		ArrayList<Attribute> alAttributes = sp.getAttributeList();
		//ArrayList<Attribute> alAttributes = sc.buildAttributeList(exfeat, opword,context);
		
//STEP 2: Train a Classifier
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
		
//STEP 3: Test the Classifier
		//Testing set
		Instances isTestingSet = sp.getInstanceSet();
		/*sc.buildInstanceSet(exfeatTest, opwordTest,contextTest, fsentimentTest, alAttributes);*/
		
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
		
		//Print the result
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		
		//Get and print confusion matrix
		double[][] cmMatrix = eTest.confusionMatrix();
		System.out.println("Confusion Matrix: ----------");
		for (int i=0; i<cmMatrix.length; i++) {
			for (int j=0; j<cmMatrix[i].length; j++) {
				System.out.print("\t" + cmMatrix[i][j]);
			}
			System.out.println();
		}
		System.out.println("----------------------------");
		
//STEP 4: Use the classifier
		//Create an Instance (for example iUse)
		//Then:
//		iUse.setDataset(isTraining);
		
		//Get likelihood (probability)
		//[0] = positive
		//[1] = negative
//		double[] fDistribution = cModel.distributionForInstance(iUse);
	}

	/**
	 * @param exfeat
	 * @param opword
	 * @param context
	 * @return
	 */
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
		
		//Declare the feature vector
		ArrayList<Attribute> alAttributes = new ArrayList<Attribute>(4);
		alAttributes.add(atExfeat);
		alAttributes.add(atOpword);
		alAttributes.add(atContext);
		alAttributes.add(atFsentiment);
		return alAttributes;
	}

	/**
	 * @param exfeat
	 * @param opword
	 * @param context
	 * @param fsentiment
	 * @param alAttributes
	 * @return
	 */
	public Instances buildInstanceSet(ArrayList<String> exfeat,
			ArrayList<String> opword, ArrayList<String> context,
			ArrayList<String> fsentiment, ArrayList<Attribute> alAttributes) {
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
