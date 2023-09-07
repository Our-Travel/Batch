package ot.batch.api.open.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenApiIntroDto {

    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private String playTime;
    private String restDate;
    private String useTimeFestival;
}
