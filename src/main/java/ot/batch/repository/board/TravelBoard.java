package ot.batch.repository.board;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ot.batch.repository.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
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
