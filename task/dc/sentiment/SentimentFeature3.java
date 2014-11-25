package task.dc.sentiment;

import java.util.*;

import task.dc.*;
import text.*;
import training.*;

public class SentimentFeature3 extends DCFeatureGenerator{
	// Unigram + Bigram
	protected DataPoint get_one(Paragraph p,Dict d,int c){
		//1. first deal with negation
		p.negation();
		//2. bag of words + POS
		// --- not good design of public sents and words, but ...
		Set<Integer> feats = new HashSet<Integer>();
		for(Sentence s : p.sents){
			for(int i=0;i<s.words.size();i++){
				//unigram
				String str = s.words.get(i);
				int ind = d.add(str);
				if(ind >= 0)	//exist or not-closed
					feats.add(ind);
				//bigram
				if(i < s.words.size()-1){
					String str2 = str+"||"+s.words.get(i+1);
					int ind2 = d.add(str2);
					if(ind2 >= 0)	//exist or not-closed
						feats.add(ind2);
				}
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
