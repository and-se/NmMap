package pstgu.NmMap.application;

public class TestProgram {
	public static void main(String[] args) {
		var db = new TestMtStorage();

		var data = db.findHumansByFio("А", null, 0, 20);

		for (var human : data) {
			System.out.println(human.getFio());
		}
	}
}
