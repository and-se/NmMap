package pstgu.NmMap.testJSON;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import pstgu.NmMap.model.Human;
import pstgu.NmMap.model.Location;

/**
 * 
 *
 */
public class TestJackson {
  public static void main(String[] args) throws Exception {
    final String path = "resources/output/5635.json";
    ObjectMapper mapper = new ObjectMapper();

    // вывод в json файл списка классов
    /*
     * List<Human> list = generateHumansList(); String jsonString =
     * mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list); writeToFile(path,
     * jsonString); System.out.println(jsonString);
     */

    // зачитывание файла и
    String jsonDataString = readFile(path, Charset.forName("UTF-8"));
    if (jsonDataString == null || jsonDataString.isEmpty()) {
      System.err.println("Failed to load file " + path);
      System.exit(-1);
    }
    // создание Human
    Human human = mapper.readValue(jsonDataString, Human.class);
    System.out.println(human);
  }

  static List<Human> generateHumansList() {
    List<Human> list = new ArrayList<Human>();

    Human human1 = new Human(1,
        "Священномученик ГАВРИИЛ (Архангельский Гавриил Иванович), священник",
        "Окончил Петровское духовное училище и Саратовскую духовную семинарию. Рукоположен во диакона.",
        Arrays.asList(new Location(52.864192, 45.133941,
            "1890, 25 марта" , " — родился в с. Урлейка Петровского уезда Саратовской губ."
                + " (ныне Кондольский р-н Пензенской обл.) в семье псаломщика.")));
    Human human2 = new Human(2, "ГАВРИЛОВА Лидия Дмитриевна (инокиня Лидия), регент",
        "1905 — родилась в Москве в семье торговца.\n" + "С детства пела в церковных хорах.\n"
            + "1917–1925 — духовная дочь архимандрита Филарета (Волчана),"
            + " бывшего настоятеля Чудова монастыря.\n" + "1923 — окончила гимназию.",
        Arrays.asList(
            new Location(55.551230, 36.373706,
                "1937, весна – 1941, октябрь ", "— проживала в пос. Дорохово под Можайском"
                    + " (Рузского р-на Московской обл.) вместе с монахиней Таисией (Арцыбушевой),"
                    + " духовной дочерью архимандрита Данилова монастыря Серафима (Климкова)."),
            new Location(48.971165, 22.980488, null, 
                " Регентовала в Подольском храме г. Житомира, затем в Никольском храме"
                    + " с. Битля Дрогобычской обл. (ныне Турковского р-на Львовской обл.),"
                    + " где служил о. Серафим.")));

    Human human3 = new Human(3, "АВЕРДОВСКИЙ Николай Николаевич, священник",
        "1877 — родился в г. Кадникове Вологодской губ.\n"
            + "Окончил Вологодскую духовную семинарию.",
        Arrays.asList(new Location(61.091513, 40.924797, null, 
            "Служил в Афанасиевской церкви с. Верхняя Подюга (точнее, Верхнеподюжского прихода."
                + " Центр прихода - ныне Архангельская обл., Коношский р-н, дер. Николаевка), проживал в дер."
                + " Великониколаевская Кадниковского Вельского уезда (ныне Николаевка)."),
            new Location(51.040001, 23.670830,
                "1876", "— родилась в дер. Улковяны Волковяны Холмского уезда Люблинской губ."
                    + " (ныне Хелмского повята Любельского воеводства Польши) в крестьянской семье."),
            new Location(53.107421, 45.555764, null, 
                "Активно противостоял обновленческому расколу. Служил в Рождество-Богородицкой церкви"
                    + " с. Канаевка Городищенского уезда Пензенской губ. (ныне р-на, обл.). ")));

    list.addAll(Arrays.asList(human1, human2, human3));
    return list;
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

  static String readFile(String path, Charset encoding) {
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, encoding);
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    return null;
  }
}
