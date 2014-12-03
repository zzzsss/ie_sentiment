package task.dc.sentiment;

import java.util.*;
import task.dc.DCFeatureGenerator;
import text.Dict;
import text.Paragraph;
import text.Sentence;
import training.DataPoint;

public class SentimentFeatureSpecial extends task.dc.DCFeatureGenerator{
	//special features considering frequencies --- tfidf score
	
	public SentimentFeatureSpecial(){
		super(false);
	}
	protected DataPoint get_one(Paragraph p,Dict d,int c){
		return null;	//error
	}
	
	static class SentimentFeature1Freq extends DCFeatureGenerator{
		//baseline feature --- bag of words with freq
		protected DataPoint get_one(Paragraph p,Dict d,int c){
			//1. first deal with negation
			p.negation();
			//2. bag of words 
			// --- not good design of public sents and words, but ...
			HashMap<Integer,Integer> feats = new HashMap<Integer,Integer>();
			for(Sentence s : p.sents){
				for(String str : s.words){
					int ind = d.add(str);
					if(ind >= 0){
						Integer ori = feats.get(ind);
						if(ori==null)
							feats.put(ind, 1);
						else
							feats.put(ind, ori+1);
					}
				}
			}
			int[] feats_one = new int[feats.size()];
			double[] feats_value = new double[feats.size()];
			int i=0;
			for(int x : feats.keySet())
				feats_one[i++] = x;
			Arrays.sort(feats_one);
			for(int ii=0;ii<feats.size();ii++)
				feats_value[ii] = feats.get(feats_one[ii]);
			return new DataPoint(c,feats_one,feats_value);
		}
	}
	//maybe useful
	public static DCFeatureGenerator get_fgfreq(){
		return new SentimentFeature1Freq();
	}
	
	//has its own get_data
	public Object[] get_datapoints(List<Paragraph>[] pl,Dict d){
		//get_features
		//use the simple but not good way;; and only for unigrams
		Object[] ret = (new SentimentFeature1Freq()).get_datapoints(pl, d);
		List<DataPoint>[] data = (List<DataPoint>[])ret[0];
		Dict dict = (Dict)ret[1];
		
		///*
		int counts[][] = new int[dict.get_length()][2];	//here 2 classes
		double logs[] = new double[dict.get_length()];
		for(DataPoint one : data[0]){
			for(int x : one.get_index())
				counts[x][0] ++;
		}
		for(DataPoint one : data[1]){
			for(int x : one.get_index())
				counts[x][1] ++;
		}
		double P = data[1].size();
		double N = data[0].size();
 		for(int i=0;i<dict.get_length();i++){
			logs[i] = Math.log((P*(counts[i][0]+1))/(N*(counts[i][1]+1)));	//add-1 smooth
		}
		
 		//multiply the idf
 		for(List<DataPoint> l : data){
 			for(DataPoint dd : l){
 				int[] ind = dd.get_index();
 				double[] what = dd.get_fvalue();
 				for(int i=0;i<what.length;i++)
 					what[i] = what[i]*logs[ind[i]];
 			}
 		}
 		//*/

		return new Object[]{data,dict};
	}
}
