package task.dc;

import text.*;
import training.DataPoint;

import java.util.*;

public abstract class DCFeatureGenerator {
	abstract protected DataPoint get_one(Paragraph p,Dict d,int c);
	
	//can perform feature-selection
	public boolean can_select;
	protected DCFeatureGenerator(boolean c){
		can_select = c;
	}
	protected DCFeatureGenerator(){
		can_select = true;
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
	
	protected static void add_one_str_feature(String x,Dict d,Set<Integer> feats){
		int ind = d.add(x);
		if(ind >= 0)	//exist or not-closed
			feats.add(ind);
	}
	
	// although-but another routine like negation
	//--maybe this should be added to the Sentence class, but since it is not convenient to change...
	static String[] ALTHOUGH_INDICATE_ARRAY = new String[]{"虽说","固然","非但","虽然","尽管"};
	static HashSet<String> ALTHOUGH_INDICATE = new HashSet<String>();
	static String[] BUT_INDICATE_ARRAY = new String[]{"不过","但","但是","而是","反之","可是","然而","转而","恰恰相反","反倒","反而","却","仍"};
	static HashSet<String> BUT_INDICATE = new HashSet<String>();
	static{
		for(String x:ALTHOUGH_INDICATE_ARRAY)
			ALTHOUGH_INDICATE.add(x);
		for(String x:BUT_INDICATE_ARRAY)
			BUT_INDICATE.add(x);
	}
	static String AB_HEAD[] = new String[]{"ALTHOUGH_","BUT_"};
	protected static void deal_but(Sentence s){
		//simple method
		int idi = -1;	//-1 nothing, 0 although, 1 but
		for(int i=0;i<s.words.size();i++){
			String now = s.words.get(i);
			if(ALTHOUGH_INDICATE.contains(now))
				idi = 0;
			else if(BUT_INDICATE.contains(now))
				idi = 1;
			else if(s.pos.get(i).length()>0 && s.pos.get(i).charAt(0) == 'w')	//punct
				idi = -1;
			else if(idi >= 0)
				s.words.set(i,AB_HEAD[idi]+now);
		}
	}
}
