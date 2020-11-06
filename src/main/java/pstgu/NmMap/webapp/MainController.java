package pstgu.NmMap.webapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pstgu.NmMap.model.Human;
import pstgu.NmMap.model.Location;
import pstgu.NmMap.model.MainMtStorage;
import pstgu.NmMap.model.MtStorage;

@Controller
public class MainController {

  MtStorage storage = new MainMtStorage("resources/output");
  private HumanPagingService humanService = new HumanPagingService();

  @GetMapping("/")
  public String homepage(Model m) {
    var azbuka = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЭЮЯ".toCharArray();
    m.addAttribute("azbuka", azbuka);
    m.addAttribute("head", "fragments::mapScripts");

    // В main.html будет переменная $view = 'index'
    // Можно передавать и через model - model.addAttribute("view", "index")
    return "main :: html(view=index)";
  }


  @GetMapping("/persons/{id}")
  public String personPage(@PathVariable("id") int id, Model model) {
    Human human = storage.getHuman(id);
    model.addAttribute("human", human);
    String[] article = human.getArticle().split("\n");
    model.addAttribute("article", article);

    return "main :: html(view=human)";
  }

  @GetMapping("/search")
  public String fullSearch(
      @RequestParam(name = "q", required = false, defaultValue = "") String query, Model model,
      @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
    var page_info = PageRequest.of(page.orElse(1) - 1, size.orElse(10));

    // Строим страницу, вторым параметром передаём функцию поиска, которая
    // принимает skip и take, а возвращает
    // пару <жизнеописания для страницы, общее количество>
    // Используем полнотекстовый поиск
    var humanPage = humanService.buildPage(page_info, (skip, take) -> {
      Human[] response = new Human[0];
      int total = 0;
      if (!query.isEmpty()) {
        response = storage.findHumansFullText(query, skip, take);
        total = (int) storage.countHumansFullText(query);
      }

      return Pair.of(response, total);
    });

    model.addAttribute("humanPage", humanPage);
    model.addAttribute("q", query);

    addPageNumbersToModel(model, humanPage);
    return "main :: html(view=search)";
  }

  @RequestMapping(value = "/persons", method = RequestMethod.GET)
  public String listPersons(@RequestParam(name = "startletter", required = false) String letter,
      Model model, @RequestParam("page") Optional<Integer> page,
      @RequestParam("size") Optional<Integer> size) {
    var page_info = PageRequest.of(page.orElse(1) - 1, size.orElse(10));
    // Строим страницу - в передаваемой функции поиска используем поиск по ФИО
    // А так всё то же, что в fullSearch
    var humanPage = humanService.buildPage(page_info, (skip, take) -> {
      Human[] data;
      var count = (int) storage.countHumansByFio(letter, "");
      var humans = storage.findHumansByFio(letter, "", skip, take);
      return Pair.of(humans, count);
    });

    model.addAttribute("humanPage", humanPage);
    model.addAttribute("startletter", letter);

    addPageNumbersToModel(model, humanPage);

    return "main :: html(view=list)";
  }

  private void addPageNumbersToModel(Model model, Page<Human> humanPage) {
    int totalPages = humanPage.getTotalPages();
    if (totalPages > 0) {
      List<Integer> pageNumbers =
          IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
      model.addAttribute("pageNumbers", pageNumbers);
    }
  }

  @GetMapping("/greeting")
  public String greeting(
      @RequestParam(name = "name", required = false, defaultValue = "World") String name,
      Model model) {
    model.addAttribute("name", name);
    return "greeting";
  }

  @GetMapping("/map")
  public String map(Model model) {

    return "map";
  }

  @GetMapping("/map/all_points")
  @ResponseBody
  public List<Location> getAllPoints() {
    return storage.getLocations(null);
    // return new Location [] {new Location(55.76, 37.64, "HelloWorld!"),new Location(55.86, 37.64,
    // "Hello!")};
  }

}
