
public class TestProgram {
  public static void main(String[] args) {    
    MtStorage db = new TestMtStorage();
    
    Human[] data = db.findHumansByFio("À", null, 0, 20);
    
    for (Human human: data) {
      System.out.println(human.getFio());
    }    
  }
}
