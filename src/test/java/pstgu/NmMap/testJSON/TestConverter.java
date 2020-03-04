package pstgu.NmMap.testJSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Пример конвертации предоставленных JSON-данных в формат, эквивалентный классу Human.
 */
public class TestConverter {
  /**
   * Выполняет конвертацию.
   * @param datareader поток для чтения входных данных
   * @return сконвертированные данные в формате JSON
   * @throws IOException если что-то с чтением пошло не так
   */
  public ObjectNode convert(InputStream datareader) throws IOException {
    // Чтение данных в дерево data
    var rdr  = new ObjectMapper();
    var data = rdr.readTree(datareader);
    
    // Создаём пустое дерево result
    var wrtr  = new ObjectMapper();
    var result = wrtr.createObjectNode();
    
    // Копируем ключ записи
    result.set("id", data.get("Номер"));
    
    // @Заголовок = Канонизация.Чин_святости + ФИО + Сан_ЦеркСлужение
    // Пробуем читать все необходимые поля и собираем из них итоговый текст
    var articleTitle = data.get("ФИО").asText();    
    var san = data.get("Сан_ЦеркСлужение");
    var sainthood = data.at("/Канонизация/Чин_святости");
    
    if (san != null) {
      articleTitle += ", " + san.asText();
    }
    
    if (sainthood != null) {
      articleTitle = sainthood.asText() + " " + articleTitle;
    }    
    
    //result.set("fio", result.textNode(article_title));
    result.put("fio", articleTitle);
    
    // TODO Написать код конвертации остальных полей 
    
    return result;
    
  }
  
  public static void main(String[] args) throws FileNotFoundException, IOException
  {
    // Создаём конвертер - по совместительству это наш текущий класс
    var converter = new TestConverter();
    ObjectNode result;
    
    // Открываем поток чтения жизнеописания 5638 и передаём конвертеру.
    // Блок try автоматически закроет файл при окончании работы
    try(var inp = new FileInputStream("src/files/5638.json")) {
      result = converter.convert(inp);
    }
    
    // Выводим на экран получившийся в результате конвертации JSON
    System.out.println(result.toPrettyString());    
  }

}
