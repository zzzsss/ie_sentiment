package task.dc.sentiment;

import java.util.*;

import task.dc.*;
import text.*;
import training.*;

public class SentimentFeature1 extends DCFeatureGenerator{
	//baseline feature --- bag of words
	protected DataPoint get_one(Paragraph p,Dict d,int c){
		//1. first deal with negation
		p.negation();
		//2. bag of words 
		// --- not good design of public sents and words, but ...
		Set<Integer> feats = new HashSet<Integer>();
		for(Sentence s : p.sents){
			for(String str : s.words){
				int ind = d.add(str);
				if(ind >= 0)	//exist or not-closed
					feats.add(ind);
			}
		}
		int[] feats_one = new int[feats.size()];
		int i=0;
		for(int x : feats)
			feats_one[i++] = x;
		Arrays.sort(feats_one);
		return new DataPoint(c,feats_one);
	}
	
	//test
	public static void main(String[] x){
		Tools.init(false,false);
		DCCorpusReader r = new SentimentReader1();
		DCFeatureGenerator f = new SentimentFeature1();
		List<Paragraph>[] what = r.read_corpus("data/t");

		Object[] whatwhat = f.get_datapoints(what, null);
		List<DataPoint>[] a = (List<DataPoint>[])whatwhat[0];
		Dict b = (Dict)whatwhat[1];
		Tools.deinit();
	}
}
