package com.personalfinanceapp.frontend.service;

import com.personalfinanceapp.frontend.model.Ticket;

public interface TicketService {

    //Ticket create(Ticket tik);
    Ticket update(Ticket tik, String token);
    boolean sendEmail(Integer id, String token);
    boolean sendReview(Integer id, String token);
}
