package text;

import java.util.*;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class Sentence {
	//1.nlpir
	public List<String> words;
	public List<String> pos;
	public String segmented_str;
	//2.tagger
	List<String> pos2;	//using stanford pos tagger
	//3.ner
	List<String> ner;
	//4.parser
	Tree tree;
	List<TypedDependency> dep;
	
	//constructor
	//-- x is the extra info for seg(if no info then null)
	protected void self_init(String s,String[] x){
		Object[] sseg = Tools.get_seg(s,x);
		words = (List<String>)sseg[0];
		pos = (List<String>)sseg[1];
		segmented_str = (String)sseg[2];
		pos2 = Tools.get_pos(words);
		//options
		ner = Tools.get_ner(segmented_str);
		Object[] sparse = Tools.get_parse(words);
		if(sparse != null){
			tree = (Tree)(sparse[0]);
			dep = (List<TypedDependency>)sparse[1];
		}
	}
	public Sentence(String s,String[] x){	
		self_init(s,x);
	}
	public Sentence(String s){
		self_init(s,null);
	}
	public String toString(){
		return segmented_str;
	}
	
	//negation --- for sentiment
	static String[] NEG_INDICATE_ARRAY = new String[]{"不","没有","不是"};
	static HashSet<String> NEG_INDICATE = new HashSet<String>();
	static String[] STOP_INDICATE_ARRAY = new String[]{"!","?",",",".","？","！","，","。"};
	static HashSet<String> STOP_INDICATE = new HashSet<String>();
	static{
		for(String x:NEG_INDICATE_ARRAY)
			NEG_INDICATE.add(x);
		for(String x:STOP_INDICATE_ARRAY)
			STOP_INDICATE.add(x);
	}
	static String NEG_HEAD = "NOT_";
	public void negation(){
		//simple method
		int neg = 0;
		for(int i=0;i<words.size();i++){
			String now = words.get(i);
			if(NEG_INDICATE.contains(now))
				neg = 1-neg;
			else if(STOP_INDICATE.contains(now))
				neg = 0;
			else if(neg==1)
				words.set(i,NEG_HEAD+now);
		}
	}
	//bag of words
	public List<Integer> get_bagofwords(){
		List<Integer> ret = new ArrayList<Integer>();
		return ret;
	}
}
