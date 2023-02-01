package com.personalfinanceapp.frontend.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.personalfinanceapp.frontend.Models.Bank;
import com.personalfinanceapp.frontend.Models.FixedDeposits;
import com.personalfinanceapp.frontend.Service.BankService;
import com.personalfinanceapp.frontend.Service.FixedDepostisService;


@Controller
@RequestMapping("/admin")
public class FixedDepositsController{
    
    @Autowired
    BankService bankService;


    @Autowired
    FixedDepostisService fixedDepostisService;



    @GetMapping("/managefixeddeposits/addfixed")
    public String addfixed(Model model){
        List<Bank> bankList = bankService.findAllBank();
        model.addAttribute("bankList", bankList);
      
        return "admin/addfixed";
    }

    // @GetMapping("/managefixeddeposits/general")
    // public String retrieveAllBank(Model model){
    //     List<FixedDeposits> fixedList = fixedDepostisService.findAllFixeds();
    //     model.addAttribute("fixedList", fixedList);
    //     return "admin/general";
    // }

    @RequestMapping(value = "/managefixeddeposits/addfixed", method = RequestMethod.POST)
    public String savebank(String tenure, String minAmount, String maxAmount, String interestRate, String b_id){
        LocalDateTime now = LocalDateTime.now();

        FixedDeposits fDeposits = new FixedDeposits(Integer.parseInt(tenure), Integer.parseInt(minAmount), Integer.parseInt(maxAmount),
                                        Double.parseDouble(interestRate),now,bankService.findBankById(Long.parseUnsignedLong(b_id)));
        fixedDepostisService.addFixedDeposits(fDeposits);
        return "redirect:/admin/managefixeddeposits/addbank?success";
    }

}
