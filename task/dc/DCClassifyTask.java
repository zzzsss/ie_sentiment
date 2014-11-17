package task.dc;

import text.*;
import training.*;
import java.util.*;

public class DCClassifyTask {
	static String FNAME_DICT = ".fdict";
	static String FNAME_MACH = ".mach";
	static String FNAME_FOLD = ".fold";
	static int FOLDS = 3;
	
	DCCorpusReader reader;
	DCFeatureGenerator feature_gen;
	Mach trainer;
	Dict feature_dict;
	
	private void self_train_onepiece(List<Paragraph>[] input_data,String name,boolean write){
		Object[] data = feature_gen.get_datapoints(input_data, null);
		feature_dict = (Dict)(data[1]);
		List<DataPoint>[] dps = (List<DataPoint>[])(data[0]);
		trainer.train(feature_dict.get_length(), dps ,name);
		if(write){
			feature_dict.write(name+FNAME_DICT);
			trainer.write(name+FNAME_MACH);
		}
	}
	private List<Double>[] self_test_onepiece(List<Paragraph>[] input_data){
		Object[] data = feature_gen.get_datapoints(input_data, feature_dict);
		List<DataPoint>[] dps = (List<DataPoint>[])(data[0]);
		List<Double>[] ret = new List[input_data.length];
		for(int i=0;i<dps.length;i++)
			ret[i] = trainer.evaluate(dps[i]);
		return ret;
	}
	private double self_test_accuracy(List<Paragraph>[] input_data,List<Double>[] result){
		//average accuracy
		int all = 0;
		int right = 0;
		for(int i=0;i<input_data.length;i++){
			all += result[i].size();
			for(double d : result[i]){
				if(i == (int)(d+0.5))	//rounding here also suitable for regression svm
					right++;
			}
		}
		return ((double)right)/all;
	}
	
	//-----------------------
	public DCClassifyTask(DCCorpusReader r,DCFeatureGenerator f,Mach t,Dict x){
		reader = r;
		feature_gen = f;
		trainer = t;
		feature_dict = x;
	}
	public DCClassifyTask(DCCorpusReader r,DCFeatureGenerator f,Mach t){
		reader = r;
		feature_gen = f;
		trainer = t;
		feature_dict = null;
	}
	
	public void train_all(String data_loc,String name){
		System.out.println("Training all data of "+data_loc);
		List<Paragraph>[] input_data = reader.read_corpus(data_loc);
		self_train_onepiece(input_data,name,true);
	}
	public void train_cv(String data_loc,String name){
		double all_acc = 0.0;
		System.out.println("Training data of "+data_loc+" in "+FOLDS+" folds.");
		List<Paragraph>[] input_data = reader.read_corpus(data_loc);
		List<Paragraph>[][] split_data = new List[input_data.length][FOLDS];
		for(int i=0;i<input_data.length;i++){
			List<Paragraph> to_be_splited = input_data[i];
			int start = 0,step = to_be_splited.size()/FOLDS;
			for(int piece = 0;piece < FOLDS-1;piece++){
				split_data[i][piece] = new ArrayList<Paragraph>(input_data[i].subList(start,start+step));
				start += step;
			}
			split_data[i][FOLDS-1] = new ArrayList<Paragraph>(input_data[i].subList(start,input_data[i].size()));
		}
		//training those folds
		for(int i=0;i<FOLDS;i++){
			System.out.println("Held-out fold "+i);
			List<Paragraph>[] held_d = new List[input_data.length];
			List<Paragraph>[] train_d = new List[input_data.length];
			for(int c=0;c<input_data.length;c++){
				held_d[c] = new ArrayList<Paragraph>();
				train_d[c] = new ArrayList<Paragraph>();
				for(int f=0;f<FOLDS;f++){
					if(f==i)
						held_d[c].addAll(split_data[c][f]);
					else
						train_d[c].addAll(split_data[c][f]);
				}
			}
			//train
			self_train_onepiece(train_d,(name+FNAME_FOLD+i),false);
			double acc = self_test_accuracy(held_d,self_test_onepiece(held_d));
			System.out.println("--Accuracy for fold "+i+" is "+acc);
			all_acc += acc;
		}
		System.out.println("Final average accuracy is "+all_acc/FOLDS);
	}
	public void test_all(String data_loc,String name){
		System.out.println("Testing all data of "+data_loc);
		List<Paragraph>[] input_data = reader.read_corpus(data_loc);
		double acc = self_test_accuracy(input_data,self_test_onepiece(input_data));
		System.out.println("The accuracy is "+acc);
	}
	//special one
	public double test_one(String str){
		Paragraph p = new Paragraph(str);
		List<Paragraph> pl = new ArrayList<Paragraph>();
		pl.add(p);
		List<Double>[] ret = self_test_onepiece(new List[]{pl});
		double result = ret[0].get(0);
		return result;
	}
}