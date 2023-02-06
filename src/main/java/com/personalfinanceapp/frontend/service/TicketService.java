package com.personalfinanceapp.frontend.service;

import com.personalfinanceapp.frontend.model.Ticket;

public interface TicketService {
    //boolean sendEmail(Integer id) throws ResourceNotFoundException;
    Ticket create(Ticket tik);
    Ticket update(Ticket tik);
}
