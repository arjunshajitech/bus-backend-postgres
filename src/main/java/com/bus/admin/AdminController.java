package com.bus.admin;

import com.bus.config.AppConfig;
import com.bus.config.CookieHelper;
import com.bus.config.PasswordEncoder;
import com.bus.exception.CustomBadRequestException;
import com.bus.exception.CustomUnauthorizedException;
import com.bus.repository.BusRepository;
import com.bus.repository.BusRouteRepository;
import com.bus.repository.SubRouteRepository;
import com.bus.repository.UserRepository;
import com.bus.request.CreateBusOwner;
import com.bus.request.LoginRequest;
import com.bus.tables.Bus;
import com.bus.tables.Route;
import com.bus.tables.SubRoute;
import com.bus.tables.User;
import com.bus.tables.enumerations.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    final UserRepository userRepository;
    final BusRepository busRepository;
    final BusRouteRepository busRouteRepository;
    final SubRouteRepository subRouteRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse res) throws CustomBadRequestException {
        User user = getUser(request.getEmailOrPhone());
        if (user == null)
            throw new CustomBadRequestException("Bad Credentials");
        if (!PasswordEncoder.isPasswordMatch(request.getPassword(), user.getPassword()))
            throw new CustomBadRequestException("Bad Credentials");
        setCookie(res, user);

        log.info("\033[1;92m ADMIN | Login Successful.\033[0m");
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) throws CustomUnauthorizedException {
        User user = verifyLogin(request);
        log.info("\033[1;92m ADMIN | Get Profile Successful.\033[0m");
        return ResponseEntity.status(200).body(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieHelper.deleteAdminCookie(request, response);
        log.info("\033[1;92m ADMIN | Logout Successful.\033[0m");
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }

    @PostMapping("/bus/owner")
    public ResponseEntity<?> createBusOwner(@RequestBody CreateBusOwner request, HttpServletRequest req)
            throws CustomUnauthorizedException, CustomBadRequestException {

        verifyLogin(req);
        boolean isBusOwnerEmailExists = userRepository.existsByEmailAndRole(request.getEmail(), request.getRole());
        boolean isBusOwnerPhoneExists = userRepository.existsByPhoneAndRole(request.getPhone(), request.getRole());
        if (isBusOwnerPhoneExists || isBusOwnerEmailExists)
            throw new CustomBadRequestException("Email or Phone Already Exists.");

        userRepository.save(new User(
                request.getFirstName(), request.getLastName(), request.getPhone(), request.getEmail(),
                PasswordEncoder.encodePassword(request.getPassword()), request.getRole()
        ));
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) throws CustomUnauthorizedException {
        verifyLogin(request);
        return ResponseEntity.status(200).body(userRepository.findAllByRole(UserRole.USER));
    }

    @GetMapping("/busowners")
    public ResponseEntity<?> getAllBusOwners(HttpServletRequest request) throws CustomUnauthorizedException {
        verifyLogin(request);
        return ResponseEntity.status(200).body(userRepository.findAllByRole(UserRole.BUS_OWNER));
    }

    @DeleteMapping("/busowner/{id}")
    public ResponseEntity<?> deleteBusOwners(@PathVariable("id") UUID id, HttpServletRequest request)
            throws CustomUnauthorizedException {

        verifyLogin(request);
        User user = userRepository.findByIdAndRole(id, UserRole.BUS_OWNER);
        if (user != null) {
            List<Bus> busList = busRepository.findAllByOwnerId(id);
            List<Route> routeList = busRouteRepository.findAllByOwnerId(id);
            List<SubRoute> subRouteList = subRouteRepository.findAllByOwnerId(id);
            userRepository.delete(user);
            busRepository.deleteAll(busList);
            busRouteRepository.deleteAll(routeList);
            subRouteRepository.deleteAll(subRouteList);
        }
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }

    @PutMapping("/busowner/{id}")
    public ResponseEntity<?> editBusOwner(@PathVariable("id") UUID id, @RequestBody CreateBusOwner createBusOwner,
                                          HttpServletRequest request) throws CustomUnauthorizedException, CustomBadRequestException {

        verifyLogin(request);
        User user = userRepository.findByIdAndRole(id, UserRole.BUS_OWNER);
        if (user == null)
            throw new CustomBadRequestException("Bus owner not found");

        if (!user.getEmail().equals(createBusOwner.getEmail())) {
            boolean isEmailExists = userRepository.existsByEmailAndRole(createBusOwner.getEmail(), UserRole.BUS_OWNER);
            if (isEmailExists)
                throw new CustomBadRequestException("Email or Phone already exists");
        }
        if (!user.getPhone().equals(createBusOwner.getPhone())) {
            boolean isPhoneExists = userRepository.existsByPhoneAndRole(createBusOwner.getPhone(), UserRole.BUS_OWNER);
            if (isPhoneExists)
                throw new CustomBadRequestException("Email or Phone already exists");
        }
        user.setFirstName(createBusOwner.getFirstName());
        user.setLastName(createBusOwner.getLastName());
        user.setEmail(createBusOwner.getEmail());
        user.setPhone(createBusOwner.getPhone());
        userRepository.save(user);
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }

    @PutMapping("/busowner/password/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable("id") UUID id,
                                            @RequestParam("password") String password,
                                            HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        verifyLogin(request);
        User user = userRepository.findByIdAndRole(id, UserRole.BUS_OWNER);
        if (user == null)
            throw new CustomBadRequestException("Bus owner not found");

        user.setPassword(PasswordEncoder.encodePassword(password));
        userRepository.save(user);
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }


    private User verifyLogin(HttpServletRequest req) throws CustomUnauthorizedException {
        String cookie = CookieHelper.getAdminCookieValue(req);
        if (cookie == null)
            throw new CustomUnauthorizedException("Unauthorized");
        User user = userRepository.findByCookie(UUID.fromString(cookie));
        if (user == null)
            throw new CustomUnauthorizedException("Unauthorized");
        return user;
    }

    private void setCookie(HttpServletResponse res, User user) {
        UUID cookieId = UUID.randomUUID();
        user.setCookie(cookieId);
        Cookie cookie = new Cookie("ADMIN_COOKIE", cookieId.toString());
        cookie.setPath("/");
        cookie.setSecure(true);
        res.addCookie(cookie);
        userRepository.save(user);
    }

    private User getUser(String emailOrPhone) {
        return userRepository.findByEmailAndRole(emailOrPhone, UserRole.ADMIN);
    }
}
