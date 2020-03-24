package pstgu.NmMap.application;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MtSimpleStorage implements MtStorage {

  /**
   * storage хранит все загруженные жизнеописания
   */
  Map<Integer, Human> storage = new HashMap<Integer, Human>();

  public MtSimpleStorage(String folderPath) {
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

  @Override
  public Human getHuman(int id) {
    return storage.get(id);
  }

  @Override
  public Human[] findHumansByFio(String fioStarts, String fioContains, int skip, int take) {
    // Из storage берём только жизнеописания (значения values(), а не ключи).
    // Далее просим дать нам объект stream(), который умеет фильтровать данные
    // по заданным нами критериям.
    var result = storage.values().stream()
        // Отбираем записи
        .filter(human ->
                // fioStarts не задано, либо ФИО начинается с fioStarts
               (fioStarts == null || human.getFio().startsWith(fioStarts))
               // fioContains не задано, либо ФИО начинается с fioContains
            && (fioContains == null || human.getFio().contains(fioContains)))
        // Сортируем по ФИО
        .sorted(Comparator.comparing(human -> human.getFio()))
        // Пропускаем skip записей, берём не более take записей
        .skip(skip).limit(take)
        // Превращаем результат в массив Human[]
        .toArray(Human[]::new);

    return result;
  }

  @Override
  public Human[] findHumansFullText(String query, int skip, int take) {
    // Разбиваем на слова TODO игнорирование пунктуации
    var words = query.split("\\s");
    
    var result = storage.values().stream().filter(human -> {
      // Восстанавливаем полный текст статьи - ФИО + остальное
      var all_text = human.getFio() + "\n" + human.getArticle();      
      // Нужно, чтобы все слова из запроса содержались в полном тексте
      return Arrays.stream(words).allMatch(word -> all_text.contains(word));
    })
    .sorted(Comparator.comparing(human -> human.getFio()))
    .skip(skip).limit(take).toArray(Human[]::new);
    
    return result;
  }
}
