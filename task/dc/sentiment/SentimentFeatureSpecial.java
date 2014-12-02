package task.dc.sentiment;

import java.util.ArrayList;
import java.util.List;

import text.Dict;
import text.Paragraph;
import training.DataPoint;

public class SentimentFeatureSpecial extends task.dc.DCFeatureGenerator{
	//special features considering frequencies
	
	public SentimentFeatureSpecial(){
		super(false);
	}
	
	protected DataPoint get_one(Paragraph p,Dict d,int c){
		return null;	//error
	}
	
	//has its own get_data
	public Object[] get_datapoints(List<Paragraph>[] pl,Dict d){
		Dict dic = d;
		if(dic == null)
			dic = new Dict();
		List<DataPoint>[] ret = new List[pl.length];
		//get_features
		
		dic.close_dict();
		return new Object[]{ret,dic};
	}
}
