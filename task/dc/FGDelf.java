package task.dc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import text.Dict;
import training.DataPoint;

public class FGDelf extends DCFeatureGenerator  {
	double[] for_dict;
	public FGDelf(FeatureCounter g){
		super(g);
	}
	//delta if-idf
	protected Object[] toData(List<HashMap<Integer,Integer>>[] l,Dict d,boolean training){
		assert(l.length == 2);	//asserting binary classification and must assert same dict
		
		if(training){
			int counts[][] = new int[d.get_length()][2];	//here 2 classes
			double logs[] = new double[d.get_length()];
			for(HashMap<Integer,Integer> one : l[0]){
				for(int x : one.keySet())
					counts[x][0] ++;
			}
			for(HashMap<Integer,Integer> one : l[1]){
				for(int x : one.keySet())
					counts[x][1] ++;
			}
			double P = l[1].size();
			double N = l[0].size();
	 		for(int i=0;i<d.get_length();i++){
				logs[i] = Math.log((P*(counts[i][0]+1))/(N*(counts[i][1]+1)));	//add-1 smooth
			}
			for_dict = logs;
		}
			
	 		//multiply the idf
			List<DataPoint>[] new_data = new ArrayList[l.length];
			for(int ii=0;ii<l.length;ii++){
	 			new_data[ii] = new ArrayList<DataPoint>();
	 			for(HashMap<Integer,Integer> dd : l[ii]){
	 				int[] ind = new int[dd.size()];
	 				double[] what = new double[dd.size()];
	 				int temp_count = 0;
	 				for(int i : dd.keySet()){
	 					ind[temp_count++] = i;
	 				}
	 				Arrays.sort(ind);
	 				for(int i=0;i<ind.length;i++){
	 					int the_index = ind[i];
	 					what[i] = for_dict[the_index]*dd.get(the_index);
	 				}
	 				new_data[ii].add(new DataPoint(ii,ind,what));
	 			}
	 		}

			return new Object[]{new_data,d};
	}
}
