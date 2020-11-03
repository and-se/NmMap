package pstgu.NmMap.application;

import java.util.ArrayList;
import pstgu.NmMap.model.Human;
import pstgu.NmMap.model.MainMtStorage;

public class TestForZhenya {

  public static void main(String[] args) {
    MainMtStorage storage = new MainMtStorage("resources/output");

    Human human = storage.getHuman(1);

    String[] article = human.getArticle().split("\n");
    for (String string : article) {
      System.out.println("tatata " + string);
    }
  }
}
