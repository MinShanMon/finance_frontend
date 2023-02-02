package com.personalfinanceapp.frontend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.personalfinanceapp.frontend.model.Enquiry;
import com.personalfinanceapp.frontend.model.Ticket;
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
        List<Enquiry> openEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        equals(TicketStatusEnum.OPEN)).collect(Collectors.toList());

        List<Enquiry> closeEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        equals(TicketStatusEnum.CLOSED)).collect(Collectors.toList());

        model.addAttribute("enquiries", enquiries);
        model.addAttribute("openSum", openEnquiries.size());
        model.addAttribute("closeSum", closeEnquiries.size());
        return "admin/inbox";
    }
    
    @GetMapping("/enquiries/open")
    public String viewOpenEnquiries(Model model) {
        List<Enquiry> openEnquiries = enqService.getOpenEnquiry();
        model.addAttribute("open", openEnquiries);
        return "admin/open-tickets";
    }

    @GetMapping("/enquiries/closed")
    public String viewClosedEnquiries(Model model) {
        List<Enquiry> closedEnquiries = enqService.getClosedEnquiry();
        model.addAttribute("closed", closedEnquiries);
        return "admin/closed-tickets";
    }
    
    @GetMapping("/reply/{id}")
    public String replyEnq(@PathVariable int id, Model model){
        Enquiry enquiry = enqService.getOneEnquiry(id);
        model.addAttribute("enq", enquiry);
        return "admin/reply";
    }

}