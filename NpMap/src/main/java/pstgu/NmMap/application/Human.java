package pstgu.NmMap.application;

import java.util.List;

/**
 * Модель жизнеописания.
 */
public class Human {
	private int id;
	private String fio;
	private String article;

	List<Location> coordinates;

	public Human() {
	}

	/**
	 * Создаёт новый объект жизнеописания
	 * 
	 * @param id      ключ
	 * @param fio     заголовок статьи
	 * @param article текст статьи
	 */
	public Human(int id, String fio, String article) {
		this.id = id;
		this.fio = fio;
		this.article = article;
	}

	public Human(int id, String fio, String article, List<Location> coordinates) {
		this(id, fio, article);
		this.coordinates = coordinates;
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

	public List<Location> getCoordinates() {
		return coordinates;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public void setCoordinates(List<Location> coordinates) {
		this.coordinates = coordinates;
	}
}