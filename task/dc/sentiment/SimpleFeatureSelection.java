package task.dc.sentiment;

import java.util.*;

import text.Dict;
import text.Paragraph;
import training.DataPoint;
import task.dc.DCFeatureGenerator;

public class SimpleFeatureSelection extends DCFeatureGenerator{
	protected DataPoint get_one(Paragraph p,Dict d,int c){
		return null;	//error
	}
	
	DCFeatureGenerator base;
	int low_end,high_end;
	public SimpleFeatureSelection(DCFeatureGenerator b,int l,int h){
		super(false);
		low_end = l;
		high_end = h;
		base = b;
		if(!b.can_select)
			base = null;
	}
	
	//return Object{List<DataPoint>[],Dict} --- if d==null then generate one
		public Object[] get_datapoints(List<Paragraph>[] pl,Dict d){
			if(d != null){	//not train-process
				return base.get_datapoints(pl, d);
			}
			Object[] ret = base.get_datapoints(pl, d);
			List<DataPoint>[] data = (List<DataPoint>[])ret[0];
			Dict dict = (Dict)ret[1];
			
			System.err.println("Original featre size is "+dict.get_length());
			String [] strings = new String[dict.get_length()];
			for(String s : dict.maps.keySet()){
				strings[dict.index(s)] = s;
			}
			
			//feature counting
			int counting[][] = new int[dict.get_length()][data.length];
			int deleting[] = new int[dict.get_length()];
			for(int i=0;i<pl.length;i++){
				for(DataPoint dp : data[i]){
					for(int x : dp.get_index())
						counting[x][i] += 1;
				}
			}
			
			//choosing them based on counting
			Dict new_dict = new Dict();
			for(int i=0;i<dict.get_length();i++){
				boolean to_delete = true;
				for(int j=0;j<data.length;j++){
					if(counting[i][j]>=low_end && counting[i][j]<=high_end)
						to_delete = false;
				}
				if(to_delete)
					deleting[i] = 1;
				else
					new_dict.add(strings[i]);
			}
			new_dict.close_dict();
			System.err.println("Shrinking featre size to "+new_dict.get_length());
			//reconstruct data
			List<DataPoint>[] new_data = new ArrayList[data.length];
			for(int i=0;i<data.length;i++){
				new_data[i] = new ArrayList<DataPoint>();
				for(DataPoint point : data[i]){
					int temp_count = 0;
					int[] index = point.get_index();
					for(int onef : index)
						if(deleting[onef]==0)
							temp_count ++;
					int [] new_index = new int[temp_count];
					int counting_new_index_temp = 0;
					for(int j=0;j<index.length;j++){
						int origin_ind = index[j];
						if(deleting[origin_ind]==0){
							String the_str = strings[origin_ind];
							int new_ind = new_dict.index(the_str);
							new_index[counting_new_index_temp] = new_ind;
							counting_new_index_temp++;
						}
					}
					new_data[i].add(new DataPoint(i,new_index));
				}
			}
			return new Object[]{new_data,new_dict};
		}
}
