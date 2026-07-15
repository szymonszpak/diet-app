package dietapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class DailySummary {
    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFat;
    private double calorieGoal;
    private double remainingCalories;
}
