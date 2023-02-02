package com.personalfinanceapp.frontend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enquiries")
public class Enquiry {
    @Id
    @Column(name="enquiry_id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="enquiryType",nullable = false, columnDefinition = "ENUM('ACCOUNT','PRODUCT','FEEDBACK','OTHER')")
    @Enumerated(EnumType.STRING)
    private EnquiryTypeEnum enquiryType;

    @Column(name="salutation",nullable = false, columnDefinition = "ENUM('MS','MRS','MDM','MR')")
    @Enumerated(EnumType.STRING)
    private SalutationEnum title;

    private String name;

    private String email;

    @Column(name = "contact_number")
    private String phoneNum;

    private String question;

    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime enquiry_dateTime;
    
    private int rating;

    @JsonIgnoreProperties({"ticket"})
    @OneToOne
    @JoinColumn(name="ticket_id")
    private Ticket ticket;

    public Enquiry(EnquiryTypeEnum enquiryType,SalutationEnum title, String name,String email,
    String phoneNum,String question, LocalDateTime enquiry_dateTime,int rating,Ticket ticket){ 

        this.enquiryType= enquiryType;
        this.title = title;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.question = question;
        this.enquiry_dateTime = enquiry_dateTime;
        this.rating = rating;
        this.ticket = ticket;
    }
}


