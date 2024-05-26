package com.bus.user;


import com.bus.config.AppConfig;
import com.bus.config.CookieHelper;
import com.bus.config.PasswordEncoder;
import com.bus.exception.CustomBadRequestException;
import com.bus.exception.CustomUnauthorizedException;
import com.bus.repository.BusRepository;
import com.bus.repository.BusRouteRepository;
import com.bus.repository.SubRouteRepository;
import com.bus.repository.UserRepository;
import com.bus.request.LoginRequest;
import com.bus.request.UserSignup;
import com.bus.response.BusResponse;
import com.bus.response.BusRoutes;
import com.bus.response.BusSubRoutes;
import com.bus.tables.Bus;
import com.bus.tables.Route;
import com.bus.tables.SubRoute;
import com.bus.tables.User;
import com.bus.tables.enumerations.UserRole;
import com.bus.tables.enumerations.WeekDay;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    final UserRepository userRepository;
    final BusRepository busRepository;
    final BusRouteRepository busRouteRepository;
    final SubRouteRepository subRouteRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignup request) throws CustomBadRequestException {

        boolean isBusOwnerEmailExists = userRepository.existsByEmailAndRole(request.getEmail(), request.getRole());
        boolean isBusOwnerPhoneExists = userRepository.existsByPhoneAndRole(request.getPhone(), request.getRole());
        if (isBusOwnerPhoneExists || isBusOwnerEmailExists)
            throw new CustomBadRequestException("Email or Phone Already Exists");

        userRepository.save(new User(
                request.getFirstName(), request.getLastName(), request.getPhone(), request.getEmail(),
                PasswordEncoder.encodePassword(request.getPassword()), request.getRole()
        ));
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse res)
            throws CustomBadRequestException {

        User user;
        if (AppConfig.isEmail(request.getEmailOrPhone())) {
            user = getUser(request.getEmailOrPhone(), "EMAIL");

        } else if (AppConfig.isPhone(request.getEmailOrPhone())) {

            user = getUser(request.getEmailOrPhone(), "PHONE");
        } else throw new CustomBadRequestException("Invalid Email or Phone");

        if (!PasswordEncoder.isPasswordMatch(request.getPassword(), user.getPassword()))
            throw new CustomBadRequestException("Bad Credentials");

        setCookie(res, user);
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) throws CustomUnauthorizedException {
        User user = verifyLoginAndReturnUser(request);
        return ResponseEntity.status(200).body(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieHelper.deleteUserCookie(request, response);
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }

    @GetMapping("/routes")
    public ResponseEntity<?> getAllRoutes(@RequestParam("day") WeekDay day,
                                          HttpServletRequest request) throws CustomUnauthorizedException {
        User user = verifyLoginAndReturnUser(request);
        List<BusRoutes> busRoutesList = new ArrayList<>();

        List<Route> busRoutes = busRouteRepository.findAllByDay(day);
        if (!busRoutes.isEmpty()) {
            busRoutes.forEach(route -> {
                Bus bus = busRepository.findOneById(route.getBusId());
                if (bus != null) {
                    LocalTime time = LocalTime.now();
                    boolean started = time.isAfter(route.getStartTime()) && time.isBefore(route.getEndTime());
                    boolean ended = time.isAfter(route.getEndTime());
                    busRoutesList.add(new BusRoutes(route,bus.getBusName(),bus.getOwnerName(),started,ended));
                }
            });
        }

        return ResponseEntity.status(200).body(busRoutesList);
    }

    @GetMapping("/sub/routes/{id}")
    public ResponseEntity<?> getAllSubRoutes(@PathVariable("id") UUID id,
                                             HttpServletRequest request) throws CustomUnauthorizedException {

        User user = verifyLoginAndReturnUser(request);
        List<BusSubRoutes> busSubRoutes = new ArrayList<>();
        List<SubRoute> busRoutes = subRouteRepository.findAllByRouteId(id);
        Route route = busRouteRepository.findById(id).orElse(null);
        if (route != null) {
            SubRoute sr = new SubRoute(null,null,null,route.getStartLocation(),route.getStartTime());
            busSubRoutes.add(new BusSubRoutes(sr,true));
            busRoutes.forEach(subRoute -> {
                boolean completed = subRoute.getBusTime().isBefore(LocalTime.now());
                busSubRoutes.add(new BusSubRoutes(subRoute,completed));
            });
            SubRoute srr = new SubRoute(null,null,null,route.getEndLocation(),route.getEndTime());
            busSubRoutes.add(new BusSubRoutes(srr,true));
        }

        return ResponseEntity.status(200).body(busSubRoutes);
    }


    private void setCookie(HttpServletResponse res, User user) {
        UUID cookieId = UUID.randomUUID();
        user.setCookie(cookieId);
        Cookie cookie = new Cookie("USER_COOKIE", cookieId.toString());
        res.addCookie(cookie);
        userRepository.save(user);
    }

    private User getUser(String emailOrPhone, String type) throws CustomBadRequestException {
        User user;
        if (type.equals("EMAIL")) {
            user = userRepository.findByEmailAndRole(emailOrPhone, UserRole.USER);
        } else {
            user = userRepository.findByPhoneAndRole(emailOrPhone, UserRole.USER);
        }
        if (user == null)
            throw new CustomBadRequestException("Bad Credentials");
        return user;
    }

    private User verifyLoginAndReturnUser(HttpServletRequest req) throws CustomUnauthorizedException {
        String cookie = CookieHelper.getUserCookieValue(req);
        if (cookie == null)
            throw new CustomUnauthorizedException("Unauthorized");
        User user = userRepository.findByCookie(UUID.fromString(cookie));
        if (user == null)
            throw new CustomUnauthorizedException("Unauthorized");
        return user;
    }
}
