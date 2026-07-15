package dietapp.service;

import dietapp.model.DailySummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dietapp.model.Meal;
import dietapp.model.MealItem;
import dietapp.model.Product;
import dietapp.repository.MealItemRepository;
import dietapp.repository.MealRepository;
import dietapp.repository.ProductRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final ProductRepository productRepository;
    private final MealItemRepository mealItemRepository;

    private double userGoal = 2000.0;

    public MealService(MealRepository mealRepository, ProductRepository productRepository, MealItemRepository mealItemRepository) {
        this.mealRepository = mealRepository;
        this.productRepository = productRepository;
        this.mealItemRepository = mealItemRepository;
    }

    public double getUserGoal() {
        return userGoal;
    }

    public void setUserGoal(double userGoal) {
        this.userGoal = userGoal;
    }

    public Meal createMeal(String name) {
        Meal meal = new Meal();
        meal.setName(name);
        meal.setDate(LocalDate.now());
        return mealRepository.save(meal);
    }

    @Transactional
    public Meal addProductToMeal(Long mealId, Long productId, double weight) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono posiłku"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono produktu"));

        MealItem item = new MealItem();
        item.setMeal(meal);
        item.setProduct(product);
        item.setWeightInGrams(weight);

        mealItemRepository.save(item);

        meal.getItems().add(item);
        return meal;
    }

    public List<Meal> getTodaysMeals() {
        return mealRepository.findAllByDate(LocalDate.now());
    }

    @Transactional
    public void removeItemFromMeal(Long itemId) {
        if (!mealItemRepository.existsById(itemId)) {
            throw new RuntimeException("Nie znaleziono takiej pozycji w posiłku!");
        }
        mealItemRepository.deleteById(itemId);
    }

    @Transactional
    public void deleteMeal(Long mealId) {
        mealRepository.deleteById(mealId);
    }

    public DailySummary getDailySummary() {
        LocalDate today = LocalDate.now();
        List<Meal> todaysMeals = mealRepository.findAllByDate(today);

        double totalKcal = todaysMeals.stream().mapToDouble(Meal::getTotalCalories).sum();
        double totalProtein = todaysMeals.stream().mapToDouble(Meal::getTotalProtein).sum();
        double totalCarbs = todaysMeals.stream().mapToDouble(Meal::getTotalCarbs).sum();
        double totalFat = todaysMeals.stream().mapToDouble(Meal::getTotalFat).sum();

        return new DailySummary(
                totalKcal,
                totalProtein,
                totalCarbs,
                totalFat,
                userGoal,
                userGoal - totalKcal
        );
    }
}