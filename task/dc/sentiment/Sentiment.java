package task.dc.sentiment;

import java.io.*;

import nlpir.NLPIR.CLibrary;
import task.dc.*;
import text.*;
import training.*;

public class Sentiment {
	static int NB=0,SVMC=1,SVMR=2,SVM_LI=3;
	
	static DCCorpusReader r = new SentimentReader2();
	//static DCFeatureGenerator f = new SimpleFeatureSelection(new SentimentFeature1(),1,10000);
	//static DCFeatureGenerator f = new SentimentFeature_try();
	static DCFeatureGenerator f = new SentimentFeatureSpecial();
	static Mach m;
	static Dict d;
	static DCClassifyTask task;
	static{
		Tools.init(false,false);
		for(String x : DictSentiment.dict_sentiment.get_bunch(DictSentiment.ALL_SET))
			CLibrary.Instance.NLPIR_AddUserWord(x);
	}

	//init the task
	static void init_task(int mach){
		if(mach == NB)
			m = new NaiveBayes();
		else
			m = new LibsvmCommon(mach-1,false);
		task = new DCClassifyTask(r,f,m);
	}
	static void init_task(String mach,String dict){
		m = Mach.read(new File(mach));
		d = new Dict(new File(dict));
		task = new DCClassifyTask(r,f,m,d);
	}

	public static void main(String[] x) throws Exception{
		init_task(3);
		//task.train_cv("data/t_correct/data2_origin.obj", "testing2/nothing");
		task.train_part("data/t_correct/data2_origin.obj", "testing2/nothing");
		return;
		/*
		init_task("testing2/f1_n.mach","testing2/f1_n.fdict");
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
        */
	}
}
