package com.personalfinanceapp.frontend.service;

import java.util.List;

import com.personalfinanceapp.frontend.model.Enquiry;

public interface EnquiryService {
    List<Enquiry> viewDashboard(String token);
    List<Enquiry> getAllEnquiry(String token); 
    List<Enquiry> getOpenEnquiry(String token);
    List<Enquiry> getClosedEnquiry(String token);
    Enquiry getOneEnquiry(Integer id, String token);
}
