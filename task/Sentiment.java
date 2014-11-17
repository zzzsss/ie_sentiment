package task;

import java.io.*;
import java.util.*;

import text.*;

public class Sentiment {
	
}

class OneClass{
	//one class of data
	List<Paragraph> data;
	
	//---------------------------
	OneClass(String folder){
		//one folder of files --- each file one paragraph
		File f = new File(folder);
		if(!f.isDirectory()){	//fault
		}
		File [] files = f.listFiles();
		for(File onepiece : files){
			try{
				FileInputStream in = new FileInputStream(onepiece);
				BufferedReader dr=new BufferedReader(new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				String line =  dr.readLine();
				while(line!= null){ 
					str.append(line);
					System.out.println(line);   
					line = dr.readLine();
				}
				data.add(new Paragraph(str.toString()));
				dr.close();
			}catch (Exception e){
	            e.printStackTrace();
			}
		}
		for(Paragraph p : data)
			p.negation();
	}
	void add_words(Dict dict){
		
	}
	List<List<Integer>> get_index_norep(Dict dict){
		//get bag-of-words feature
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		return ret;
	}
}