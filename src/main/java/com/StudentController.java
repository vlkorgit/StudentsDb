package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class StudentController {

	private StudentRepo studentRepo;

	@Autowired
	public StudentController(StudentRepo sr) {
		this.studentRepo = sr;
	}

	@GetMapping("/")
	public String getHome(){
		return "redirect:/students";
	}


	@GetMapping("/students")
	public String getStudentPage(Model model, @RequestParam(value = "page",required = false) Integer page){
		if (page==null) page = 1;
		model.addAttribute("students",studentRepo.getStudentsPage(page,15));
		model.addAttribute("PageNum",page);
		int pages = studentRepo.getStudentsAmount()/15;
		if (pages==0) pages=1;
		model.addAttribute("Pages",pages);
		return "students";
	}

	@GetMapping("/students/add")
	public String getAddPage(Model model) {
		model.addAttribute("student", new StudentValidator());
		return "addstudent";
	}
	@GetMapping("/students/remove")
	public String removeStudent(Model model, @RequestParam("id") Integer id){
		studentRepo.removeStudentById(id);
		return "redirect:/";
	}

	@PostMapping("/students/add")
	public String addStudent(StudentValidator student, Model model) {
		var validationResults = student.validate();
		if (student.hasErrors()) {
			model.addAttribute("student", student);
			validationResults.forEach(error -> {
				model.addAttribute(error.getFieldName() + "ErrorState", error.getError());
				model.addAttribute(error.getFieldName() + "ErrorMessage", error.getMessage());
			});
			return "addstudent";
		}
		studentRepo.addStudent(student.parse());
		model.addAttribute("student", new StudentValidator());
		return "addstudent";
	}
}
