package com.personalfinanceapp.frontend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.personalfinanceapp.frontend.model.Enquiry;
import com.personalfinanceapp.frontend.model.Ticket;
import com.personalfinanceapp.frontend.model.TicketStatusEnum;
import com.personalfinanceapp.frontend.service.EnquiryService;
import com.personalfinanceapp.frontend.service.TicketService;

import java.time.LocalDateTime;


@Controller
@RequestMapping("/admin")
public class AdminTicketController {
    @Autowired
    private EnquiryService enqService;

    @Autowired
    private TicketService tikService;

    // *** mock need to edit again
    @GetMapping("/dashboard")
    public String view(Model model) {
        List<Enquiry> enquiries = enqService.viewDashboard();
        List<Enquiry> openEnquiries = enqService.getOpenEnquiry();
        model.addAttribute("open", openEnquiries);
        model.addAttribute("enquiries", enquiries);
        return "admin/dashboard";
    }

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

    // @PostMapping("/save")
    // public String saveTik(String reply) {
    //     // if (result.hasErrors()) {
    //     //     return "admin/reply";
    //     // }

    //     Ticket newTik = new Ticket(reply,TicketStatusEnum.OPEN,LocalDateTime.now());
        
    //     tikService.create(newTik);

    //     return "redirect:/admin/enquiries/";
    // }

    @GetMapping("/view/{id}")
    public String replyEnq(@PathVariable int id, Model model){
        Enquiry enquiry = enqService.getOneEnquiry(id);
        model.addAttribute("enquiry", enquiry);
       // model.addAttribute("ticket", enquiry.getTicket());
        model.addAttribute("ticket", enquiry.getTicket());
        return "admin/reply";
    }
    
    @PostMapping("/update")
    public String reply(String id, String reply,Model model) {

        // if (result.hasErrors()) {
        //     return "admin/reply";
        // }

        Ticket currticket = enqService.getOneEnquiry(Integer.parseInt(id)).getTicket();
        currticket.setReply(reply);
        currticket.setReply_dateTime(LocalDateTime.now());
        
        tikService.update(currticket);
        tikService.sendEmail(Integer.parseInt(id));

        return "redirect:/admin/enquiries/";
    }

    @PostMapping("/close_ticket")
    public String closeTicket(String id, String reply,Model model) {

        // if (result.hasErrors()) {
        //     return "admin/reply";
        // }

        Ticket currticket = enqService.getOneEnquiry(Integer.parseInt(id)).getTicket();

        currticket.setReply(reply);
        currticket.setReply_dateTime(LocalDateTime.now());
        currticket.setTikStatus(TicketStatusEnum.CLOSED);

        tikService.update(currticket);
        tikService.sendReview(Integer.parseInt(id));

        return "redirect:/admin/enquiries/";
    }

}