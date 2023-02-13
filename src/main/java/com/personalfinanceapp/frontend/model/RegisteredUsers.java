package com.personalfinanceapp.frontend.model;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import javax.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "Registered_Users")
public class RegisteredUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId", nullable = false)
    private Integer id;

    @Column(name = "UserName", columnDefinition = "nvarchar(150) not null")
    private String fullName;

    @Email(message = "Email should be valid")
    @Size(max = 200)
    @Pattern(regexp=".+@.+\\..+", message = "Wrong email!")
    @Column(name = "UserEmail", columnDefinition = "nvarchar(200) not null")
    private String email;

    @Column(name = "UserPassword", nullable = true)
    private String password;

    @Column(name = "FaceBookId", nullable = true)
    private String fbid;

    @Column(name = "confirm_account_otp")
	private String otp;

	@Column(name = "otp_requested_time")
	private Date otpReqTime;

    @Column(name = "JwtToken")
    private String jwtToken;
    
    
    @ManyToMany(targetEntity = Role.class)
    @JoinTable(name = "userrole", joinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "UserId") }, inverseJoinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "RoleId") }
    )
    private List<Role> roleSet;




}
