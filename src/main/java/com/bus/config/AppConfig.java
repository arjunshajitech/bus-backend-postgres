package com.bus.config;

import com.bus.repository.UserRepository;
import com.bus.tables.User;
import com.bus.tables.enumerations.UserRole;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class AppConfig {

    final UserRepository userRepository;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_REGEX = "^(?:\\+?\\d{1,3}[-.●\\s]?)?\\(?(?:\\d{2,3})\\)?[-.●\\s]?\\d{3,4}[-.●\\s]?\\d{4}$";

    @PostConstruct
    public void storeDefaultAdmin() {
        log.info("############# post construct initiated ################");
        List<User> userList = new ArrayList<>();
        userList.add(new User("Admin","Admin",null,"admin@admin.com",PasswordEncoder.encodePassword("Password@1"), UserRole.ADMIN));
        userList.add(new User("Super","Admin",null,"superadmin@admin.com",PasswordEncoder.encodePassword("Password@1"),UserRole.ADMIN));

        userList.forEach(user -> {
            boolean isUserExists = userRepository.existsByEmailAndRole(user.getEmail(),user.getRole());
            if (!isUserExists)
                userRepository.save(user);
        });
    }

    public static boolean isEmail(String input) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static boolean isPhone(String input) {
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

}