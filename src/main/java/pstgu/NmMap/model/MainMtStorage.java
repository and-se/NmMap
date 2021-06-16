package pstgu.NmMap.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import pstgu.NmMap.model.fts.TextSearchImage;

public class MainMtStorage implements MtStorage {

  /**
   * storage хранит все загруженные жизнеописания
   */
  Map<Integer, Human> storage = new HashMap<Integer, Human>();

  public MainMtStorage(String folderPath) {
    File inputFiles = new File(folderPath);
    File[] inputFilesList = inputFiles.listFiles(f -> f.getName().endsWith(".json"));

    ObjectMapper mapper = new ObjectMapper();

    for (File file : inputFilesList) {
      try {
        Human currentHuman = mapper.readValue(file, Human.class);
        storage.put(currentHuman.getId(), currentHuman);

      } catch (IOException e) {
        System.out.println(file.getName());
        e.printStackTrace();
      }
    }

  }

  public Map<Integer, Human> getMap() {
    return storage;
  }

  @Override
  public Human getHuman(int id) {
    return storage.get(id);
  }

  @Override
  public Human[] findHumansByFio(String fioStarts, String fioContains, int skip, int take) {
    // Из storage берём только жизнеописания (значения values(), а не ключи).
    // Далее просим дать нам объект parallelStream(), который умеет фильтровать
    // данные
    // в нескольких потоках параллельно по заданным нами критериям.
    var result = storage.values().parallelStream()
        // Отбираем записи, игнорируя разницу между прописными и строчными буквами
        .filter(human ->
        // fioStarts не задано, либо ФИО начинается с fioStarts
        (fioStarts == null || human.getFio().toUpperCase().startsWith(fioStarts.toUpperCase()))
            // fioContains не задано, либо ФИО содержит fioContains
            && (fioContains == null
                || human.getTitle().toUpperCase().contains(fioContains.toUpperCase())))
        // Объединяем парелельные потоки в один
        .sequential()
        // Сортируем по ФИО
        .sorted(Comparator.comparing(human -> human.getTitle()))
        // Пропускаем skip записей, берём не более take записей
        .skip(skip).limit(take)
        // Превращаем результат в массив Human[]
        .toArray(Human[]::new);

    return result;
  }

  @Override
  public HumanTextSearchResult[] findHumansFullText(String query, int skip, int take) {   
	var words0 = query.split("[^A-Za-zА-ЯЁа-яё0-9]+");
	if(words0.length > 0 && (words0[0] == null || words0[0].equals(""))) {
		words0=Arrays.copyOfRange(words0, 1, words0.length);
	}
	if(words0.length==0){
		return new HumanTextSearchResult[0];
	}
	final var words = words0;
    /*
     * for (int i = 0; i < words.length; i++) { words[i] = words[i].replaceAll("[^А-Яа-я0-9]", "");
     * }//Разбиваем на слова, потом ( как я поняла) заменяем знаки на ничто
     */
    var result = storage.values().parallelStream().map(human -> {
      // Восстанавливаем полный текст статьи - ФИО + остальное
      var allText = human.getTitle() + "\n" + human.getArticle();

      // Строим поисковый образ текста статьи
      var textSearch = new TextSearchImage(allText, Arrays.asList(words));

      // Если текст удовлетворяет запросу, возвращаем HumanTextSearchResult
      if (textSearch.isTextFitsQuery()) {
        return new HumanTextSearchResult(human, textSearch.getTextSnippet(200, "\n"),
            textSearch.getRelevance());
      }

      // Если нет - просто null
      return null;

    })
        // Отсеиваем null, чтобы остались только подходящие тексты
        .filter(r -> r != null).sorted((a, b) -> {
          // Сортируем по убыванию (-1) релевантности
          int r = -1 * Double.compare(a.getRelevance(), b.getRelevance());
          // А если она совпадает - то по возрастанию ФИО
          if (r == 0) {
            r = a.getHuman().getTitle().compareTo(b.getHuman().getTitle());
          }
          return r;
        }).skip(skip).limit(take).toArray(HumanTextSearchResult[]::new);

    return result;
  }

  @Override
  public long countHumansByFio(String fioStarts, String fioContains) {
    return findHumansByFio(fioStarts, fioContains, 0, Integer.MAX_VALUE).length;
  }

  @Override
  public long countHumansFullText(String query) {
    return findHumansFullText(query, 0, Integer.MAX_VALUE).length;
  }

  @Override
  public ArrayList<Location> getLocations(LocationsFilter filter) {
    var result = new ArrayList<Location>();
    if (filter != null) {
      for (var human : storage.values()) {
        var locations = human.getCoordinates();
        if (locations != null) {
          for (var location : locations) {
            if (Arrays.asList(filter.getClusterTypes()).contains(location.getClusterType()))
              result.add(location);
          }
        }
      }
    } else {
      for (var human : storage.values()) {
        result.addAll(
            human.getCoordinates() != null ? human.getCoordinates() : new ArrayList<Location>());
      }
    }

    return result;
  }
}
