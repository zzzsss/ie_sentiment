package task.dc.sentiment;

import java.util.*;

import task.dc.*;
import text.*;
import training.*;

public class SentimentFeature0 extends DCFeatureGenerator{
	//the very-baseline feature --- bag of words(no negation)
	protected DataPoint get_one(Paragraph p,Dict d,int c){
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
}
