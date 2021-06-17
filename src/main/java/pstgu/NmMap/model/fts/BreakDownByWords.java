package pstgu.NmMap.model.fts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.util.Pair;

public class BreakDownByWords {
	public void procces (String text) {
		
		Pattern pattern = Pattern.compile("\\b[а-яА-ЯЁёA-Za-z0-9]+\\b");
		Matcher matcher = pattern.matcher(text);
		int s;
		int e;
		//String kl;
	    while(matcher.find()) {
	    //if(matcher.find()) {
	    	   s=matcher.start();
	    	   e=matcher.end();	    	   
	    	   word_event(text.substring(s, e) ,s, e);
	      }	
	}

	protected void word_event(String word, int start, int end) {
		 System.out.printf("<%d, %d> %s\n", start, end, word);
	}
	

	public static void main(String[] args) {
		var ww = new BreakDownByWords();		
		ww.procces("У Лукоморья дуб зелёный\nЗлатая цепь,,,,,на дубе том!!!");
	}
}