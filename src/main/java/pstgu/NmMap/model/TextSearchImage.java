package pstgu.NmMap.model;
import java.util.Arrays;
import org.springframework.data.util.Pair;
import java.util.*;
import pstgu.NmMap.StemmerPorterRU;

/**
 * Поисковый образ текста. Знает, где в тексте находятся искомые слова и умеет выдавать вырезку из
 * текста, в которой встречаются все искомые слова. Также умеет оценивать релевантность текста
 * запросу, т.е. степень "полезности" совпадения.
 * 
 */
public class TextSearchImage {
  // текст, в котором ищем
  String text;

  // слова, которые ищем
  List<String> words;

  /**
   * словарь, где каждому искомому слову сопоставлен его индекс в тексте. если искомого слова в
   * тексте нет, то и в словаре нет записи про это слово.
   */
  Map<String, Pair<Integer, Integer>> index = new HashMap<String, Pair<Integer, Integer>>();

  /**
   * Строит поисковый образ текста.
   * 
   * @param text текст, в котором ищем
   * @param searchWords слова, которые ищем
   */
  public TextSearchImage(String text, Collection<String> searchWords) {
    this.text = text;
    this.words = new ArrayList<String>(searchWords);

    // строим индекс
    fillIndex();
  }

  /**
   * Возвращает значение, показывающее, подходит ли текст под запрос, т.е. найдены ли в нём все
   * искомые слова.
   */
  public boolean isTextFitsQuery() {
    return index.size() == words.size() && words.size() != 0;
  }

  /**
   * Оценка степени полезности текста для данного запроса. Чем больше, тем лучше. Поисковую выдачу
   * полезно сортировать по убыванию релевантности, чтобы в начале оказывались более полезные
   * совпадения. Если текст не удовлетворяет запросу - 0.
   * 
   * @return
   */
  public double getRelevance() {

    if (!isTextFitsQuery()) {
      return 0;
    }

    // если слово одно, оцениваем степень близости его к началу текста
    if (words.size() == 1)
      return (double) text.length()/index.values().stream().map(p -> p.getFirst()).toArray(Integer[]::new)[0];

    int start = index.values().stream().map(x -> x.getFirst()).min(Comparator.naturalOrder()).get();
    int end = index.values().stream().map(x -> x.getFirst()).max(Comparator.naturalOrder()).get();

    // Оценивать релевантность предлагается исходя из следующей эвристики:
    // совпадение лучше, когда искомые слова находятся недалеко друг друга, а не на
    // разных концах
    // текста.

    // Посему:
    // нам нужно взять расстояние между первым и последним совпадением в тексте.
    // А потом разделить единицу на это число.
    // Тем самым получим, что для радом стоящих слов делитель будет не очень большой
    // и релевантность
    // ближе к 1,
    // а для далеко разнесённых слов наоборот - ближе к 0.
    // Только надо не забыть про случай, когда искомое слово всего одно...
    return 1. / (end - start);
  }

  /**
   * Возвращает вырезку из текста, которая содержит все искомые слова. Сами искомые слова
   * заключаются в < >. Если текст не удовлетворяет запросу, возвращает null
   * 
   * @param maxLength максимальная длина вырезки
   * @return строка, в которой искомые слова заключены в угловые скобки &lt;слово&gt;
   */
  public String getTextSnippet(int maxLength) {
    return getTextSnippet(maxLength, ".!?");
  }
  
  
  /**
     Возвращает вырезку из текста, которая содержит все искомые слова. Сами искомые слова
   * заключаются в < >. Если текст не удовлетворяет запросу, возвращает null
   * 
   * @param maxLength максимальная длина вырезки
   * @param sentenceSeparator разделительные знаки
   * @return строка, в которой искомые слова заключены в угловые скобки &lt;слово&gt;

   */
  public String getTextSnippet(int maxLength, String sentenceSeparator) {
    if (!isTextFitsQuery()) {
      return null;
    }
    
    int start = index.values().stream().map(x -> x.getFirst()).min(Comparator.naturalOrder()).get();
    int end = index.values().stream().map(x -> x.getSecond()).max(Comparator.naturalOrder()).get();
    
    for(; start > 0; start--) {
      if(sentenceSeparator.indexOf(text.charAt(start))!=-1) {
       start ++;
       break;
      }
    }
    
    for(; end<text.length(); end++ ) {
      if(sentenceSeparator.indexOf(text.charAt(end))!=-1) {
        break;
      }
    }
    
    if (end < text.length()) {
      end++;
    }

    var result = text.substring(start, end);
    final int start2= start;
    ///
    int[] index1 = index.values().stream().map(x -> x.getFirst()).mapToInt(x->x-start2).sorted().toArray();
    int[] index2 = index.values().stream().map(x -> x.getSecond()).mapToInt(x->x-start2).sorted().toArray();
    
    String result1=result.substring(0, index1[0]);
    int i;
    for(i=0; i<index1.length-1; i++) {
      result1 +="<mark>"+result.substring(index1[i], index2[i]+1)+"</mark>" + 
                result.substring(index2[i]+1, index1[i+1]);
    }
    result1+="<mark>"+result.substring(index1[i], index2[i]+1)+"</mark>" + 
        result.substring(index2[i]+1);
    ///
    if(result1.length()>maxLength) {
      result1 = result1.substring(0,maxLength-6)+" <...>";
    }
   
    return result1;
  }
    
  int endWord(String allText, String findWord) {
    ///char[] strToArray = allText.toCharArray();
    int i=allText.indexOf(findWord);
    while(i < allText.length() && ".,!?;: \n".indexOf(allText.charAt(i)) == -1)
      {
      i++;
      
    }
    
   return i-1;
  }

  private void fillIndex() {
    String t1 = text.toLowerCase().replace('ё', 'е');
    for (var word : words) {
      // берем основу слова с помощью стеммера
      word = StemmerPorterRU.stem(word);
      // поиск по подстроке работает,т.к. стеммер всего лишь обрезает слово.
      if (t1.contains(word)) {
        var p = Pair.of(t1.indexOf(word), endWord(t1,word));
        index.put(word, p);
        System.out.printf("%s s=%d e=%d\n", word, p.getFirst(), p.getSecond());
        // Здесь нужно для каждого слова найти его положение в тексте и положить эту
        // информацию
        // в словарь index - ключом выступает слово, а значением - индекс символа, с
        // которого оно
        // начинается.
      }
    }
  }

  
  ///// TEST /////
  public static void main(String[] args) {
    var q1 = new String[] {"зелёный", "дуб"};
    var q2 = new String[] {"Лукоморья", "Пушкин"};

    // Здесь должно быть isTextSatisfiesQuery=true, а также ненулевая релевантность
    // и вырезка текста
    String testText = "У! Лукоморья дуб. зелёный";
    var t = new TextSearchImage(testText, Arrays.asList(q1));
    System.out.println(testText);
    System.out.printf("isTextSatisfiesQuery=%b\tRelevance=%f\n", t.isTextFitsQuery(),
        t.getRelevance());
    System.out.println(t.getTextSnippet(100,"\n"));
    System.out.println(t.getTextSnippet(15));

    // А здесь должно быть isTextSatisfiesQuery=false, так что релевантность 0 и
    // вырезка null
    t = new TextSearchImage(testText, Arrays.asList(q2));
    System.out.printf("isTextSatisfiesQuery=%b\tRelevance=%f\n", t.isTextFitsQuery(),
        t.getRelevance());
    System.out.println(t.getTextSnippet(100));

    t = new TextSearchImage(testText, Arrays.asList("дуб"));
    System.out.printf("isTextSatisfiesQuery=%b\tRelevance=%f\n", t.isTextFitsQuery(),
        t.getRelevance());
    System.out.println(t.getTextSnippet(100));

    t = new TextSearchImage(testText, new ArrayList<String>());
    System.out.printf("isTextSatisfiesQuery=%b\tRelevance=%f\n", t.isTextFitsQuery(),
        t.getRelevance());
    System.out.println(t.getTextSnippet(100));

  }
}
