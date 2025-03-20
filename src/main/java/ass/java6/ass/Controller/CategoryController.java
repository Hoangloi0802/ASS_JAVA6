// package ass.java6.ass.Controller;

// import org.springframework.ui.Model;
// import java.util.List;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import ass.java6.ass.Entity.Category;
// import ass.java6.ass.Repository.CategoryRepository;
// import ass.java6.ass.Service.CategoryService;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.RequestParam;

// @Controller
// public class CategoryController {
//     @Autowired
//     CategoryService categoryService;
//     @Autowired
//     CategoryRepository categoryRepository;

//     @GetMapping("/admin/categories")
//     public String CategoryIndex(Model model) {
//         Category cate = new Category();
//         model.addAttribute("cate", cate);
//         List<Category> categories = categoryRepository.findAll();
//         model.addAttribute("categories", categories);
//         return "admin/categoryManage";
//     }

//     @RequestMapping("/admin/categories/{id}")
//     public String CategoryEdit(Model model, @PathVariable("id") String id) {
//         Category cate = categoryRepository.findById(id).get();
//         model.addAttribute("cate", cate);
//         List<Category> categories = categoryRepository.findAll();
//         model.addAttribute("categories", categories);
//         return "admin/categoryManage";
//     }

//     @RequestMapping("/admin/categories/create")
//     public String create(Category item) {
//         categoryService.save(item);
//         return "redirect:/category/index";
//     }

//     @RequestMapping("/admin/categories/update")
//     public String update(Category item) {
//         categoryService.save(item);
//         return "redirect:/category/edit/" + item.getId();
//     }

//     @RequestMapping(value = "/admin/categories/delete", method = RequestMethod.POST)
//     public String delete(@RequestParam("id") String id) {
//         categoryService.deleteById(id);
//         return "redirect:/admin/categories";
//     }
// }
