package task.dc.sentiment;

import java.util.*;

import task.dc.*;
import text.*;
import training.*;

public class SentimentFeature4 extends DCFeatureGenerator{
	//adding the although-but dealing...
	protected DataPoint get_one(Paragraph p,Dict d,int c){
		// unigram + bigram + although-but
		Set<Integer> feats = new HashSet<Integer>();
		for(Sentence s : p.sents){
			s.negation();
			
			int size = s.words.size();
			for(int i=0;i<size;i++){
				String c_word = s.words.get(i);
				//unigram
				add_one_str_feature(c_word,d,feats);
				//bigram
				if(i < s.words.size()-1){
					String str2 = c_word+"||"+s.words.get(i+1);
					add_one_str_feature(str2,d,feats);
				}
			}
			
			deal_but(s);
			for(int i=0;i<size;i++){
				String c_word = s.words.get(i);
				//unigram
				add_one_str_feature(c_word,d,feats);
				//bigram
				if(i < s.words.size()-1){
					String str2 = c_word+"||"+s.words.get(i+1);
					add_one_str_feature(str2,d,feats);
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
