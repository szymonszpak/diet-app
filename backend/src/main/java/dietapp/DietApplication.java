package dietapp; // Tu będzie Twoja nowa nazwa pakietu

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import dietapp.model.Product;
import dietapp.repository.ProductRepository;

@SpringBootApplication
public class DietApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietApplication.class, args);
    }
}