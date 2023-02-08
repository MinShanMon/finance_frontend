package com.personalfinanceapp.frontend.service;

import java.util.List;

import com.personalfinanceapp.frontend.model.Enquiry;

public interface EnquiryService {
    List<Enquiry> viewDashboard();
    List<Enquiry> getAllEnquiry(); 
    List<Enquiry> getOpenEnquiry();
    List<Enquiry> getClosedEnquiry();
    Enquiry getOneEnquiry(Integer id);
}
