package pstgu.NmMap.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pstgu.NmMap.application.MtSimpleStorage;
import pstgu.NmMap.model.Human;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SimpleController {

	MtSimpleStorage storage = new MtSimpleStorage("resources/output");

	@GetMapping("/list")
	public String listPage(Model model) {
		List<Human> persons = new ArrayList<Human>(storage.getMap().values());
		model.addAttribute("persons", persons);

		return "list";
	}

	@GetMapping("/human")
	public String personPage(@RequestParam("id") int id, Model model) {
		Human human = storage.getHuman(id);
		model.addAttribute("human", human);

		return "human";
	}

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}
}
