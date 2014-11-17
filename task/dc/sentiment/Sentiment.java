package task.dc.sentiment;

import java.io.*;
import java.util.*;

import task.dc.*;
import text.*;
import training.*;

public class Sentiment {
	static int NB=0,SVMC=1,SVMR=2;
	
	static DCCorpusReader r = new SentimentReader1();
	static DCFeatureGenerator f = new SentimentFeature1();
	static Mach m;
	static Dict d;
	static DCClassifyTask task;
	static{
		Tools.init(false,false);
	}

	//init the task
	static void init_task(int mach){
		if(mach == NB)
			m = new NaiveBayes();
		else
			m = new LibsvmCommon(mach-1);
		task = new DCClassifyTask(r,f,m);
	}
	static void init_task(String mach,String dict){
		m = Mach.read(new File(mach));
		d = new Dict(new File(dict));
		task = new DCClassifyTask(r,f,m,d);
	}

	public static void main(String[] x) throws Exception{
		/*
		init_task(0);
		task.train_cv("data/t1", "testing/t1n");
		task.train_all("data/t1", "testing/t1n");
		*/
		/*
		init_task(1);
		task.train_cv("data/t1", "testing/t1c");
		task.train_all("data/t1", "testing/t1c");
		*/
		
		
		init_task("testing/t1n.mach","testing/t1n.fdict");
		//init_task("testing/t1c.mach","testing/t1c.fdict");
		BufferedInputStream i = new BufferedInputStream(System.in);
        BufferedReader br = new BufferedReader(new InputStreamReader(i));
        do{
        	System.out.print("Enter sentence: ");
        	String line = br.readLine();
        	if(line == null)
        		break;
        	double ret = task.test_one(line);
        	System.out.println("Result is "+ret);
        }while(true);
        
		
		
		
	}
}
