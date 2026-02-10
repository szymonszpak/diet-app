package dietapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import dietapp.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductApiService {

    public List<Product> searchProductsByName(String name) {
        String url = "https://pl.openfoodfacts.org/cgi/search.pl?search_terms=" + name + "&search_simple=1&action=process&json=1";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Product> results = new ArrayList<>();

        if (response != null && response.containsKey("products")) {
            List<Map<String, Object>> productsJson = (List<Map<String, Object>>) response.get("products");

            for (Map<String, Object> item : productsJson) {
                try {
                    Map<String, Object> nutriments = (Map<String, Object>) item.get("nutriments");

                    if (nutriments != null && nutriments.containsKey("energy-kcal_100g")) {
                        Product p = new Product();
                        p.setName((String) item.get("product_name"));
                        p.setCalories(Double.parseDouble(nutriments.get("energy-kcal_100g").toString()));
                        p.setProtein(getDoubleValue(nutriments, "proteins_100g"));
                        p.setCarbs(getDoubleValue(nutriments, "carbohydrates_100g"));
                        p.setFat(getDoubleValue(nutriments, "fat_100g"));

                        results.add(p);
                    }
                } catch (Exception e) {
                }
            }
        }
        return results;
    }

    private double getDoubleValue(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? Double.parseDouble(val.toString()) : 0.0;
    }
}