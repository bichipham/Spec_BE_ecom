package com.ecom.api.health;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * Redirects the root URL {@code /} to the Swagger UI page.
 *
 * <p>Without this, Spring Boot 3.x throws
 * {@code NoResourceFoundException: No static resource .} for any request
 * to {@code /} because there is no handler and no static index file.
 */
@Controller
public class RootRedirectController {

    @GetMapping("/")
    public void root(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }
}
