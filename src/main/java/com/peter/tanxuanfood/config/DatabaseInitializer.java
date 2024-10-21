package com.peter.tanxuanfood.config;

import com.peter.tanxuanfood.domain.Category;
import com.peter.tanxuanfood.domain.Product;
import com.peter.tanxuanfood.domain.Role;
import com.peter.tanxuanfood.domain.User;
import com.peter.tanxuanfood.repository.CategoryRepository;
import com.peter.tanxuanfood.repository.ProductRepository;
import com.peter.tanxuanfood.repository.RoleRepository;
import com.peter.tanxuanfood.repository.UserRepository;
import com.peter.tanxuanfood.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${peter.initiation.data.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        long countUser = this.userRepository.count();
        long countProduct = this.productRepository.count();
        long countRole = this.roleRepository.count();
        long countCategory = this.categoryRepository.count();

        if (countRole == 0) {
            this.roleRepository.save(new Role(RoleType.ADMIN, "ADMIN có full quyền"));
        }

        if (countUser == 0) {
            Set<Role> roles = new HashSet<>(this.roleRepository.findAll());
            User user = new User();
            user.setFullName("admin");
            user.setEmail("admin@gmail.com");
            user.setPassword(passwordEncoder.encode(password));
            user.setAddress("179/58/16 Lê Đình Thám");
            user.setPhone("0394494821");
            user.setRoles(roles);
            this.userRepository.save(user);
        }

        if (countCategory == 0){
            this.categoryRepository.save(new Category("Thịt đông lạnh"));
            this.categoryRepository.save(new Category("Rau củ đông lạnh"));
        }

        if (countProduct == 0) {
            Category meatCategory = this.categoryRepository.findByName("Thịt đông lạnh");
            Category vegetableCategory = this.categoryRepository.findByName("Rau củ đông lạnh");

            Set<Product> products = new HashSet<>();
            products.add(new Product("Xương bò Úc", "Nhập khẩu từ Sydney", 65000, 500, meatCategory, true));
            products.add(new Product("Nạm bò Úc", "Nhập khẩu từ Sydney", 92000, 700, meatCategory, true));
            products.add(new Product("Chân gà 35+ up Balan Plukon", "Công dụng, chế biến:chân gà hấp/luộc/nướng/trộn sốt,….", 68000, 200, meatCategory, true));
            products.add(new Product("Đùi Tỏi lớn Koch", "Đùi tỏi lớn Koch là sản phẩm chất lượng cao, nhập khẩu từ nguồn cung uy tín, đảm bảo tiêu chuẩn vệ sinh an toàn thực phẩm. Sản phẩm thích hợp để chế biến các món ăn đa dạng như nướng, chiên, hoặc làm nguyên liệu cho món hầm.", 53000, 1500, meatCategory, true));
            products.add(new Product("Nạm đùi trâu Ấn mã 11", "Công dụng, chế biến: bò bít tết, lúc lắc, bò hấp/hầm/nướng/tẩm sốt, phở bò/bún bò nạm,...", 99000, 200, meatCategory, true));
            products.add(new Product("Dựng heo sau Tonnies", "Công dụng, chế biến: hủ tiếu giò, giò khoanh nướng/chiên/hầm,...", 31000, 268, meatCategory, true));
            products.add(new Product("Hành tím", "Công dụng, chế biến: chế biến gia vị, nêm nếm, hành phi,…", 11000, 50, vegetableCategory, true));
            products.add(new Product("Tỏi củ", "Công dụng, chế biến: chế biến gia vị, nêm nếm, tỏi phi,…", 12000, 50, vegetableCategory, true));
            products.add(new Product("Ba rọi heo còn sườn Nga Miratog", "Công dụng, chế biến: thịt ba rọi chiên/nướng/luộc/kho/quay,...", 82000, 90, meatCategory, true));
            products.add(new Product("Thịt Heo xay Schwede", "Công dụng, chế biến: thịt xào, thịt viên, nấu canh, chả/xúc xích heo,...", 63000, 450, meatCategory, true));
            this.productRepository.saveAll(products);
        }

        if(countRole > 0 && countUser > 0 && countCategory > 0 && countProduct > 0){
            logger.info("Skip initialize Data");
        } else {
            logger.info("Data Initialized!");
        }
    }
}
