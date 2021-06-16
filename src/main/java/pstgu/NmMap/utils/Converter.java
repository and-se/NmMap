package pstgu.NmMap.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
  private static final String DEFAULT_CLUSTER_TYPE = "другое";
  private static final Map<String, String> clusterTypes = new HashMap<>(25) {
    private static final long serialVersionUID = 1L;

    {
      put("арест", "репрессии");
      put("духовно-образовательная деятельность", "служение");
      put("заключение", "репрессии");
      put("канонизация", "служение");
      put("кончина", "кончина");
      put("кратковременный арест", "репрессии");
      put("награда", "служение");
      put("образование", "другое");
      put("осуждение", "репрессии");
      put("погребение", "кончина");
      put("постриг", "служение");
      put("предварительное заключение", "репрессии");
      put("приговор", "репрессии");
      put("приговор внесудебного органа", "репрессии");
      put("приговор судебного органа", "репрессии");
      put("проживание", "другое");
      put("работа в церкви", "служение");
      put("работа в церкви/монастыре", "служение");
      put("раскулачивание", "репрессии");
      put("расстрел", "кончина");
      put("рождение", "другое");
      put("рукоположение", "служение");
      put("служение", "служение");
      put("смерть", "кончина");
      put("ссылка/высылка", "репрессии");
    }
  };



  public Converter() {
    var out = new PrintStream(new BufferedOutputStream(System.out, 2048));
    System.setOut(out);
  }

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
    String fio = data.get("ФИО").asText();
    String articleTitle = fio;
    JsonNode san = data.get("Сан_ЦеркСлужение");
    JsonNode sainthood = data.at("/Канонизация/Чин_святости");

    if (san != null) {
      articleTitle += ", " + san.asText();
    }

    if (sainthood != null) {
      articleTitle = sainthood.asText() + " " + articleTitle;
    }

    // result.set("fio", result.textNode(article_title));
    result.put("title", articleTitle.strip());
    result.put("fio", fio.strip());


    String biographyFacts = "";
    var locationList = new ArrayList<Location>();
    JsonNode events = data.withArray("События");
    for (JsonNode event : events) {
      var type = event.get("Тип");
      var eventDate = event.get("Датировка");
      var dates = event.withArray("Поисковые_даты");
      String startDate = null;
      String endDate = null;
      if (dates.size() > 0) {
        var date = dates.get(0);
        startDate = date.get("Начало_мин").asText();
        endDate = date.get("Окончание_макс").asText();
      }
      var text = event.get("Текст");

      var eventStr = Optional.ofNullable(eventDate).map(t -> t.asText() + " — ").orElse("")
          + Optional.ofNullable(text).map(JsonNode::asText).orElse("") + "\n";

      biographyFacts += eventStr;

      var geography = event.withArray("География");
      for (JsonNode g : geography) {
        var gps = new ArrayList<JsonNode>();
        for (JsonNode c : g.withArray("Координаты")) {
          gps.add(c);
        }

        for (int i = 0; i < gps.size(); i++) {
          var cur = gps.get(i);
          double Ni = Math.toRadians(gps.get(i).get("Широта").asDouble());
          double Ei = Math.toRadians(gps.get(i).get("Долгота").asDouble());
          for (int j = i + 1; j < gps.size(); j++) {

            double Nj = Math.toRadians(gps.get(j).get("Широта").asDouble());
            double Ej = Math.toRadians(gps.get(j).get("Долгота").asDouble());

            var centerAngle = Math.acos(Math.sin(Ni) * Math.sin(Nj)
                + Math.cos(Ni) * Math.cos(Nj) * Math.cos(Math.abs(Ei - Ej)));
            var d = 6371 * centerAngle;

            if (d < 1) {
              gps.remove(j);
              j--;
              continue;
            }
          }
        }

        for (JsonNode c : gps) {
          var N = c.get("Широта");
          var E = c.get("Долгота");
          var strType = type != null ? type.asText() : null;
          var clusterType = clusterTypes.getOrDefault(strType, DEFAULT_CLUSTER_TYPE);

          var location = new Location(N.asDouble(), E.asDouble(), strType, clusterType,
              eventDate != null ? eventDate.asText() : null, startDate, endDate, text.asText());
          locationList.add(location);
        }
      }
    }

    String comment =
        Optional.ofNullable(data.get("Комментарий")).map(JsonNode::asText).orElse("");

    var bibliography = new StringBuilder();
    JsonNode list = data.withArray("Библиография");
      int i=1;
    for (JsonNode text : list) {
      JsonNode name = text.get("Название");
      JsonNode type = text.get("Тип");
      var document = new StringBuilder();
      document.append("\n");
      
      document.append(i);
      document.append(". ");
      document.append(name.asText());
      
      
      bibliography.append(document);
      i++;
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
    System.out.println("Processing...");
    int count = 0;
    for (File inputFile : inputFiles.listFiles()) {
      if (inputFile.isFile())
        try (FileInputStream inp = new FileInputStream(inputFile)) {
          result = converter.convert(inp);

          // Выводим на экран получившийся в результате конвертации JSON
          System.out.println(result);

          // Выводим результат конвертирования в файлы
          writeToFile("resources/output/" + inputFile.getName(), result.toPrettyString());
          count++;
        }
    }

    System.out.println("### END ###");
    System.out.printf("%d files has been processing\n", count);
    System.out.flush();
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
