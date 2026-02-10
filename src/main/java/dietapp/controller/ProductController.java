package dietapp.controller;

import org.springframework.web.bind.annotation.*;
import dietapp.model.Product;
import dietapp.repository.ProductRepository;
import dietapp.service.ProductApiService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductApiService apiService;
    private final ProductRepository productRepository;

    public ProductController(ProductApiService apiService, ProductRepository productRepository) {
        this.apiService = apiService;
        this.productRepository = productRepository;
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String name) {
        return apiService.searchProductsByName(name);
    }

    @PostMapping("/add")
    public Product addProductToDatabase(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping("/my-pantry")
    public List<Product> getMyProducts() {
        return productRepository.findAll();
    }
}