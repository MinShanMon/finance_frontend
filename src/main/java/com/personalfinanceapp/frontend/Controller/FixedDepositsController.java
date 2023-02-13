package com.personalfinanceapp.frontend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.personalfinanceapp.frontend.Models.Bank;
import com.personalfinanceapp.frontend.Models.FixedDeposits;
import com.personalfinanceapp.frontend.service.BankService;
import com.personalfinanceapp.frontend.service.FixedDepostisService;

import javax.servlet.http.HttpSession;
import com.personalfinanceapp.frontend.model.UserSession;


@Controller
@RequestMapping("/admin")
public class FixedDepositsController{
    
    @Autowired
    BankService bankService;


    @Autowired
    FixedDepostisService fixedDepostisService;



    @GetMapping("/managefixeddeposits/addfixed")
    public String addfixed(Model model, HttpSession session){
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        List<Bank> bankList = bankService.findAllBank(token);
        model.addAttribute("bankList", bankList);
      
        return "admin/addfixed";
    }


    @RequestMapping(value = "/managefixeddeposits/addfixed", method = RequestMethod.POST)
    public String savebank(HttpSession session, String tenure, String minAmount, String maxAmount, String interestRate, String b_id){
        LocalDateTime now = LocalDateTime.now();

        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        FixedDeposits fDeposits = new FixedDeposits(Integer.parseInt(tenure), Integer.parseInt(minAmount), Integer.parseInt(maxAmount),
                                        Double.parseDouble(interestRate),now,bankService.findBankById(Long.parseUnsignedLong(b_id),token));
        fixedDepostisService.addFixedDeposits(fDeposits, token);
        return "redirect:/admin/managefixeddeposits";
    }


    @GetMapping("/managefixeddeposits/deletefixed/{id}")
    public String deleteThroughId(@PathVariable(value = "id") long id, HttpSession session) {
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        fixedDepostisService.deletefixed(id, token);
        return "redirect:/admin/managefixeddeposits";

    }

    @GetMapping("/managefixeddeposits/editfixed/{id}")
    public String editbank(@PathVariable(value = "id") long id,Model model, HttpSession session) {


        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        FixedDeposits fixedDeposits = fixedDepostisService.findbyid(id, token);
        List<Bank> bankList = bankService.findAllBank(token);
        model.addAttribute("bankList", bankList);
        model.addAttribute("fixed", fixedDeposits);
        return "/admin/fixededit";

    }

    @PostMapping("/managefixeddeposits/editfixed/")
    public String editbank(HttpSession session, String id,String tenure, String minAmount, String maxAmount, String interestRate, String b_id) {

        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        FixedDeposits fixedDeposits = fixedDepostisService.findbyid(Long.parseUnsignedLong(id), token);

        fixedDeposits.setTenure(Integer.parseInt(tenure));
        fixedDeposits.setMaxAmount(Integer.parseInt(maxAmount));
        fixedDeposits.setMinAmount(Integer.parseInt(minAmount));
        fixedDeposits.setInterestRate(Double.parseDouble(interestRate));
        fixedDeposits.setFd_bank(bankService.findBankById(Long.parseUnsignedLong(b_id), token));
        fixedDepostisService.editfixed(fixedDeposits, token);
        return "redirect:/admin/managefixeddeposits";

    }


    @GetMapping("/fixedanalysis")
    public String fixedAnalysis(){

        return "admin/bankdepositsanalysis";
    }


}
