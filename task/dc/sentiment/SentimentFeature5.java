package task.dc.sentiment;

import java.util.*;

import task.dc.*;
import text.*;
import training.*;

public class SentimentFeature5 extends DCFeatureGenerator{
	//Unigram + bigram-ADJ-context
	protected DataPoint get_one(Paragraph p,Dict d,int c){
		//1. first deal with negation
		p.negation();
		//2. bag of words 
		// --- not good design of public sents and words, but ...
		Set<Integer> feats = new HashSet<Integer>();
		for(Sentence s : p.sents){
			for(int i=0;i<s.words.size();i++){
				String str = s.words.get(i);
				if(s.pos2.get(i).indexOf("JJ") > -1){
					if(i<s.words.size()-1){
						int ind = d.add("JJ:"+str+"||"+s.words.get(i+1));
						if(ind >= 0)	//exist or not-closed
							feats.add(ind);
					}
					if(i>0){
						int ind = d.add("JJ:"+s.words.get(i-1)+"||"+str);
						if(ind >= 0)	//exist or not-closed
							feats.add(ind);
					}
				}
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
