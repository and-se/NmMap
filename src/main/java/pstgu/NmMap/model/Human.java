package pstgu.NmMap.model;

import java.util.List;

/**
 * Модель жизнеописания.
 */
public class Human {
	private int id;
	private String fio;
	private String title;
	private String text;
	private String bibliography;

	List<Location> coordinates;

	public Human() {
	}

	/**
	 * Создаёт новый объект жизнеописания.
	 * 
	 * @param id          ключ
	 * @param title       заголовок статьи
	 * @param article     текст статьи
	 * @param coordinates координаты для карты
	 */
	public Human(int id, String title, String text, String bibliography, List<Location> coordinates) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.bibliography = bibliography;

		for (Location location : coordinates) {
			location.setHuman(this);
		}
		this.coordinates = coordinates;
	}

	public int getId() {
		return id;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getTitle() {
		return title;
	}
	
	public String getText() {
		return text;
	}
	
	public String getBibliography() {
		return bibliography;
	}

	public List<Location> getCoordinates() {
		return coordinates;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String fio) {
		this.title = fio;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setBibliography(String bibliography) {
		this.bibliography = bibliography;
	}

	
	public void setCoordinates(List<Location> coordinates) {
		for (Location location : coordinates) {
			location.setHuman(this);
		}
		this.coordinates = coordinates;
	}

	@Override
	public String toString() {
		return String.format("id %d\n%s\n\n%s", id, title, text + "\n**" + bibliography);
	}
}
