package com.personalfinanceapp.frontend.service;

import com.personalfinanceapp.frontend.model.Enquiry;

public interface ReviewService {
    Enquiry getOneReview(Integer id);
    Enquiry updateReview(Enquiry Enq);
}
