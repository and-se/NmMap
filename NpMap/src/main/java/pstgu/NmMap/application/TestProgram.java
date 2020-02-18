package pstgu.NmMap.application;

public class TestProgram {
	public static void main(String[] args) {
		MtStorage db = new TestMtStorage();

		Human[] data = db.findHumansByFio("А", null, 0, 20);

		for (Human human : data) {
			System.out.println(human.getFio());
		}
	}
}
