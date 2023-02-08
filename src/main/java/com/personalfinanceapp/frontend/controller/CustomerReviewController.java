package com.personalfinanceapp.frontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.personalfinanceapp.frontend.model.Enquiry;
import com.personalfinanceapp.frontend.service.ReviewService;

@Controller
@RequestMapping("/customer")
public class CustomerReviewController {
    @Autowired
    private ReviewService rivService;

    @GetMapping("/review/{id}")
    public String reviewEnq(@PathVariable int id, Model model){
        Enquiry enquiry = rivService.getOneReview(id);
        model.addAttribute("enquiry", enquiry);
       // model.addAttribute("ticket", enquiry.getTicket());
        return "customer/review";
    }
}    
    