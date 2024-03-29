package pstgu.NmMap.webapp;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pstgu.NmMap.model.Human;
import pstgu.NmMap.model.HumanTextSearchResult;
import pstgu.NmMap.model.Location;
import pstgu.NmMap.model.MainMtStorage;
import pstgu.NmMap.model.MtStorage;
import pstgu.NmMap.model.fts.TextHighlighter;

@Controller
public class MainController {

	MtStorage storage = new MainMtStorage("resources/output");
	private HumanPagingService humanService = new HumanPagingService();

	@GetMapping("/")
	public String homepage(Model m) {
		m.addAttribute("head", "fragments::mapScripts");
		m.addAttribute("title", "Новомученики-интерактивная карта");
		m.addAttribute("description","Карта Новомученики позволяет просмотреть места,"
				+ " связанные с жизненным путём новомучеников. На карте отмечены места репресcий,"
				+ " служения, кончины и прочие.");
		// В main.html будет переменная $view = 'index'
		// Можно передавать и через model - model.addAttribute("view", "index")
		return "main :: html(view=index)";
	}

	@GetMapping("/persons/{id}")
	public String personPage(@PathVariable("id") int id,
			@RequestParam(name="highlight", required=false, defaultValue="")
			String query,
			Model model) {
		Human human = storage.getHuman(id);
		model.addAttribute("human", human);
		
		String text, title,heading;
		
		title = HtmlUtils.htmlEscape(human.getTitle());
		
		if (query.isEmpty())
		{
			text = HtmlUtils.htmlEscape(human.getArticle());
			heading = HtmlUtils.htmlEscape(human.getTitle());
		}
		else
		{
			var textBuilder = new TextHighlighter();
			text = textBuilder.highlight(query, human.getArticle());
			heading = textBuilder.highlight(query, human.getTitle());
		}		
		
		String[] article = text.split("\n");
		model.addAttribute("title", title);
		model.addAttribute("description",title);
		model.addAttribute("article", article);
		model.addAttribute("heading", heading);

		return "main :: html(view=human)";
	}

	@GetMapping("/search")
	public String fullSearch(@RequestParam(name = "q", required = false, defaultValue = "") String query, Model model,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		var page_info = PageRequest.of(page.orElse(1) - 1, size.orElse(10));

		// Строим страницу, вторым параметром передаём функцию поиска, которая
		// принимает skip и take, а возвращает
		// пару <результат поиска для страницы, общее количество>
		// Используем полнотекстовый поиск
		var humanPage = humanService.buildPageFts(page_info, (skip, take) -> {
			HumanTextSearchResult[] response = null;
			int total = 0;
			response = storage.findHumansFullText(query, skip, take);
			total = (int) storage.countHumansFullText(query);
			return Pair.of(response, total);
		});
		
		model.addAttribute("title", "Новомученики-Поиск");
		model.addAttribute("description","Поиск биографий новомучеников и исповедников, "
				+ "пострадавших в годы гонений на Русскую Православную Церковь в XXв.");
		model.addAttribute("humanPage", humanPage);
		model.addAttribute("q", query);

		addPageNumbersToModel(model, humanPage);
		return "main :: html(view=search)";
	}

	@RequestMapping(value = "/persons", method = RequestMethod.GET)
	public String listPersons(@RequestParam(name = "startletter", required = false) String letter,
			@RequestParam(name = "searchtype", defaultValue = "starts-with") String searchType, Model model,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		var page_info = PageRequest.of(page.orElse(1) - 1, size.orElse(10));
		// Строим страницу - в передаваемой функции поиска используем поиск по ФИО
		// А так всё то же, что в fullSearch
		var humanPage = humanService.buildPage(page_info, (skip, take) -> {
			Human[] humans;
			int count;
			if (searchType.equals("contains")) {
				count = (int) storage.countHumansByFio("", letter);
				humans = storage.findHumansByFio("", letter, skip, take);
			} else {
				count = (int) storage.countHumansByFio(letter, "");
				humans = storage.findHumansByFio(letter, "", skip, take);
			}

			return Pair.of(humans, count);
		});
		model.addAttribute("title", "Новомученики-Персоналии");
		model.addAttribute("description","Биографии новомучеников и исповедников, "
				+ "пострадавших в годы гонений на Русскую Православную Церковь в XXв.");
		model.addAttribute("humanPage", humanPage);
		model.addAttribute("startletter", letter);
		model.addAttribute("searchtype", searchType);
		var azbuka = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЭЮЯ".toCharArray();
		model.addAttribute("azbuka", azbuka);

		addPageNumbersToModel(model, humanPage);

		return "main :: html(view=list)";
	}

	private void addPageNumbersToModel(Model model, Page humanPage) {
		int totalPages = humanPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("description","Проект Новомученики позволяет просмотреть на карте места, связанные с жизненным путём новомучеников."
				+ " Доступны фильтры по типам событий, а также годам.");
		model.addAttribute("title", "О проекте");
		return "main :: html(view=about)";
	}

	@GetMapping("/map/all_points")
	@ResponseBody
	public List<Location> getAllPoints() {
		return storage.getLocations(null);
		// return new Location [] {new Location(55.76, 37.64, "HelloWorld!"),new
		// Location(55.86, 37.64,
		// "Hello!")};
	}
	
		
	@GetMapping("/robots.txt")
	public void robots(HttpServletResponse response ) 
		 throws IOException {
		response.setContentType("text/plain");
	    
	       ServletOutputStream out = response.getOutputStream();
	       out.println("User-agent: *\n" + 
	       		"Disallow: /search\n" + 
	       		"\n" + 
	       		"Host: map.nmbook.ru");
	       out.flush();
	       out.close();
	}

}
