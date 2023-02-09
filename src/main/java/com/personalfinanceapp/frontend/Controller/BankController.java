package com.personalfinanceapp.frontend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.personalfinanceapp.frontend.Models.Bank;
import com.personalfinanceapp.frontend.Models.FixedDeposits;
import com.personalfinanceapp.frontend.Models.FixedDepositsRecords;
import com.personalfinanceapp.frontend.service.BankService;
import com.personalfinanceapp.frontend.service.FixedDepositsRecordsService;
import com.personalfinanceapp.frontend.service.FixedDepostisService;


@Controller
@RequestMapping("/admin")
public class BankController {
    
    @Autowired
    BankService bankService;

    @Autowired
    FixedDepostisService fixedDepostisService;

    @Autowired
    FixedDepositsRecordsService fixedDepositsRecordsService;




    @GetMapping("/managefixeddeposits")
    public String retrieveAllBank(Model model){


        LocalDate date = LocalDate.now();


        if(date.getDayOfMonth() == 1){
            List<FixedDeposits> listfor_r= fixedDepostisService.findAllFixeds();

            if(date.getMonthValue() == 1){

                List<FixedDeposits> filterByMonth_1 = listfor_r.stream().filter(r -> r.getUpdateDate().getMonthValue() == 12).collect(Collectors.toList()); 
                List<FixedDeposits> finalFilter = filterByMonth_1.stream().filter(r -> r.getUpdateDate().getYear() == (r.getUpdateDate().getYear() - 1)).collect(Collectors.toList());
                
                for(int i =0 ; i< finalFilter.size(); i++){
                    FixedDeposits f = finalFilter.get(i);
                    fixedDepositsRecordsService.recordFixed(new FixedDepositsRecords(
                        f.getTenure(),f.getMinAmount(),f.getMaxAmount(),f.getInterestRate(),f.getUpdateDate().getMonthValue(),f.getUpdateDate().getYear(),f.getFd_bank().getBankName()));
                }
            }else{

                List<FixedDeposits> finalFilter_else = listfor_r.stream().filter(r -> r.getUpdateDate().getMonthValue() == (date.getMonthValue() -1) ).collect(Collectors.toList()); 
                
                for(int i =0 ; i< finalFilter_else.size(); i++){
                    FixedDeposits f = finalFilter_else.get(i);
                    fixedDepositsRecordsService.recordFixed(new FixedDepositsRecords(
                        f.getTenure(),f.getMinAmount(),f.getMaxAmount(),f.getInterestRate(),f.getUpdateDate().getMonthValue(),f.getUpdateDate().getYear(),f.getFd_bank().getBankName()));

            }
        }
    }

        List<Bank> bankList = bankService.findAllBank();
        model.addAttribute("bankList", bankList);
        List<FixedDeposits> fixedList = fixedDepostisService.findAllFixeds();
        model.addAttribute("fixedList", fixedList);
        return "admin/managebankfixeddeposits";
    }

    

    // @GetMapping("/managefixeddeposits/addfixed")
    // public String addfixed(Model model){
      
    //     return "admin/addfixed";
    // }

   

    @RequestMapping(value = "/managefixeddeposits/addbank", method = RequestMethod.POST)
    public String savebank(String name, String link, Model model){

        List<Bank> banklist = bankService.findAllBank();
       
        for(Bank bank: banklist){
            if(bank.getBankName().compareTo(name) == 0){
                return "redirect:/admin/managefixeddeposits?errorn";
            }else if(bank.getBankLink().compareTo(link) == 0){
                return "redirect:/admin/managefixeddeposits?errorl";
            }
        }
        Bank bank = new Bank(name, link);
            bankService.addBank(bank);
            return "redirect:/admin/managefixeddeposits";
       
    }


    @GetMapping("/managefixeddeposits/{id}")
    public String retrievefixedbybankid(Model model,@PathVariable(value = "id") long id){

       
        List<FixedDeposits> fixedList = fixedDepostisService.findfixedbybankid(id);
        model.addAttribute("fixedList", fixedList);
        List<Bank> bankList = bankService.findAllBank();
        model.addAttribute("bankList", bankList);
        return "admin/managebankfixeddeposits";
    }

    @GetMapping("/managefixeddeposits/deletebank/{id}")
    public String deletebank(@PathVariable(value = "id") long id) {
        bankService.deletebank(id);
        return "redirect:/admin/managefixeddeposits";

    }

    @GetMapping("/managefixeddeposits/editbank/{id}")
    public String editbank(@PathVariable(value = "id") long id,Model model) {

        Bank bank = bankService.findBankById(id);

        model.addAttribute("bank", bank);
        return "/admin/bankedit";

    }

    @PostMapping("/managefixeddeposits/editbank/")
    public String editbank(Long id,String name, String link) {

        Bank bank = bankService.findBankById(id);

        bank.setBankName(name);
        bank.setBankLink(link);

        bankService.editbank(bank);
        return "redirect:/admin/managefixeddeposits";

    }

}
