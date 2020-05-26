package pstgu.NmMap.model;

/**
 * 
 * Результат полнотекстового поиска жизнеописания
 */
public class HumanTextSearchResult {
  Human human;
  String textSnippet;
  double relevance;
  
  /**
   * 
   * @param human жизнеописание
   * @param textSnippet часть текста жизнеописания, содержащая искомые слова
   * @param relevance степень полезности совпадения
   */
  public HumanTextSearchResult(Human human, String textSnippet, double relevance) {
    this.human = human;
    this.textSnippet = textSnippet;
    this.relevance = relevance;
  }

  /**
   * @return Возвращает жизнеописание.
   */
  public Human getHuman() {
    return human;
  }

  /**
   * @return Возвращает вырезку текста жизнеописания, объясняющую совпадение с запросом.
   */
  public String getTextSnippet() {
    return textSnippet;
  }

  /**
   * @return Возвращает степень полезности совпадения.
   */
  public double getRelevance() {
    return relevance;
  }  
  
  
}
