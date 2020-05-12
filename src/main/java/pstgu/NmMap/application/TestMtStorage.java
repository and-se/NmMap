package pstgu.NmMap.application;

import java.util.ArrayList;

import pstgu.NmMap.model.Human;
import pstgu.NmMap.model.MtStorage;

public class TestMtStorage implements MtStorage {

	Human[] data = new Human[] { new Human(1, "Иван Иванович", "из Москвы"),
			new Human(2, "Петр Петрович", "из Петербурга"), new Human(3, "Сидор Сидорович", "из Казани"),
			new Human(4, "Адамантов Адамант", "из Минска"), new Human(5, "Ааронович Аарон", "из Вологды") };

	@Override
	public Human getHuman(int id) {
		return null;
	}

	@Override
	public Human[] findHumansByFio(String fioStarts, String fioContains, int skip, int take) {
		ArrayList<Human> result = new ArrayList<Human>();
		for (Human h : data) {
			if (h.getFio().startsWith(fioStarts)) {
				result.add(h);
			}
		}

		return result.toArray(new Human[0]);
	}

	@Override
	public Human[] findHumansFullText(String query, int skip, int take) {
		return null;
	}

	@Override
	public long countHumansByFio(String fioStarts, String fioContains) {
		throw new UnsupportedOperationException("");
//		return 0;
	}

  @Override
  public long countHumansFullText(String query) {
    // TODO Auto-generated method stub
    return 0;
  }

}
