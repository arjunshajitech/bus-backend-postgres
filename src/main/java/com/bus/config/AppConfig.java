package com.bus.config;

import com.bus.repository.BusRepository;
import com.bus.repository.BusRouteRepository;
import com.bus.repository.SubRouteRepository;
import com.bus.repository.UserRepository;
import com.bus.response.BusSubRoutes;
import com.bus.tables.Bus;
import com.bus.tables.Route;
import com.bus.tables.User;
import com.bus.tables.enumerations.UserRole;
import com.bus.tables.enumerations.WeekDay;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class AppConfig {

    final UserRepository userRepository;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_REGEX = "^(?:\\+?\\d{1,3}[-.●\\s]?)?\\(?(?:\\d{2,3})\\)?[-.●\\s]?\\d{3,4}[-.●\\s]?\\d{4}$";
    private final BusRouteRepository busRouteRepository;
    private final BusRepository busRepository;
    private final SubRouteRepository subRouteRepository;

    @PostConstruct
    public void storeDefaultAdmin() {
        log.info("############# post construct initiated ################");
        List<User> userList = new ArrayList<>();
        userList.add(new User("Admin", "Admin", null, "admin@admin.com", PasswordEncoder.encodePassword("111111"), UserRole.ADMIN));
        userList.add(new User("Super", "Admin", null, "superadmin@admin.com", PasswordEncoder.encodePassword("111111"), UserRole.ADMIN));
        userList.add(new User("Default", "User", "0000000000", "user@user.com", PasswordEncoder.encodePassword("111111"), UserRole.USER));

        userList.forEach(user -> {
            boolean isUserExists = userRepository.existsByEmailAndRole(user.getEmail(), user.getRole());
            if (!isUserExists)
                userRepository.save(user);
        });

        initBusOwnerAndRouteAndSubRoute();
    }

    private void initBusOwnerAndRouteAndSubRoute() {

        boolean notExists = userRepository.existsByEmailAndRole("bus@bus.com", UserRole.BUS_OWNER);
        if (!notExists) {
            User busOwner = userRepository.save(new User(
                    "Default", "BusOwner", "0000000001", "bus@bus.com", PasswordEncoder.encodePassword("111111"), UserRole.BUS_OWNER)
            );

            Bus bus = busRepository.save(new Bus(busOwner.getId(), "Default BusOwner", "The Rock", "KL 784512"));

            List<Route> busRouteList = new ArrayList<>();
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Muhamma, Alappuzha", "Alappuzha, Kerala", LocalTime.of(8, 0, 0), LocalTime.of(9, 0, 0), WeekDay.MONDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Muhamma, Alappuzha", "Alappuzha, Kerala", LocalTime.of(8, 0, 0), LocalTime.of(9, 0, 0), WeekDay.TUESDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Muhamma, Alappuzha", "Alappuzha, Kerala", LocalTime.of(8, 0, 0), LocalTime.of(9, 0, 0), WeekDay.WEDNESDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Muhamma, Alappuzha", "Alappuzha, Kerala", LocalTime.of(8, 0, 0), LocalTime.of(9, 0, 0), WeekDay.THURSDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Muhamma, Alappuzha", "Alappuzha, Kerala", LocalTime.of(8, 0, 0), LocalTime.of(9, 0, 0), WeekDay.FRIDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Muhamma, Alappuzha", "Alappuzha, Kerala", LocalTime.of(8, 0, 0), LocalTime.of(9, 0, 0), WeekDay.SATURDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Muhamma, Alappuzha", "Alappuzha, Kerala", LocalTime.of(8, 0, 0), LocalTime.of(9, 0, 0), WeekDay.SUNDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Mannamcherry, Alappuzha", LocalTime.of(10, 30, 0), LocalTime.of(11, 30, 0), WeekDay.MONDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Mannamcherry, Alappuzha", LocalTime.of(10, 30, 0), LocalTime.of(11, 30, 0), WeekDay.TUESDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Mannamcherry, Alappuzha", LocalTime.of(10, 30, 0), LocalTime.of(11, 30, 0), WeekDay.WEDNESDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Mannamcherry, Alappuzha", LocalTime.of(10, 30, 0), LocalTime.of(11, 30, 0), WeekDay.THURSDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Mannamcherry, Alappuzha", LocalTime.of(10, 30, 0), LocalTime.of(11, 30, 0), WeekDay.FRIDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Mannamcherry, Alappuzha", LocalTime.of(10, 30, 0), LocalTime.of(11, 30, 0), WeekDay.SATURDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Mannamcherry, Alappuzha", LocalTime.of(10, 30, 0), LocalTime.of(11, 30, 0), WeekDay.SUNDAY));

            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Cherthala, Alappuzha", "Muhamma, Alappuzha", LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), WeekDay.MONDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Cherthala, Alappuzha", "Muhamma, Alappuzha", LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), WeekDay.TUESDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Cherthala, Alappuzha", "Muhamma, Alappuzha", LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), WeekDay.WEDNESDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Cherthala, Alappuzha", "Muhamma, Alappuzha", LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), WeekDay.THURSDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Cherthala, Alappuzha", "Muhamma, Alappuzha", LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), WeekDay.FRIDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Cherthala, Alappuzha", "Muhamma, Alappuzha", LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), WeekDay.SATURDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Cherthala, Alappuzha", "Muhamma, Alappuzha", LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), WeekDay.SUNDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Cherthala, Alappuzha", LocalTime.of(14, 0, 0), LocalTime.of(18, 30, 0), WeekDay.MONDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Cherthala, Alappuzha", LocalTime.of(14, 0, 0), LocalTime.of(18, 30, 0), WeekDay.TUESDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Cherthala, Alappuzha", LocalTime.of(14, 0, 0), LocalTime.of(18, 30, 0), WeekDay.WEDNESDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Cherthala, Alappuzha", LocalTime.of(14, 0, 0), LocalTime.of(18, 30, 0), WeekDay.THURSDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Cherthala, Alappuzha", LocalTime.of(14, 0, 0), LocalTime.of(18, 30, 0), WeekDay.FRIDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Cherthala, Alappuzha", LocalTime.of(14, 0, 0), LocalTime.of(18, 30, 0), WeekDay.SATURDAY));
            busRouteList.add(new Route(busOwner.getId(), bus.getId(), "Alappuzha, Kerala", "Cherthala, Alappuzha", LocalTime.of(14, 0, 0), LocalTime.of(18, 30, 0), WeekDay.SUNDAY));
            
            busRouteRepository.saveAll(busRouteList);
        }
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
