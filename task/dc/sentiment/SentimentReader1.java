package task.dc.sentiment;

import java.util.*;
import task.dc.*;
import text.*;
import java.io.*;

public class SentimentReader1 extends DCCorpusReader{
	static String classes_fname = "c";
	
	public void Error(String x){
		throw new RuntimeException(x);
	}
	public List<Paragraph>[] read_corpus(String f){
		//specified file and dir format
		int classes = 0;
		File folder = new File(f);
		if(!folder.exists() || !folder.isDirectory())
			Error("No dir "+f);
		
		//how many classes
		File[] files = folder.listFiles();
		HashMap<String,File> fnames = new HashMap<String,File>();
		for(File ff : files)
			fnames.put(ff.getName(),ff);
		for(classes=0;;classes++){
			String name = classes_fname+classes;
			if(!fnames.containsKey(name))
				break;
		}
		if(classes < 2)	//no meaning for classification
			Error("Classes < 2.");
		
		//for all those classes
		List<Paragraph>[] ret = new List[classes];
		for(int i=0;i<classes;i++){
			ret[i] = new ArrayList<Paragraph>();
			String name = classes_fname+i;
			File ff = fnames.get(name);
			if(!ff.exists() || !ff.isDirectory())
				Error("No subdir "+ff.toString());
			for(File subf : ff.listFiles())
				ret[i].add(new Paragraph(subf));
		}
		return ret;
	}
}
