package pstgu.NmMap.application;

import java.util.Scanner;
import pstgu.NmMap.model.MainMtStorage;

public class TestMain {
  /*
   * public static void main2(String[] args) { var db = new TestMtStorage();
   * 
   * var data = db.findHumansByFio("А", null, 0, 20);
   * 
   * for (var human : data) { System.out.println(human.getFio()); } }
   */

  public static void main(String[] args) {
    var storage = new MainMtStorage("resources/output");

    var cin = new Scanner(System.in);

    System.out.println("Введите первые буквы ФИО");
    var q1 = cin.nextLine();

    System.out.println("Введите дополнительный критерий ФИО");
    var q2 = cin.nextLine();

    for (var h : storage.findHumansByFio(q1, q2, 0, 10)) {
      System.out.println(h.getFio());
    }

    System.out.println("Введите слова через пробел для полнотекстового поиска");
    var q3 = cin.nextLine();
    for (var h : storage.findHumansFullText(q3, 0, 10)) {
      System.out.println(h.getFio());
    }

    cin.close();
  }
}
