package dietapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double weightInGrams;

    public double getCalculatedCalories() {
        return (product.getCalories() * weightInGrams) / 100.0;
    }
}