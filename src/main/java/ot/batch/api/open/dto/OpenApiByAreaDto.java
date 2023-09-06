package ot.batch.api.open.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenApiByAreaDto {

    private Long contentId;
    private Long contentTypeId;
    private String title;
    private String address;
    private String tel;
    private double longitude;
    private double latitude;
    private String image;
}
