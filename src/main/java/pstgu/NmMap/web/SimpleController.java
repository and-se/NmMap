package pstgu.NmMap.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pstgu.NmMap.application.MtSimpleStorage;
import pstgu.NmMap.model.Human;
import pstgu.NmMap.model.MtStorage;

@Controller
public class SimpleController {

  MtStorage storage = new MtSimpleStorage("resources/output");
  private HumanPagingService humanService = new HumanPagingService(storage);

  @GetMapping("/")
  public String homepage(Model m)
  {
    var azbuka = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЭЮЯ".toCharArray();
    m.addAttribute("azbuka", azbuka);
    
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

    return "human";
  }

  @GetMapping("/search")
  public String fullSearch(
      @RequestParam(name = "q", required = false, defaultValue = "") String query, Model model,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("size") Optional<Integer> size) {
    
    int take = size.orElse(10);
    int pageNum = page.orElse(1);    
    int skip = (pageNum-1)*take;
    
    Human[] response = new Human[0];
    long total = 0;
    if (!query.isEmpty())
    {
      response = storage.findHumansFullText(query, skip, take);    
      total = storage.countHumansFullText(query);
    }

    Page<Human> humanPage = new PageImpl<Human>(
        Arrays.asList(response), PageRequest.of(pageNum-1, take), total);
    
    
    model.addAttribute("humanPage", humanPage);
    model.addAttribute("q", query);
    
    addPageNumbersToModel(model, humanPage);
    
    return "main :: html(view=search)";
  }

  @RequestMapping(value = "/persons", method = RequestMethod.GET)
  public String listPersons(@RequestParam(name="startletter", required=false) String letter, Model model, @RequestParam("page") Optional<Integer> page,
      @RequestParam("size") Optional<Integer> size) {
    final int currentPage = page.orElse(1);
    final int pageSize = size.orElse(10);
    

    Page<Human> humanPage = humanService.findPaginated(PageRequest.of(currentPage - 1, pageSize), letter);

    model.addAttribute("humanPage", humanPage);

    addPageNumbersToModel(model, humanPage);
    
    model.addAttribute("startletter", letter);
    
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
}
