//package com.meetplus.batch.common;
//
//import java.time.LocalDateTime;
//import lombok.Getter;
//import org.springframework.beans.factory.annotation.Value;
//
//@Getter
//public class TotalDonationCustomJobParameter {
//    private LocalDateTime TotalDonationJobStartTime;
//    private LocalDateTime TotalDonationJobEndTime;
//
//    @Value("#{jobParameters['totalDonationJobStartTime']}")
//    public void settotalDonationJobStartTime(String totalDonationJobStartTime) {
//        if(totalDonationJobStartTime != null && !totalDonationJobStartTime.isEmpty()) {
//            this.TotalDonationJobStartTime = LocalDateTime.parse(totalDonationJobStartTime);
//        }
//    }
//
//    @Value("#{jobParameters['totalDonationJobEndTime']}")
//    public void settotalDonationJobEndTime(String totalDonationJobEndTime) {
//        if(totalDonationJobEndTime != null && !totalDonationJobEndTime.isEmpty()) {
//            this.TotalDonationJobEndTime = LocalDateTime.parse(totalDonationJobEndTime);
//        }
//    }
//}
//
//
