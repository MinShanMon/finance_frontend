package com.personalfinanceapp.frontend.service;

import com.personalfinanceapp.frontend.model.Ticket;

public interface TicketService {

    //Ticket create(Ticket tik);
    Ticket update(Ticket tik);
    boolean sendEmail(Integer id);
    boolean sendReview(Integer id);
}
