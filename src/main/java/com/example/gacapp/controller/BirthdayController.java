package com.example.gacapp.controller;

import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.BirthdayResponse;
import com.example.gacapp.service.BirthdayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/birthdays")
@RequiredArgsConstructor
public class BirthdayController {


    private final BirthdayService birthdayService;



    @GetMapping("/this-month")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BirthdayResponse>>>
    getMonthlyBirthdays(){


        return ResponseEntity.ok(
                ApiResponse.success(
                        birthdayService.getMonthlyBirthdays(),
                        "Monthly birthdays retrieved successfully"
                ));

    }




    @GetMapping("/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BirthdayResponse>>>
    getTodayBirthdays(){


        return ResponseEntity.ok(
                ApiResponse.success(
                        birthdayService.getTodayBirthdays(),
                        "Today's birthdays retrieved successfully"
                ));

    }

}
