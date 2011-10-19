/**
 * @author ARUNDAS THULASIDAS CHANDRALEEBA
 *
 */

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
		
		//Training Set and Testing Set Arrays
		//First set is Annotation1.csv, Second set is Annotation10.csv
		String[][] exfeat     = {{"phone",    "phone",       "ringtones", "screen",   "carrier"  }, {"-1", "-1", "keypad / joystick", "use", "-1", "-1", "screen", "color", "camera", "open an application", "open an application", "-1", "-1", "-1", "-1", "newest color"}};
		String[][] opword     = {{"big",      "complicated", "beautiful", "quality",  "find"     }, {"good looking", "nice", "like", "easy", "small", "big", "big", "65000", "VGA", "slow", "minutes", "slow", "popular", "good", "good enough", "cool"}};
		String[][] context    = {{"too",      "too",         "-1",        "high",     "not"      }, {"-1", "-1", "-1", "-1", "not", "not", "quite", "-1", "still", "quite", "few", "really", "very", "-1", "not", "quite"}};
		String[][] fsentiment = {{"negative", "negative",    "positive",  "positive", "negative" }, {"positive", "positive", "positive", "positive", "positive", "positive", "negative", "positive", "positive", "negative", "negative", "negative", "positive", "positive", "negative", "positive"}};
		
		//Control of which set is used to train and which to test
		int trainIndex = 0;
		int testIndex = 1;
		boolean swap = false;
//		swap = true; /* <============== UNCOMMENT THIS LINE TO SWAP TRAINING SET AND TESTING SET!  */
		if (swap) {
			trainIndex = 1;
			testIndex = 0;
		}
		
//STEP 1: Express the problem with features
		String[] extractedSet = null;
		
		//exfeat
		extractedSet = extractSet(exfeat);
		List<String> alExfeat = new ArrayList<String>(extractedSet.length);
		for (int i=0; i<extractedSet.length; i++) {
			alExfeat.add(extractedSet[i]);
		}
		Attribute atExfeat = new Attribute("exfeat", alExfeat);
		
		//opword
		extractedSet = extractSet(opword);
		List<String> alOpword = new ArrayList<String>(extractedSet.length);
		for (int i=0; i<extractedSet.length; i++) {
			alOpword.add(extractedSet[i]);
		}
		Attribute atOpword = new Attribute("opword", alOpword);
		
		//context
		extractedSet = extractSet(context);
		List<String> alContext = new ArrayList<String>(extractedSet.length);
		for (int i=0; i<extractedSet.length; i++) {
			alContext.add(extractedSet[i]);
		}
		Attribute atContext = new Attribute("context", alContext);
		
		//fsentiment (class attribute)
		List<String> alFsentiment = new ArrayList<String>(2);
		alFsentiment.add("positive");
		alFsentiment.add("negative");
		Attribute atFsentiment = new Attribute("fsentiment", alFsentiment);
		
		//Declare the feature vector
		ArrayList<Attribute> alAttributes = new ArrayList<Attribute>(4);
		alAttributes.add(atExfeat);
		alAttributes.add(atOpword);
		alAttributes.add(atContext);
		alAttributes.add(atFsentiment);
		
//STEP 2: Train a Classifier
		//Training set
		Instances isTrainingSet = new Instances("Rel", alAttributes, exfeat[trainIndex].length);
		isTrainingSet.setClassIndex(3);
		
		//Fill training set with Instances
		for (int i=0; i<exfeat[trainIndex].length; i++) {
			Instance instance = new DenseInstance(4);
			instance.setValue((Attribute)alAttributes.get(0), exfeat[trainIndex][i]);
			instance.setValue((Attribute)alAttributes.get(1), opword[trainIndex][i]);
			instance.setValue((Attribute)alAttributes.get(2), context[trainIndex][i]);
			instance.setValue((Attribute)alAttributes.get(3), fsentiment[trainIndex][i]);
			isTrainingSet.add(instance);
		}
		
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
		Instances isTestingSet = new Instances("Rel", alAttributes, exfeat[testIndex].length);
		isTestingSet.setClassIndex(3);
		
		//Fill testing set with Instances
		for (int i=0; i<exfeat[testIndex].length; i++) {
			Instance instance = new DenseInstance(4);
			instance.setValue((Attribute)alAttributes.get(0), exfeat[testIndex][i]);
			instance.setValue((Attribute)alAttributes.get(1), opword[testIndex][i]);
			instance.setValue((Attribute)alAttributes.get(2), context[testIndex][i]);
			instance.setValue((Attribute)alAttributes.get(3), fsentiment[testIndex][i]);
			isTestingSet.add(instance);
		}
		
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
	
	//Extracts and returns a set of Strings (in array) from a given list of Strings (in a 2D array)
	private static String[] extractSet(String[][] list) {
		Set<String> set = new HashSet<String>();
		for (int i=0; i<list.length; i++) {
			for (int j=0; j<list[i].length; j++) {
				set.add(list[i][j]);
			}
		}
		
		Object[] setArray = set.toArray();
		String[] stringSet = new String[setArray.length];
		for (int i=0; i<setArray.length; i++) {
			stringSet[i] = (String)setArray[i];
		}
			
		return stringSet;
	}

	public void buildClassifier() {
		
	}
	
	public void classifySentiment() {
		
	}

}
