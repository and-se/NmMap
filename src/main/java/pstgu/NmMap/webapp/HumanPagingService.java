package pstgu.NmMap.webapp;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import pstgu.NmMap.model.Human;
import pstgu.NmMap.model.MtStorage;

/**
 * Клас для организации паджинации списка
 * 
 */
// @Service
public class HumanPagingService {
  /**
   * Строит страницу данных
   * 
   * @param page_info информация о странице (номер от 0, размер)
   * @param searcher функция поиска данных - принимает skip и take, а возвращает пару <массив
   *        жизнеописаний для отображения на странице, общее количество>
   * @return
   */
  public Page<Human> buildPage(Pageable page_info,
      BiFunction<Integer, Integer, Pair<Human[], Integer>> searcher) {
    int pageNum = page_info.getPageNumber();
    int pageSize = page_info.getPageSize();

    // Производим поиск, вытаскиваем список жизнеописаний и общее количество
    var d = searcher.apply((int) page_info.getOffset(), pageSize);
    var humans = d.getFirst();
    var humans_count = d.getSecond();

    Page<Human> humanPage =
        new PageImpl<Human>(Arrays.asList(humans), PageRequest.of(pageNum, pageSize), humans_count);

    return humanPage;
  }
}
