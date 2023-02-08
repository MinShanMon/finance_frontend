package com.personalfinanceapp.frontend.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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


    @RequestMapping(value = "/managefixeddeposits/addfixed", method = RequestMethod.POST)
    public String savebank(String tenure, String minAmount, String maxAmount, String interestRate, String b_id){
        LocalDateTime now = LocalDateTime.now();

        FixedDeposits fDeposits = new FixedDeposits(Integer.parseInt(tenure), Integer.parseInt(minAmount), Integer.parseInt(maxAmount),
                                        Double.parseDouble(interestRate),now,bankService.findBankById(Long.parseUnsignedLong(b_id)));
        fixedDepostisService.addFixedDeposits(fDeposits);
        return "redirect:/admin/managefixeddeposits";
    }


    @GetMapping("/managefixeddeposits/deletefixed/{id}")
    public String deleteThroughId(@PathVariable(value = "id") long id) {
        fixedDepostisService.deletefixed(id);
        return "redirect:/admin/managefixeddeposits";

    }

    @GetMapping("/managefixeddeposits/editfixed/{id}")
    public String editbank(@PathVariable(value = "id") long id,Model model) {

        FixedDeposits fixedDeposits = fixedDepostisService.findbyid(id);
        List<Bank> bankList = bankService.findAllBank();
        model.addAttribute("bankList", bankList);
        model.addAttribute("fixed", fixedDeposits);
        return "/admin/fixededit";

    }

    @PostMapping("/managefixeddeposits/editfixed/")
    public String editbank(String id,String tenure, String minAmount, String maxAmount, String interestRate, String b_id) {

        FixedDeposits fixedDeposits = fixedDepostisService.findbyid(Long.parseUnsignedLong(id));

        fixedDeposits.setTenure(Integer.parseInt(tenure));
        fixedDeposits.setMaxAmount(Integer.parseInt(maxAmount));
        fixedDeposits.setMinAmount(Integer.parseInt(minAmount));
        fixedDeposits.setInterestRate(Double.parseDouble(interestRate));
        fixedDeposits.setFd_bank(bankService.findBankById(Long.parseUnsignedLong(b_id)));

    
        fixedDepostisService.editfixed(fixedDeposits);
        return "redirect:/admin/managefixeddeposits";

    }


    @GetMapping("/fixedanalysis")
    public String fixedAnalysis(){

        return "admin/bankdepositsanalysis";
    }


}
