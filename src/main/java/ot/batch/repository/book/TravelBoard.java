package ot.batch.repository.book;

import ot.batch.repository.BaseTimeEntity;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

public class TravelBoard extends BaseTimeEntity {

    private String title;
    private String content;
    private Integer regionCode;
    private Integer numberOfTravelers;
    private LocalDate recruitmentPeriodStart;
    private LocalDate recruitmentPeriodEnd;
    private LocalDate journeyPeriodStart;
    private LocalDate journeyPeriodEnd;

    @Enumerated(EnumType.STRING)
    private RecruitmentStatus recruitmentStatus;

    public void updateRecruitmentStatus(RecruitmentStatus recruitmentStatus){
        this.recruitmentStatus = recruitmentStatus;
    }
}
