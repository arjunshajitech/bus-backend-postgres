package com.bus.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieHelper {

    public static String getBusOwnerCookieValue(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("BUS_OWNER_COOKIE")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getAdminCookieValue(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("ADMIN_COOKIE")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getUserCookieValue(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("USER_COOKIE")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void deleteAdminCookie(HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("ADMIN_COOKIE")) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setSecure(true);
                    res.addCookie(cookie);
                    break;
                }
            }
        }
    }

    public static void deleteBusOwnerCookie(HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("BUS_OWNER_COOKIE")) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    res.addCookie(cookie);
                    break;
                }
            }
        }
    }

    public static void deleteUserCookie(HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("USER_COOKIE")) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    res.addCookie(cookie);
                    break;
                }
            }
        }
    }


}
