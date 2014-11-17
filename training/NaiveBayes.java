package training;

import java.util.*;
import java.io.*;

public class NaiveBayes extends Mach{
	//multinomial naive bayes with simple add-one smoothing
	int classes;	//from 0 to ...
	int v_size;		//number of words
	double log_pcw[][];	//logs[C][V]
	double log_pc[];		//pc[C]
	
	public NaiveBayes(File filename){
		super(0);
		try{
			FileInputStream in = new FileInputStream(filename);
			Scanner sin = new Scanner(in);
			if(sin.next() != NAMES[mach_id]){
				sin.close();
				throw new RuntimeException("Wrong mach file, should be naiveb.");
			}
			classes = sin.nextInt();
			v_size = sin.nextInt();
			log_pcw = new double[classes][v_size];
			log_pc = new double[classes];
			for(int i=0;i<classes;i++){
				log_pc[i] = sin.nextDouble();
			}
			for(int i=0;i<classes;i++)
				for(int j=0;j<v_size;j++)
					log_pcw[i][j] = sin.nextDouble();
			sin.close();
		}catch (FileNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException("File not exist: "+filename);
		}
	}
	
	public NaiveBayes(int c,int v,List<int[]>[] index){
		super(0);
		//index should be of size c
		classes = c;
		v_size = v;
		log_pcw = new double[classes][v_size];
		log_pc = new double[classes];
		int pc_normalize = 0;
		for(int i=0;i<c;i++){
			List<int[]> t = index[i];
			log_pc[i] = t.size();	//temperary for counts
			pc_normalize += log_pc[i];
			//count for log_pcw[i]
			int pcw_count = v_size;
			for(int j=0;j<v_size;j++)
				log_pcw[i][j] = 1;	//smoothing
			for(int[] item : t){
				//HashSet<Integer> no_rep = new HashSet<Integer>(item);	//not here
				pcw_count += item.length;
				for(int ind : item)
					log_pcw[i][ind] ++;
			}
			//normalize and log
			for(int n=0;n<v_size;n++)
				log_pcw[i][n] = Math.log(log_pcw[i][n]/pcw_count);
		}
		for(int i=0;i<c;i++){
			log_pc[i] = Math.log(log_pc[i]/pc_normalize);
		}
	}
	
	public double evaluate(int[]index,double[]f){
		//no check here --- here f can be null
		double []values = new double[classes];
		for(int i=0;i<classes;i++){
			values[i] = log_pc[i];
			for(int ind : index)
				values[i] += log_pcw[i][ind];
		}
		int max_item = 0;
		double max_value = values[0];
		for(int i=1;i<classes;i++){
			if(values[i] > max_value){
				max_value = values[i];
				max_item = i;
			}
		}
		return max_item;
	}
	
	public List<Double> evaluate(List<int[]> i,List<double[]> f){
		List<Double> ret = new ArrayList<Double>();
		for(int[] index : i){
			ret.add(evaluate(index,null));
		}
		return ret;
	}
	
	public void write(String filename){
		try{
			FileOutputStream out = new FileOutputStream(filename);
			PrintStream p = new PrintStream(out);
			p.println(NAMES[mach_id]);
			p.printf("%d %d\n",classes,v_size);
			for(int i=0;i<classes;i++){
				p.printf("%f ",log_pc[i]);
				p.println();
			}
			for(int i=0;i<classes;i++){
				for(double one : log_pcw[i])
					p.printf("%f ",one);
				p.println();
			}
			p.close();
		}catch (FileNotFoundException e){
            e.printStackTrace();
        }
	}
}
