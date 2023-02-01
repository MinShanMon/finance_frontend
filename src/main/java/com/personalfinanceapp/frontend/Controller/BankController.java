package com.personalfinanceapp.frontend.Controller;

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
public class BankController {
    
    @Autowired
    BankService bankService;

    @Autowired
    FixedDepostisService fixedDepostisService;




    @GetMapping("/managefixeddeposits/general")
    public String retrieveAllBank(Model model){
        List<Bank> bankList = bankService.findAllBank();
        model.addAttribute("bankList", bankList);
        List<FixedDeposits> fixedList = fixedDepostisService.findAllFixeds();
        model.addAttribute("fixedList", fixedList);
        return "admin/general";
    }



    // @GetMapping("/managefixeddeposits/addfixed")
    // public String addfixed(Model model){
      
    //     return "admin/addfixed";
    // }

    @GetMapping("/managefixeddeposits/addbank")
    public String addbank(Model model){
      
        return "admin/addbank";
    }




    @RequestMapping(value = "/managefixeddeposits/addbank", method = RequestMethod.POST)
    public String savebank(String name, String link){
        Bank bank = new Bank(name, link);
        bankService.addBank(bank);
        return "redirect:/admin/managefixeddeposits/addbank?success";
    }
}
