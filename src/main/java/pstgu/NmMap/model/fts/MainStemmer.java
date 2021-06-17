package pstgu.NmMap.model.fts;

import java.util.Scanner;

public class MainStemmer {
	public static String stem_word(String word) {
		if(word.length()>=3 ) {
			// стеммер заменяет ё --> е
			 var stemmed = StemmerPorterRU.stem(word); 
		     if(stemmed.length()>3) {
		    	 word = stemmed;
		     }
		     else {
		    	 word =  word.substring(0,3).toLowerCase();
		     }
		}
		
		return word;
	}
	
	public static void main(String[] args) {	    
	    Scanner cin = new Scanner(System.in);
	    String word ="";
	    while(!word.equals("exit")) {
	      System.out.println("input word or 'exit': ");
	      word = cin.nextLine();
	      System.out.println(MainStemmer.stem_word(word));
	    }
	   }
}
