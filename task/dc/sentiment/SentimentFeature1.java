package task.dc.sentiment;

import java.util.*;

import task.dc.*;
import text.*;
import training.*;

public class SentimentFeature1 extends DCFeatureGenerator{
	private DataPoint get_one(Paragraph p,Dict d,int c){
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
	
	//return Object{List<DataPoint>[],Dict} --- if d==null then generate one
	public Object[] get_datapoints(List<Paragraph>[] pl,Dict d){
		Dict dic = d;
		if(dic == null)
			dic = new Dict();
		List<DataPoint>[] ret = new List[pl.length];
		//get_feature
		for(int i=0;i<pl.length;i++){
			ret[i] = new ArrayList<DataPoint>();
			for(Paragraph p : pl[i])
				ret[i].add(get_one(p,dic,i));
		}
		dic.close_dict();
		return new Object[]{ret,dic};
	}
	
	//test
	public static void main(String[] x){
		Tools.init(false,false);
		DCCorpusReader r = new SentimentReader1();
		DCFeatureGenerator f = new SentimentFeature1();
		List<Paragraph>[] what = r.read_corpus("data/t1");

		Object[] whatwhat = f.get_datapoints(what, null);
		List<DataPoint>[] a = (List<DataPoint>[])whatwhat[0];
		Dict b = (Dict)whatwhat[1];
		Tools.deinit();
	}
}
