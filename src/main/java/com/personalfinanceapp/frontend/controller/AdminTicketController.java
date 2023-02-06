package com.personalfinanceapp.frontend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import com.personalfinanceapp.frontend.model.Enquiry;
import com.personalfinanceapp.frontend.model.Ticket;
import com.personalfinanceapp.frontend.model.TicketStatusEnum;
import com.personalfinanceapp.frontend.service.EnquiryService;
import com.personalfinanceapp.frontend.service.TicketService;
import javax.validation.*;
import java.time.LocalDateTime;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminTicketController {
    @Autowired
    private EnquiryService enqService;

    @Autowired
    private TicketService tikService;

    // @InitBinder("tik")
    // private void initLeaveBinder(WebDataBinder binder) {
    //     binder.addValidators(tikValidator);
    // }

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

    @PostMapping("/save")
    public String saveMember(@Valid @ModelAttribute("tik") Ticket tik, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/reply";
        }

        Ticket newTik = new Ticket();
        newTik.setTikStatus(tik.getTikStatus());
        newTik.setReply(tik.getReply());
        newTik.setReply_dateTime(LocalDateTime.now());
        tikService.create(newTik);

        return "redirect:/admin/inbox";
    }

    @GetMapping("/view/{id}")
    public String replyEnq(@PathVariable int id, Model model){
        Enquiry enquiry = enqService.getOneEnquiry(id);
        model.addAttribute("enquiry", enquiry);
        model.addAttribute("tik", tikService.update(enquiry.getTicket()));
        return "admin/reply";
    }

    @PutMapping("/update")
    public String reply(@Valid @ModelAttribute("tik") Ticket tik, BindingResult result, Model model, RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            return "admin/reply";
        }
        tikService.update(tik);
        return "redirect:/admin/inbox";
    }


    // @PostMapping("/reply/{id}")
    // public String approveOrRejectCourse(@ModelAttribute("tik") @Valid Ticket tik, BindingResult result,
    //  @PathVariable Integer id,Model model){
        
    //     return "redirect:/admin/inbox";
    // }   
 

    // @PostMapping("/reply/{id}")
    // public String editTikStatus(@ModelAttribute("status") @Valid CloseTicket close, BindingResult result,
    // @PathVariable Integer id){
    //     if(result.hasErrors()){
    //         return "admin/reply";
    //     }

    //     Ticket tik = tikService.getLeaveWithLeaveId(id);
    //     if(close.getDecision().trim().equalsIgnoreCase(TicketStatusEnum.CLOSED.toString())){
    //         tik.setTikStatus(TicketStatusEnum.CLOSED);            
    //         //tik.setReason(close.getReason());
    //     }
    //     else{
    //         tik.setTikStatus(TicketStatusEnum.OPEN);
    //        // l.setReason(approve.getReason());
    //     }
    //     tikService.approvLeave(tik);
    //     return "redirect:/admin/inbox";
    // }

}