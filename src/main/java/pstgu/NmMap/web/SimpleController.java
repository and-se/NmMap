package pstgu.NmMap.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import pstgu.NmMap.application.MtSimpleStorage;
import pstgu.NmMap.model.Human;

@Controller
public class SimpleController {

	MtSimpleStorage storage = new MtSimpleStorage("resources/output");

	@GetMapping("/persons")
	public String listPage(Model model) {
		List<Human> persons = new ArrayList<Human>(storage.getMap().values());
		model.addAttribute("persons", persons);

		return "list";
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
    public String fullSearch(@RequestParam(name="q", required=false, defaultValue="") 
    String query, Model model) {
		Human[] response = new Human[0];
		
		if(!query.isEmpty())
			response = storage.findHumansFullText(query, 0, 30);
		
        model.addAttribute("persons", response);
        model.addAttribute("q", query);
        return "search";
    }

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}
}
