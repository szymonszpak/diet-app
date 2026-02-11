package dietapp.controller;

import dietapp.repository.ProductRepository;
import dietapp.service.MealService;
import dietapp.service.ProductApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import dietapp.model.Product;

import java.util.List;

@Controller
public class MainViewController {

    private final MealService mealService;
    private final ProductApiService productApiService;
    private final ProductRepository productRepository;

    public MainViewController(MealService mealService, ProductApiService productApiService, ProductRepository productRepository){
        this.mealService = mealService;
        this.productApiService = productApiService;
        this.productRepository = productRepository;
    }

    @PostMapping("/add-meal")
    public String addMeal(@RequestParam String name) {
        mealService.createMeal(name);
        return "redirect:/";
    }

    @PostMapping("/add-to-meal")
    public String addProductToMeal(
            @RequestParam Long mealId,
            @RequestParam double weight,
            @ModelAttribute Product product) {

        Product savedProduct = productRepository.findByName(product.getName())
                .orElseGet(() -> productRepository.save(product));

        mealService.addProductToMeal(mealId, savedProduct.getId(), weight);

        return "redirect:/";
    }

    @PostMapping("/delete-item/{itemId}")
    public String deleteItem(@PathVariable Long itemId) {
        mealService.removeItemFromMeal(itemId);
        return "redirect:/";
    }

    @PostMapping("/delete-meal/{mealId}")
    public String deleteMeal(@PathVariable Long mealId) {
        mealService.deleteMeal(mealId);
        return "redirect:/";
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String query, Model model){
        model.addAttribute("summary", mealService.getDailySummary());
        model.addAttribute("meals", mealService.getTodaysMeals());

        if (query != null && !query.isEmpty()) {
            List<Product> results = productApiService.searchProductsByName(query);
            model.addAttribute("searchResults", results);
        }

        return "index";
    }

    @PostMapping("/set-goal")
    public String setGoal(@RequestParam double goal) {
        mealService.setUserGoal(goal);
        return "redirect:/";
    }
}

