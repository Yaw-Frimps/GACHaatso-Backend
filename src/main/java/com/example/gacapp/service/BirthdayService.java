package com.example.gacapp.service;

import com.example.gacapp.dto.response.BirthdayResponse;

import java.util.List;


public interface BirthdayService {


    List<BirthdayResponse> getMonthlyBirthdays();


    List<BirthdayResponse> getTodayBirthdays();


    void checkBirthdayNotifications();

}