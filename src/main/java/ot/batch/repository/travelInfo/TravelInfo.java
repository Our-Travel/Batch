package ot.batch.repository.travelInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ot.batch.api.kakao.dto.DocumentDTO;
import ot.batch.repository.BaseTimeEntity;


import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class TravelInfo extends BaseTimeEntity {

    private Long placeId;
    private String placeName;
    private String phone;
    private String placeUrl;
    private String address;
    private String roadAddress;
    private double longitude;
    private double latitude;

    @Enumerated(EnumType.STRING)
    private PlaceResource placeResource;

    private TravelInfo(DocumentDTO documentDTO){
        this.placeId = documentDTO.getId();
        this.placeName = documentDTO.getPlaceName();
        this.phone = documentDTO.getPhone();
        this.placeUrl = documentDTO.getPlaceUrl();
        this.address = documentDTO.getAddressName();
        this.roadAddress = documentDTO.getRoadAddressName();
        this.longitude = documentDTO.getLongitude();
        this.latitude = documentDTO.getLatitude();
    }

    public static TravelInfo kakao(DocumentDTO documentDTO){
        TravelInfo travelInfoKakao = new TravelInfo(documentDTO);
        travelInfoKakao.placeResource = PlaceResource.KAKAO;
        return travelInfoKakao;
    }
}
