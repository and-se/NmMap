package pstgu.NmMap.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventTypesFinder {

  Set<String> getEventTypesFromFile(File file) throws JsonProcessingException, IOException {
    var result = new HashSet<String>();
    var rdr = new ObjectMapper();
    JsonNode data = rdr.readTree(file);

    JsonNode events = data.withArray("События");
    for (JsonNode event : events) {
      var type = Optional.ofNullable(event.get("Тип"));
      type.ifPresent(t -> result.add(t.asText()));
    }

    return result;
  }


  public static void main(String[] args) throws FileNotFoundException, IOException {
    EventTypesFinder finder = new EventTypesFinder();
    var result = new HashSet<String>();

    // Для каждого файла в каталоге открываем поток чтения жизнеописания и передаём
    // поисковику событий.
    // Блок try автоматически закроет файл при окончании работы
    File inputFiles = new File("resources/input");
    for (File inputFile : inputFiles.listFiles()) {
      if (inputFile.isFile()) {
        var setFromFile = finder.getEventTypesFromFile(inputFile);
        result.addAll(setFromFile);
      }
    }

    var listOfEventTypes = new ArrayList<String>(result);
    Collections.sort(listOfEventTypes);
    System.out.println(listOfEventTypes + " " + listOfEventTypes.size());
  }
}
