/**
 * @author ARUNDAS THULASIDAS CHANDRALEEBA
 * Chandra's Notes:
- For your case, you have to wait for Hong Ru to complete his corpus builder.
- But you can simulate your classifier as follows.
  e.g.
       Classifier Building part
       =======================
         Build attribute(data) for opinion
         Build class attribute which is either positive or negative
        
       Initializing data and their corresponding class(positive or negative)
       =======================
         Using Hongru's CorpusBuilder, extract the opinion and the fsentiment
         For each opinion, classify it according to the fsentiment
         
- Full details on how to use Naive Bayes classifier, how to build, train and test the classifier
  can be seen under "docs/Classification Docs" where a Weka API manual and a link on how to program
  is provided. (NOTE : the link on how to program is pretty useful)
 */

import weka.core.*;
import weka.core.Instances;
import weka.classifiers.bayes.*;

public class SentimentClassifier {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void buildClassifier() {
		
	}
	
	public void classifySentiment() {
		
	}

}
