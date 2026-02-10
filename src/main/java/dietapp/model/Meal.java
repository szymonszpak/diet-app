package dietapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate date;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealItem> items = new ArrayList<>();

    public double getTotalCalories() {
        return items.stream()
                .mapToDouble(MealItem::getCalculatedCalories)
                .sum();
    }

    public double getTotalProtein() {
        return items.stream().mapToDouble(item ->
                (item.getProduct().getProtein() * item.getWeightInGrams()) / 100.0).sum();
    }

    public double getTotalCarbs() {
        return items.stream().mapToDouble(item ->
                (item.getProduct().getCarbs() * item.getWeightInGrams()) / 100.0).sum();
    }

    public double getTotalFat() {
        return items.stream().mapToDouble(item ->
                (item.getProduct().getFat() * item.getWeightInGrams()) / 100.0).sum();
    }
}