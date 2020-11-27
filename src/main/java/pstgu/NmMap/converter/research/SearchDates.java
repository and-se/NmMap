package pstgu.NmMap.converter.research;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchDates {

  public static void main(String[] args) throws FileNotFoundException, IOException {
    File inputFiles = new File("resources/input");
    for (File inputFile : inputFiles.listFiles()) {
      if (inputFile.isFile())
        try (FileInputStream inp = new FileInputStream(inputFile)) {
          processFile(inp);
          }
    }

    // System.out.println(result.toPrettyString());
  }

  private static void processFile(FileInputStream inp) throws IOException {
    // TODO Auto-generated method stub
    var rdr = new ObjectMapper();
    JsonNode data = rdr.readTree(inp);
    String id = data.get("Номер").asText();
    JsonNode events = data.withArray("События");
    for (JsonNode event : events) {
   
      var date = event.get("Датировка");
      var text = event.get("Текст");
      
      if(event.withArray("Поисковые_даты").size()>1)
        {System.out.println(id);
      System.out.println(date);
      System.out.println(event.withArray("Поисковые_даты").toPrettyString());
      //System.out.println(event.withArray("Поисковые_даты").size());
        }
     }

  }
}
