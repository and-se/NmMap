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

	private final PersonRepository repository;
	MtSimpleStorage storage = new MtSimpleStorage("resources/output");

	@Autowired
	public SimpleController(PersonRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/list")
	public String listPage(Model model) {
//		List<Person> persons = repository.findAll();
//		model.addAttribute("persons", persons);
		List<Human> persons = new ArrayList<Human>(storage.getMap().values());
		model.addAttribute("persons", persons);
		
		return "list";
	}

	@GetMapping("/person")
	public String personPage(@RequestParam("id") int id, Model model) {
		Person person = repository.findById(id).orElseThrow(NotFoundException::new);
		model.addAttribute("person", person);
		return "person";
	}
	
	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}
}
