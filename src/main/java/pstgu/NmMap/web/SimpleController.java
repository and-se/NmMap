package pstgu.NmMap.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
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

@Controller
public class SimpleController {

	MtSimpleStorage storage = new MtSimpleStorage("resources/output");
	private MtSimpleStorage.HumanService humanService = storage.new HumanService();

	@GetMapping("/persons/{id}")
	public String personPage(@PathVariable("id") int id, Model model) {
		Human human = storage.getHuman(id);
		model.addAttribute("human", human);
		String[] article = human.getArticle().split("\n");
		model.addAttribute("article", article);

		return "human";
	}

	@GetMapping("/search")
	public String fullSearch(@RequestParam(name = "q", required = false, defaultValue = "") String query, Model model) {
		Human[] response = new Human[0];

		if (!query.isEmpty())
			response = storage.findHumansFullText(query, 0, 30);

		model.addAttribute("persons", response);
		model.addAttribute("q", query);
		return "search";
	}

	@RequestMapping(value = "/persons", method = RequestMethod.GET)
	public String listPersons(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		final int currentPage = page.orElse(1);
		final int pageSize = size.orElse(10);

		Page<Human> humanPage = humanService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

		model.addAttribute("humanPage", humanPage);

		int totalPages = humanPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "list";
	}

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}
}
