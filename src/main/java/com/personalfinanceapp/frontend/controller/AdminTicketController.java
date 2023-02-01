package com.personalfinanceapp.frontend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.personalfinanceapp.frontend.model.Enquiry;
import com.personalfinanceapp.frontend.model.TicketStatusEnum;
import com.personalfinanceapp.frontend.service.EnquiryService;

@Controller
@RequestMapping("/admin")
public class AdminTicketController {
    @Autowired
    private EnquiryService enqService;

    @GetMapping("/enquiries")
    public String viewEnquiries(Model model) {
        List<Enquiry> enquiries = enqService.getAllEnquiry();
        // List<Enquiry> openEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        // equals(TicketStatusEnum.OPEN)).collect(Collectors.toList());

        // List<Enquiry> closeEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        // equals(TicketStatusEnum.CLOSED)).collect(Collectors.toList());

        model.addAttribute("enquiries", enquiries);
        // model.addAttribute("open", openEnquiries.size());
        // model.addAttribute("close", closeEnquiries.size());
        return "admin/inbox";
    }

}