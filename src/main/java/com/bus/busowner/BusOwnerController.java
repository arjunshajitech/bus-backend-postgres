package com.bus.busowner;

import com.bus.config.AppConfig;
import com.bus.config.CookieHelper;
import com.bus.config.PasswordEncoder;
import com.bus.exception.CustomBadRequestException;
import com.bus.exception.CustomUnauthorizedException;
import com.bus.repository.BusRepository;
import com.bus.repository.BusRouteRepository;
import com.bus.repository.SubRouteRepository;
import com.bus.repository.UserRepository;
import com.bus.request.CreateBus;
import com.bus.request.CreateBusRoute;
import com.bus.request.CreateSubRoute;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/busowner")
@RequiredArgsConstructor
public class BusOwnerController {

    final UserRepository userRepository;
    final BusRepository busRepository;
    final BusRouteRepository busRouteRepository;
    final SubRouteRepository subRouteRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest req, HttpServletResponse res)
            throws CustomBadRequestException {

        User user;
        if (AppConfig.isEmail(request.getEmailOrPhone())) {
            user = getUser(request.getEmailOrPhone(),"EMAIL");
        } else if (AppConfig.isPhone(request.getEmailOrPhone())) {
            user = getUser(request.getEmailOrPhone(),"PHONE");
        } else throw new CustomBadRequestException("Invalid Email or Phone");

        if (!PasswordEncoder.isPasswordMatch(request.getPassword(),user.getPassword()))
            throw new CustomBadRequestException("Bad Credentials");

        setCookie(res,user);
        return ResponseEntity.status(200).body(Map.of("message","Success"));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response) {
        CookieHelper.deleteBusOwnerCookie(request,response);
        return ResponseEntity.status(200).body(Map.of("message", "Success"));
    }



    @PostMapping("/bus")
    public ResponseEntity<?> createBus(@RequestBody CreateBus request,HttpServletRequest req)
            throws CustomUnauthorizedException, CustomBadRequestException {

        User user = verifyLoginAndReturnUser(req);

        boolean isBusExists = busRepository.existsByRegistrationNumber(request.getRegistrationNumber());
        if (isBusExists) throw new CustomBadRequestException("Bus Already Exists");

        busRepository.save(new Bus(user.getId(),user.getFirstName() + " " + user.getLastName(),
                request.getBusName(), request.getRegistrationNumber()));
        return ResponseEntity.status(200).body(Map.of("message","Success"));
    }

    @GetMapping("/bus")
    public ResponseEntity<?> getAllBus(HttpServletRequest request) throws CustomUnauthorizedException {
        User user = verifyLoginAndReturnUser(request);
        return ResponseEntity.status(200).body(busRepository.findAllByOwnerId(user.getId()));
    }

    @DeleteMapping("/bus/{id}")
    public ResponseEntity<?> deleteBus(@PathVariable("id") UUID id,
                                       HttpServletRequest request) throws CustomUnauthorizedException {

        User user = verifyLoginAndReturnUser(request);
        Bus bus = busRepository.findByIdAndOwnerId(id,user.getId());
        if (bus != null) {
            List<Route> routeList = busRouteRepository.findAllByBusId(id);
            List<SubRoute> subRouteList = subRouteRepository.findAllByBusId(id);
            busRepository.delete(bus);
            busRouteRepository.deleteAll(routeList);
            subRouteRepository.deleteAll(subRouteList);
        }
        return ResponseEntity.status(200).body(Map.of("message","Success"));
    }


    @PostMapping("/routes")
    public ResponseEntity<?> createBusRoutes(@RequestBody CreateBusRoute request,HttpServletRequest req)
            throws CustomUnauthorizedException, CustomBadRequestException {

        User user = verifyLoginAndReturnUser(req);
        Optional<Bus> bus = busRepository.findById(request.getBusId());
        if (bus.isEmpty())
            throw new CustomBadRequestException("Bus not found.");

        List<Route> routeList = busRouteRepository.findByBusIdAndDay(request.getBusId(),request.getDay());
        if (!routeList.isEmpty()) {
            // todo : validate timing
        }

        busRouteRepository.save(new Route(
                user.getId(),bus.get().getId(),request.getStartLocation(),
                request.getEndLocation(),request.getStartTime(),request.getEndTime(),request.getDay()
        ));
        return ResponseEntity.status(200).body(Map.of("message","Success"));
    }

    @GetMapping("/routes")
    public ResponseEntity<?> getAllRoutes(HttpServletRequest request) throws CustomUnauthorizedException {
        User user = verifyLoginAndReturnUser(request);
        return ResponseEntity.status(200).body(busRouteRepository.findAllByOwnerId(user.getId()));
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<?> deleteBusRoute(@PathVariable("id") UUID id,
                                       HttpServletRequest request) throws CustomUnauthorizedException {

        User user = verifyLoginAndReturnUser(request);
        Route route = busRouteRepository.findByIdAndOwnerId(id,user.getId());
        if (route != null) {
            List<SubRoute> subRouteList = subRouteRepository.findAllByRouteId(id);
            subRouteRepository.deleteAll(subRouteList);
            busRouteRepository.delete(route);
        }
        return ResponseEntity.status(200).body(Map.of("message","Success"));
    }


    @PostMapping("/sub/routes")
    public ResponseEntity<?> createBusSubRoutes(@RequestBody CreateSubRoute request,HttpServletRequest req)
            throws CustomUnauthorizedException, CustomBadRequestException {

        User user = verifyLoginAndReturnUser(req);
        Optional<Route> route = busRouteRepository.findById(request.getRouteId());
        if (route.isEmpty())
            throw new CustomBadRequestException("Route not found.");

        List<SubRoute> subRouteList = subRouteRepository.findAllByRouteId(request.getRouteId());
        if (!subRouteList.isEmpty()) {
            // todo : validate
        }
        subRouteRepository.save(new SubRoute(
                route.get().getBusId(),user.getId(),route.get().getId(),request.getLocation(),request.getBusTime()
        ));
        return ResponseEntity.status(200).body(Map.of("message","Success"));
    }

    @GetMapping("/sub/routes")
    public ResponseEntity<?> getAllSubRoutes(HttpServletRequest request) throws CustomUnauthorizedException {
        User user = verifyLoginAndReturnUser(request);
        return ResponseEntity.status(200).body(subRouteRepository.findAllByOwnerId(user.getId()));
    }

    @DeleteMapping("/sub/routes/{id}")
    public ResponseEntity<?> deleteBusSubRoute(@PathVariable("id") UUID id,
                                            HttpServletRequest request) throws CustomUnauthorizedException {

        User user = verifyLoginAndReturnUser(request);
        SubRoute subRoute = subRouteRepository.findByIdAndOwnerId(id,user.getId());
        if (subRoute != null) {
            subRouteRepository.delete(subRoute);
        }
        return ResponseEntity.status(200).body(Map.of("message","Success"));
    }



    private void setCookie(HttpServletResponse res, User user) {
        UUID cookieId = UUID.randomUUID();
        user.setCookie(cookieId);
        Cookie cookie = new Cookie("BUS_OWNER_COOKIE", cookieId.toString());
        res.addCookie(cookie);
        userRepository.save(user);
    }

    private User getUser(String emailOrPhone, String type) throws CustomBadRequestException {
        User user;
        if (type.equals("EMAIL")) {
            user = userRepository.findByEmailAndRole(emailOrPhone,UserRole.BUS_OWNER);
        } else {
            user = userRepository.findByPhoneAndRole(emailOrPhone,UserRole.BUS_OWNER);
        }
        if (user == null)
            throw new CustomBadRequestException("Bad Credentials");
        return user;
    }

    private User verifyLoginAndReturnUser(HttpServletRequest req) throws CustomUnauthorizedException {
        String cookie = CookieHelper.getBusOwnerCookieValue(req);
        if (cookie == null)
           throw new CustomUnauthorizedException("Unauthorized");
        User user = userRepository.findByCookie(UUID.fromString(cookie));
        if (user == null)
            throw new CustomUnauthorizedException("Unauthorized");
        return user;
    }
}
