package com.example.cleonoraadmin.service;

import jakarta.servlet.http.HttpServletRequest;

public interface MailService {
    void sendToken(String token, String to, HttpServletRequest request);
}
