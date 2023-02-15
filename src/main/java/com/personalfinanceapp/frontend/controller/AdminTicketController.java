package com.personalfinanceapp.frontend.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.hibernate.mapping.Collection;
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
import com.personalfinanceapp.frontend.model.UserSession;
import com.personalfinanceapp.frontend.service.EnquiryService;
import com.personalfinanceapp.frontend.service.TicketService;

import java.time.LocalDateTime;


@Controller
@RequestMapping(value = {"/" ,"/admin"})
public class AdminTicketController {
    @Autowired
    private EnquiryService enqService;

    @Autowired
    private TicketService tikService;

    @GetMapping
    public String defaultPath() {
        return "redirect:/admin/dashboard";
    }


    @GetMapping("/dashboard")
    public String view(Model model, HttpSession session) {
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        List<Enquiry> enquiries = enqService.viewDashboard(token);
        List<Enquiry> openEnquiries = enqService.getOpenEnquiry(token);

        List<Enquiry> rate5 = enquiries.stream().filter(u -> u.getRating()==5).collect(Collectors.toList());
        List<Enquiry> rate4 = enquiries.stream().filter(u -> u.getRating()==4).collect(Collectors.toList());
        List<Enquiry> rate3 = enquiries.stream().filter(u -> u.getRating()==3).collect(Collectors.toList());
        List<Enquiry> rate2 = enquiries.stream().filter(u -> u.getRating()==2).collect(Collectors.toList());
        List<Enquiry> rate1 = enquiries.stream().filter(u -> u.getRating()==1).collect(Collectors.toList());
        model.addAttribute("rate5", rate5.size());
        model.addAttribute("rate4", rate4.size());
        model.addAttribute("rate3", rate3.size());
        model.addAttribute("rate2", rate2.size());
        model.addAttribute("rate1", rate1.size());

        model.addAttribute("open", openEnquiries);
        model.addAttribute("openSum", openEnquiries.size());
        model.addAttribute("enquiries", enquiries);
        return "admin/dashboard";
    }

    @GetMapping("/enquiries")
    public String viewEnquiries(Model model, HttpSession session) {
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        List<Enquiry> enquiries = enqService.getAllEnquiry(token);
        List<Enquiry> openEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        equals(TicketStatusEnum.OPEN)).collect(Collectors.toList());

        List<Enquiry> closeEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        equals(TicketStatusEnum.CLOSED)).collect(Collectors.toList());

      
        model.addAttribute("enquiries", enquiries);
        model.addAttribute("openSum", openEnquiries.size());
        model.addAttribute("closeSum", closeEnquiries.size());
        model.addAttribute("total", enquiries.size());
        return "admin/inbox";
    }
    
    @GetMapping("/enquiries/open")
    public String viewOpenEnquiries(Model model, HttpSession session) {
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();

        List<Enquiry> enquiries = enqService.getAllEnquiry(token);
       

        List<Enquiry> closeEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        equals(TicketStatusEnum.CLOSED)).collect(Collectors.toList());


        List<Enquiry> openEnquiries = enqService.getOpenEnquiry(token);
        model.addAttribute("open", openEnquiries);
        model.addAttribute("openSum", openEnquiries.size());
        model.addAttribute("closeSum", closeEnquiries.size());
        model.addAttribute("total", enquiries.size());
        return "admin/open-tickets";
    }

    @GetMapping("/enquiries/closed")
    public String viewClosedEnquiries(Model model, HttpSession session) {
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        List<Enquiry> closedEnquiries = enqService.getClosedEnquiry(token);

        List<Enquiry> enquiries = enqService.getAllEnquiry(token);
        List<Enquiry> openEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        equals(TicketStatusEnum.OPEN)).collect(Collectors.toList());

        List<Enquiry> closeEnquiries = enquiries.stream().filter(u -> u.getTicket().getTikStatus().
        equals(TicketStatusEnum.CLOSED)).collect(Collectors.toList());


        model.addAttribute("closed", closedEnquiries);
        model.addAttribute("openSum", openEnquiries.size());
        model.addAttribute("closeSum", closeEnquiries.size());
        model.addAttribute("total", enquiries.size());
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
    public String replyEnq(@PathVariable int id, Model model, HttpSession session){
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        Enquiry enquiry = enqService.getOneEnquiry(id, token);
        model.addAttribute("enquiry", enquiry);
       // model.addAttribute("ticket", enquiry.getTicket());
        model.addAttribute("ticket", enquiry.getTicket());
        return "admin/reply";
    }
    
    @PostMapping("/update")
    public String reply(String id, String reply,Model model, HttpSession session) {

        // if (result.hasErrors()) {
        //     return "admin/reply";
        // }

        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        Ticket currticket = enqService.getOneEnquiry(Integer.parseInt(id), token).getTicket();
        currticket.setReply(reply);
        currticket.setReply_dateTime(LocalDateTime.now());
        
        tikService.update(currticket, token);
        tikService.sendEmail(Integer.parseInt(id), token);

        return "redirect:/admin/enquiries/";
    }

    @PostMapping("/close_ticket")
    public String closeTicket(String id, String reply,Model model, HttpSession session) {

        // if (result.hasErrors()) {
        //     return "admin/reply";
        // }

        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        Ticket currticket = enqService.getOneEnquiry(Integer.parseInt(id), token).getTicket();

        currticket.setReply(reply);
        currticket.setReply_dateTime(LocalDateTime.now());
        currticket.setTikStatus(TicketStatusEnum.CLOSED);

        tikService.update(currticket, token);
        tikService.sendReview(Integer.parseInt(id), token);

        return "redirect:/admin/enquiries/";
    }

}