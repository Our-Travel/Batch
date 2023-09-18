package ot.batch.repository.travelInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import ot.batch.api.open.dto.OpenApiInfoDto;
import ot.batch.repository.BaseTimeEntity;


import javax.persistence.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLDelete(sql = "UPDATE travel_info SET deleted_date = NOW() where id = ?")
@Where(clause = "deleted_date is NULL")
public class TravelInfo extends BaseTimeEntity {

    private int contentId;
    private int contentTypeId;
    private String title;
    private String address;
    private String tel;
    private String telName;
    private double longitude;
    private double latitude;
    private String image;

    @Column(columnDefinition = "TEXT")
    private String homePage;

    @Column(columnDefinition = "TEXT")
    private String overView;

    private TravelInfo(OpenApiInfoDto openApiInfoDto){
        this.contentId = openApiInfoDto.getContentId();
        this.contentTypeId = openApiInfoDto.getContentTypeId();
        this.title = openApiInfoDto.getTitle();
        this.address = openApiInfoDto.getAddress();
        this.tel = openApiInfoDto.getTel();
        this.longitude = openApiInfoDto.getLongitude();
        this.latitude = openApiInfoDto.getLatitude();
        this.image = openApiInfoDto.getImage();
    }

    public static TravelInfo of(OpenApiInfoDto openApiInfoDto){
        TravelInfo travelInfo = new TravelInfo(openApiInfoDto);
        return travelInfo;
    }

    public void updateTelName(String telName){
        this.telName = telName;
    }

    public void updateHomePage(String homePage){
        this.homePage = homePage;
    }

    public void updateOverView(String overView){
        this.overView = overView;
    }
}
