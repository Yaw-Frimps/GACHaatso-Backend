package com.example.gacapp.service.impl;




import com.example.gacapp.dto.response.BirthdayResponse;
import com.example.gacapp.model.Members;
import com.example.gacapp.repository.MemberRepository;
import com.example.gacapp.service.BirthdayService;
import com.example.gacapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Clock;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BirthdayServiceImpl
        implements BirthdayService {


    private final MemberRepository membersRepository;

    private final NotificationService notificationService;
    private final Clock clock;



    @Override
    public List<BirthdayResponse> getMonthlyBirthdays(){


        int month =
                LocalDate.now(clock)
                        .getMonthValue();



        return membersRepository
                .findBirthdaysByMonth(month)
                .stream()
                .map(this::map)
                .toList();

    }





    @Override
    public List<BirthdayResponse> getTodayBirthdays(){


        return membersRepository
                .findTodaysBirthdays()
                .stream()
                .map(this::map)
                .toList();

    }





    @Scheduled(
            cron = "0 0 7 * * *"
    )
    @Override
    public void checkBirthdayNotifications(){


        List<Members> birthdays =
                membersRepository
                        .findTodaysBirthdays();



        if(!birthdays.isEmpty()){


            log.info(
                    "{} birthdays found",
                    birthdays.size()
            );


            notificationService
                    .createBirthdayNotifications(
                            birthdays
                    );

        }

    }





    private BirthdayResponse map(
            Members member
    ){

        return BirthdayResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .dateOfBirth(member.getDateOfBirth())
                .age(member.getAge())
                .phoneNumber(member.getPhoneNumber())
                .imageUrl(member.getProfileImageUrl())
                .build();

    }

}