/**
 * Модель жизнеописания.
 */
public class Human {
    private int id;
    private String fio;
    private String article;
        
    /**
     * Создаёт новый объект жизнеописания
     * @param id ключ
     * @param fio заголовок статьи
     * @param article текст статьи
     */
    public Human(int id, String fio, String article) {
      this.id = id;
      this.fio = fio;
      this.article = article;
    }

    public int getId() {
      return id;
    }
    
    public String getFio() {
      return fio;
    }
    
    public String getArticle() {
      return article;
    }    
}