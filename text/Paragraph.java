package text;

import java.util.*;

public class Paragraph {
	List<Sentence> sents;
	public Paragraph(String p){
		
	}
	
	public void negation(){
		for(Sentence s : sents)
			s.negation();
	}
}
