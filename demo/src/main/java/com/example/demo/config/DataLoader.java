package com.example.demo.config;

import org.springframework.transaction.annotation.Transactional;
import com.example.demo.model.Car;
import com.example.demo.model.Category;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.model.AppointmentType;
import com.example.demo.model.CarConfiguration;
import com.example.demo.model.CarOption;
import com.example.demo.model.ConfigStatus;
import com.example.demo.model.OwnedVehicle;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.CarConfigurationRepository;
import com.example.demo.repository.CarOptionRepository;
import com.example.demo.repository.OwnedVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;
    private final CarConfigurationRepository carConfigurationRepository;
    private final CarOptionRepository carOptionRepository;
    private final OwnedVehicleRepository ownedVehicleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create default categories if they don't exist
        if (categoryRepository.count() == 0) {
            Category sedan = new Category();
            sedan.setName("Sedan");
            sedan.setDescription("Xe hơi 4 cửa, 2 hàng ghế");

            Category suv = new Category();
            suv.setName("SUV");
            suv.setDescription("Xe thể thao đa dụng");

            Category hatchback = new Category();
            hatchback.setName("Hatchback");
            hatchback.setDescription("Xe có cửa sau mở lên");

            Category coupe = new Category();
            coupe.setName("Coupe");
            coupe.setDescription("Xe 2 cửa thể thao");

            categoryRepository.saveAll(Arrays.asList(sedan, suv, hatchback, coupe));
            System.out.println("Default categories created.");
        }

        // Create default user if not exists



        // Create 20 sample cars if they don't exist
        if (carRepository.count() == 0) {
            List\u003cCategory\u003e categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                System.err.println("No categories found, cannot create cars.");
                return;
            }

            List\u003cString\u003e carNames = Arrays.asList(
                    "Toyota Camry", "Honda Civic", "Mazda 3", "Hyundai Elantra", "Kia K3",
                    "Mercedes-Benz C-Class", "BMW 3 Series", "Audi A4", "VinFast Lux A2.0", "Ford Ranger",
                    "Mitsubishi Xpander", "Toyota Corolla Cross", "Hyundai Santa Fe", "Kia Seltos", "Mazda CX-5",
                    "Honda CR-V", "Ford Everest", "Suzuki Swift", "Toyota Yaris", "Volkswagen Polo"
            );

            for (int i = 0; i \u003c carNames.size(); i++) {
                Car car = new Car();
                car.setName(carNames.get(i));
                car.setManufacturer(i % 2 == 0 ? "Japan" : "Korea");
                car.setYear(2020 + (i % 3)); // Years 2020, 2021, 2022
                car.setPrice(BigDecimal.valueOf(500000000 + (i * 10000000))); // Starting from 500M VND
                car.setDescription("Mô tả chi tiết cho xe " + carNames.get(i));
                String uploads;
                car.setImageUrl("/uploads/car" + (i + 1) + ".jpg");
                car.setCategory(categories.get(i % categories.size())); // Assign categories cyclically
                carRepository.save(car);
            }
            System.out.println("20 sample cars created.");
        }

        // Create sample car options if they don't exist
        if (carOptionRepository.count() == 0) {
            CarOption option1 = new CarOption();
            option1.setName("Sunroof");
            option1.setDescription("Cửa sổ trời toàn cảnh");
            option1.setPrice(BigDecimal.valueOf(20000000));

            CarOption option2 = new CarOption();
            option2.setName("Leather Seats");
            option2.setDescription("Ghế da cao cấp");
            option2.setPrice(BigDecimal.valueOf(15000000));

            CarOption option3 = new CarOption();
            option3.setName("Premium Sound System");
            option3.setDescription("Hệ thống âm thanh cao cấp");
            option3.setPrice(BigDecimal.valueOf(10000000));

            carOptionRepository.saveAll(Arrays.asList(option1, option2, option3));
            System.out.println("Sample car options created.");
        }




    }
}