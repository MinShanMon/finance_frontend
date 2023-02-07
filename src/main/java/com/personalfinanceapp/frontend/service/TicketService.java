package com.personalfinanceapp.frontend.service;

import com.personalfinanceapp.frontend.model.Ticket;

public interface TicketService {
    boolean sendEmail(Integer id);
    Ticket create(Ticket tik);
    Ticket update(Ticket tik);
}
