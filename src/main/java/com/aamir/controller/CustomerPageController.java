package com.aamir.controller;

import com.aamir.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CustomerPageController {

    private final CustomerRepository customerRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("customersData", customerRepository.findAll());
        return "customers";
    }

    @GetMapping("/other-customer")
    public String otherCustomer(Model model) {
        model.addAttribute("otherCustomersData", customerRepository.findAll());
        return "customersOther";
    }


}
