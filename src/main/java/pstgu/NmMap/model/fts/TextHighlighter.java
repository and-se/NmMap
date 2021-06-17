package pstgu.NmMap.model.fts;

import java.util.HashSet;

import org.springframework.web.util.HtmlUtils;

public class TextHighlighter extends BreakDownByWords{
	StringBuilder result;
	HashSet<String> search_words;
	
	int start_copy = 0;
	String article;

	
	public String highlight(String query, String article){
		this.reset();
		this.article = article;
		
		for (var word: query.split("[^A-Za-zА-ЯЁа-яё0-9]+"))
		{
			this.search_words.add(MainStemmer.stem_word(word));
		}		
		
		this.procces(article);
		
		
		this.result.append(article.substring(start_copy));
	
		return this.result.toString();
	}


	
	@Override
	protected void word_event(String word, int start, int end) {
		var word2 = word.toLowerCase().replace('ё', 'е');
		if (this.matches_search_word(word2))
		{
			result.append(
					HtmlUtils.htmlEscape(article.substring(this.start_copy, start))
					);
			result.append("<mark>" + HtmlUtils.htmlEscape(word) + "</mark>");
			this.start_copy = end;
		}
		//if word2 in this.search_words:
			//result += previous_chars + <mark> + word + </mark> 
	}
	

	public static void main(String[] args) {
		String article = "У Лукоморья дуб зелёный\nЗлатая цепь,,,,,на дубе том!!!";
		var ww = new TextHighlighter();		 
		String query = "дуб";
		// ww.procces(article);
		System.out.println(ww.highlight(query,article));
		
		article = "Дуб в начале, в конце тоже дуба";
		System.out.println(ww.highlight(query,article));
		
		
		article = "1894 — Родился в с. Матренки Киевского уезда Киевской губ. в крестьянской семье.";
		System.out.println(ww.highlight("киев", article));
	}
	
	private boolean matches_search_word(String word)
	{
		for (var sw: this.search_words)
		{
			if (word.startsWith(sw)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void reset()
	{
		start_copy = 0;
		article = null;
		result = new StringBuilder();
		search_words = new HashSet<String>();
	}

}
