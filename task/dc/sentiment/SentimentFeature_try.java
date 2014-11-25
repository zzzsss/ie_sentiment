package task.dc.sentiment;

import java.util.*;

import task.dc.*;
import text.*;
import training.*;

public class SentimentFeature_try extends DCFeatureGenerator{
	//baseline feature --- bag of words
	protected DataPoint get_one(Paragraph p,Dict d,int c){
		//1. first deal with negation
		p.negation();
		//2. bag of words 
		// --- not good design of public sents and words, but ...
		Set<Integer> feats = new HashSet<Integer>();
		for(Sentence s : p.sents){
			for(int i=0;i<s.words.size();i++){
				String pos_i = s.pos2.get(i);
				if(pos_i.startsWith("J")||pos_i.startsWith("V")||pos_i.startsWith("N")||pos_i.startsWith("RB")){
					int ind = d.add(s.words.get(i)+pos_i);
					if(ind >= 0)	//exist or not-closed
						feats.add(ind);
				}
				//add all
				int ind = d.add(s.words.get(i));
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
