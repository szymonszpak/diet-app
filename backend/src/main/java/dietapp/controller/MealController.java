package dietapp.controller;

import dietapp.model.DailySummary;
import org.springframework.web.bind.annotation.*;
import dietapp.model.Meal;
import dietapp.service.MealService;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // Creating a new meal
    @PostMapping
    public Meal createMeal(@RequestParam String name) {
        return mealService.createMeal(name);
    }

    // Adding a product to a meal
    @PostMapping("/{mealId}/products/{productId}")
    public Meal addProductToMeal(
            @PathVariable Long mealId,
            @PathVariable Long productId,
            @RequestParam double weight) {
        return mealService.addProductToMeal(mealId, productId, weight);
    }

    // Displaying all meals
    @GetMapping
    public List<Meal> getAllMeals() {
        return mealService.getTodaysMeals();
    }

    // Removing the product from the meal
    @DeleteMapping("/items/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        mealService.removeItemFromMeal(itemId);
    }

    // Daily summary
    @GetMapping("/summary")
    public DailySummary getSummary() {
        return mealService.getDailySummary();
    }

    @PostMapping("/summary/goal")
    public void updateGoal(@RequestParam double value) {
        mealService.setUserGoal(value);
    }
}