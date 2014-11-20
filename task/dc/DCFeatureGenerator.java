package task.dc;

import text.*;
import training.DataPoint;

import java.util.*;

public abstract class DCFeatureGenerator {
	abstract protected DataPoint get_one(Paragraph p,Dict d,int c);
	
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
}
