package com.drtx.ecomerce.amazon.infrastructure.openapi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScalarApiController {

    @GetMapping("/scalar")
    public String scalar() {
        return "forward:/scalar.html";
    }

    @GetMapping("/scalar.html")
    public String scalarHtml() {
        return "forward:/scalar.html";
    }
}