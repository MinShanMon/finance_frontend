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
import com.personalfinanceapp.frontend.model.UserSession;
import com.personalfinanceapp.frontend.service.BankService;
import com.personalfinanceapp.frontend.service.FixedDepositsRecordsService;
import com.personalfinanceapp.frontend.service.FixedDepostisService;
import javax.servlet.http.HttpSession;

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
    public String retrieveAllBank(Model model, HttpSession session){


        LocalDate date = LocalDate.now();
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();

        if(date.getDayOfMonth() == 1){
            List<FixedDeposits> listfor_r= fixedDepostisService.findAllFixeds(token);

            if(date.getMonthValue() == 1){

                List<FixedDeposits> filterByMonth_1 = listfor_r.stream().filter(r -> r.getUpdateDate().getMonthValue() == 12).collect(Collectors.toList()); 
                List<FixedDeposits> finalFilter = filterByMonth_1.stream().filter(r -> r.getUpdateDate().getYear() == (r.getUpdateDate().getYear() - 1)).collect(Collectors.toList());
                
                for(int i =0 ; i< finalFilter.size(); i++){
                    FixedDeposits f = finalFilter.get(i);
                    fixedDepositsRecordsService.recordFixed(new FixedDepositsRecords(
                        f.getTenure(),f.getMinAmount(),f.getMaxAmount(),f.getInterestRate(),f.getUpdateDate().getMonthValue(),f.getUpdateDate().getYear(),f.getFd_bank().getBankName()), token);
                }
            }else{

                List<FixedDeposits> finalFilter_else = listfor_r.stream().filter(r -> r.getUpdateDate().getMonthValue() == (date.getMonthValue() -1) ).collect(Collectors.toList()); 
                
                for(int i =0 ; i< finalFilter_else.size(); i++){
                    FixedDeposits f = finalFilter_else.get(i);
                    fixedDepositsRecordsService.recordFixed(new FixedDepositsRecords(
                        f.getTenure(),f.getMinAmount(),f.getMaxAmount(),f.getInterestRate(),f.getUpdateDate().getMonthValue(),f.getUpdateDate().getYear(),f.getFd_bank().getBankName()), token);

            }
        }
    }

        List<Bank> bankList = bankService.findAllBank(token);
        model.addAttribute("bankList", bankList);
        List<FixedDeposits> fixedList = fixedDepostisService.findAllFixeds(token);
        model.addAttribute("fixedList", fixedList);
        return "admin/managebankfixeddeposits";
    }

    



    @GetMapping("/managefixeddeposits/addbank")
    public String addbank(Model model) {


        return "/admin/addbank";

    }





   

    @RequestMapping(value = "/managefixeddeposits/addbank", method = RequestMethod.POST)
    public String savebank(String name, String link, Model model, HttpSession session){

        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        List<Bank> banklist = bankService.findAllBank(token);
       
        for(Bank bank: banklist){
            if(bank.getBankName().compareTo(name) == 0){
                return "redirect:/admin/managefixeddeposits/addbank?errorn";
            }else if(bank.getBankLink().compareTo(link) == 0){
                return "redirect:/admin/managefixeddeposits/addbank?errorl";
            }
        }
        Bank bank = new Bank(name.toUpperCase(), link);
            bankService.addBank(bank, token);
            return "redirect:/admin/managefixeddeposits/addbank?success";
       
    }


    @GetMapping("/managefixeddeposits/{id}")
    public String retrievefixedbybankid(Model model,@PathVariable(value = "id") long id, HttpSession session){

        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        List<FixedDeposits> fixedList = fixedDepostisService.findfixedbybankid(id, token);
        model.addAttribute("fixedList", fixedList);
        List<Bank> bankList = bankService.findAllBank(token);
        model.addAttribute("bankList", bankList);
        return "admin/managebankfixeddeposits";
    }

    @GetMapping("/managefixeddeposits/deletebank/{id}")
    public String deletebank(@PathVariable(value = "id") long id, HttpSession session) {
        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        bankService.deletebank(id, token);
        return "redirect:/admin/managefixeddeposits";

    }

    @GetMapping("/managefixeddeposits/editbank/{id}")
    public String editbank(@PathVariable(value = "id") long id,Model model, HttpSession session) {

        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        Bank bank = bankService.findBankById(id, token);

        model.addAttribute("bank", bank);
        return "/admin/bankedit";

    }

    @PostMapping("/managefixeddeposits/editbank/")
    public String editbank(Long id,String name, String link, HttpSession session) {

        UserSession user =(UserSession) session.getAttribute("usersession");
        String token = user.getToken().getAccess_token();
        Bank bank = bankService.findBankById(id, token);

        bank.setBankName(name);
        bank.setBankLink(link);

        bankService.editbank(bank, token);
        return "redirect:/admin/managefixeddeposits?successfb";

    }

}
