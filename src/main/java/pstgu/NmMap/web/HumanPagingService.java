package pstgu.NmMap.web;

import java.util.Arrays;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pstgu.NmMap.model.Human;
import pstgu.NmMap.model.MtStorage;

/**
 * Клас для организации паджинации списка
 * 
 */
//@Service
public class HumanPagingService {
	private MtStorage storage;

	public HumanPagingService(MtStorage storage) {
		this.storage = storage;
	}

	public Page<Human> findPaginated(Pageable pageable, String letter) {
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		Human[] data;

		var count = storage.countHumansByFio(letter, "");
		if (count < startItem) {
			data = new Human[0];
		} else {
			//long toIndex = Math.min(startItem + pageSize,count );
//			list = persons.subList(startItem, toIndex);
			data = storage.findHumansByFio(letter, "", startItem, pageSize);
		}

		Page<Human> humanPage = new PageImpl<Human>(Arrays.asList(data), PageRequest.of(currentPage, pageSize), count);

		return humanPage;
	}
}
