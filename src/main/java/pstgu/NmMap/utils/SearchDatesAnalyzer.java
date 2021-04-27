package pstgu.NmMap.utils;

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

public class SearchDatesAnalyzer {



  public String analyze(InputStream datareader) throws IOException {
    // Чтение данных в дерево data
    var rdr = new ObjectMapper();
    JsonNode data = rdr.readTree(datareader);

    var strEvents = new StringBuilder();

    JsonNode events = data.withArray("События");
    for (JsonNode event : events) {
      boolean save = false;
      var buffer = new StringBuilder();

      var date = event.get("Датировка");
      buffer.append("***Датировка: ").append(date != null ? date.asText() : null).append("\n");

      var dates = event.withArray("Поисковые_даты");
      if (dates.size() == 2) {
        var date0 = dates.get(0);
        var num0 = date0.get("NUM").asInt();
        var len0 = date0.get("Длина_интервала").asInt();
        buffer.append("Поисковая дата: ").append(num0).append(" - ").append(date0).append("\n");

        var date1 = dates.get(1);
        var num1 = date1.get("NUM").asInt();
        var len1 = date1.get("Длина_интервала").asInt();
        buffer.append("Поисковая дата: ").append(num1).append(" - ").append(date1).append("\n");


        if (len1 - len0 > 2)
          save = true;
      }

      if (save)
        strEvents.append(buffer);
    }


    return strEvents.toString();
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    SearchDatesAnalyzer dAnalyzer = new SearchDatesAnalyzer();
    StringBuilder result = new StringBuilder();

    // Для каждого файла в каталоге открываем поток чтения жизнеописания и передаём
    // анализатору.
    // Блок try автоматически закроет файл при окончании работы
    File inputFiles = new File("resources/input");
    for (File inputFile : inputFiles.listFiles()) {
      if (inputFile.isFile())
        try (FileInputStream inp = new FileInputStream(inputFile)) {
          result.append(dAnalyzer.analyze(inp));

        }
    }

    System.out.println(result.toString());
  }
}
