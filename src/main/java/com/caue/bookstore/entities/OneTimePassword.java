package com.caue.bookstore.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "bs_one_time_password")
public class OneTimePassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long otp;

    private Instant expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public OneTimePassword() {
    }

    public OneTimePassword(Long id, Long otp, Instant expirationTime) {
        this.id = id;
        this.otp = otp;
        this.expirationTime = expirationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOtp() {
        return otp;
    }

    public void setOtp(Long otp) {
        this.otp = otp;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
