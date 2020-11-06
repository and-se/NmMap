package pstgu.NmMap.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pstgu.NmMap.model.Location;

/**
 * Пример конвертации предоставленных JSON-данных в формат, эквивалентный классу Human.
 */
public class Converter {
  /**
   * Выполняет конвертацию.
   * 
   * @param datareader поток для чтения входных данных
   * @return сконвертированные данные в формате JSON
   * @throws IOException если что-то с чтением пошло не так
   */
  public ObjectNode convert(InputStream datareader) throws IOException {
    // Чтение данных в дерево data
    var rdr = new ObjectMapper();
    JsonNode data = rdr.readTree(datareader);

    // Создаём пустое дерево result
    var wrtr = new ObjectMapper();
    ObjectNode result = wrtr.createObjectNode();

    // Копируем ключ записи
    result.set("id", data.get("Номер"));

    // @Заголовок = Канонизация.Чин_святости + ФИО + Сан_ЦеркСлужение
    // Пробуем читать все необходимые поля и собираем из них итоговый текст
    String articleTitle = data.get("ФИО").asText();
    JsonNode san = data.get("Сан_ЦеркСлужение");
    JsonNode sainthood = data.at("/Канонизация/Чин_святости");

    if (san != null) {
      articleTitle += ", " + san.asText();
    }

    if (sainthood != null) {
      articleTitle = sainthood.asText() + " " + articleTitle;
    }

    // result.set("fio", result.textNode(article_title));
    result.put("fio", articleTitle.strip());

    // TODO Написать код конвертации остальных полей

    String biographyFacts = "";
    var locationList = new ArrayList<Location>();
    JsonNode events = data.withArray("События");
    for (JsonNode event : events) {
      var date = event.get("Датировка");
      var text = event.get("Текст");

      var eventStr =
          Optional.ofNullable(date).map(t -> t.asText() + " — ").orElse("") + text.asText() + "\n";

      /*
       * String eventStr = Optional.ofNullable(day).map(n -> month.asText()).map(n -> year.asText())
       * .map(n -> day.asText() + "." + month.asText() + "." + year.asText() + " — ")
       * .orElse(Optional.ofNullable(date).map(n -> n.asText() + " — ").orElse("? — ")) +
       * Optional.ofNullable(text).map(t -> t.asText()).orElse("") + "\n";
       */
      biographyFacts += eventStr;

      var geography = event.withArray("География");
      for (JsonNode g : geography) {
        var gps = g.withArray("Координаты");
        for (JsonNode c : gps) {
          var N = c.get("Широта");
          var E = c.get("Долгота");
  
          var location = new Location(N.asDouble(), E.asDouble(), date!=null ? date.asText() : null, text.asText());
          locationList.add(location);
        }
      }
    }

    String comment =
        Optional.ofNullable(data.get("Комментарий")).map(JsonNode::asText).orElse("") + "\n";

    String bibliography = "**Библиография:\n";
    JsonNode list = data.withArray("Библиография");
    for (JsonNode text : list) {
      JsonNode name = text.get("Название");
      JsonNode type = text.get("Тип");
      String document = text.get("NUM").asText() + ". \"" + name.asText() + "\" ("
          + Optional.ofNullable(type).map(JsonNode::asText).orElse("документ") + ")\n";
      bibliography += document;
    }

    result.put("article", biographyFacts + comment + bibliography);
    // System.out.println(biographyFacts+comment+bibliography);

    // Преобразуем список объектов Location в подходящий формат и записываем его в ObjectNode result
    ArrayNode coordinates = wrtr.valueToTree(locationList);
    result.putArray("coordinates").addAll(coordinates);


    return result;

  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    // Создаём конвертер - по совместительству это наш текущий класс
    Converter converter = new Converter();
    ObjectNode result;

    // Для каждого файла в каталоге открываем поток чтения жизнеописания и передаём
    // конвертеру.
    // Блок try автоматически закроет файл при окончании работы
    File inputFiles = new File("resources/input");
    for (File inputFile : inputFiles.listFiles()) {
      if (inputFile.isFile())
        try (FileInputStream inp = new FileInputStream(inputFile)) {
          result = converter.convert(inp);

          // Выводим на экран получившийся в результате конвертации JSON
          System.out.println(result);

          // Выводим результат конвертирования в файлы
          writeToFile("resources/output/" + inputFile.getName(), result.toPrettyString());
        }
    }

    // System.out.println(result.toPrettyString());
  }

  /**
   * Записывает в файл строку <b>text</b>
   * 
   * @param path - путь относительно папки проекта, напр. "resources/output/out.txt"
   * @param text - строка для записи в файл
   */
  static void writeToFile(String path, String text) {
    try (FileWriter writer = new FileWriter(path, false)) {
      writer.write(text);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
