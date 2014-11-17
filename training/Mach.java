package training;

import java.util.*;
import java.io.*;

public abstract class Mach {
	//data format
	//double[] values; int[] feature_index; double[] feature_values
	static final String[] NAMES = {"naiveb","svm"};
	
	protected int mach_id;	//mach_id
	protected Mach(int id){
		mach_id = id;
	}
	public abstract double evaluate(int[]i,double[]f);
	public abstract List<Double> evaluate(List<int[]> i,List<double[]> f);
	public abstract void write(String filename);
	
	public static Mach read(File f){
		Mach ret = null;
		try{
			FileInputStream in = new FileInputStream(f);
			BufferedReader dr = new BufferedReader(new InputStreamReader(in));
			String line =  dr.readLine();
			dr.close();
			if(line!= null){ 
				for(int i=0;i<NAMES.length;i++){
					if(line.indexOf(NAMES[i]) > -1){
						switch(i){
						case 0:
							ret = new NaiveBayes(f);
							break;
						case 1:
							ret = new LibsvmInterface(f);
							break;
						}
						break;
					}
				}
			}
		}catch (Exception e){
            e.printStackTrace();
		}
		return ret;
	}
	
}
