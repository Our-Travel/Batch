package ot.batch.api.open.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Service
public class UriBuilderService {

    private static final String OPEN_API_URL = "https://apis.data.go.kr/B551011/KorService1/";
    private static final String AREA = "areaBasedList1?";
    private static final String FESTIVAL = "searchFestival1?";
    private static final String STAY = "searchStay1?";
    private static final String COMMON = "detailCommon1?";
    private static final String INTRO = "detailIntro1?";


    @Value("${open.api.key}")
    private String serviceKey;

    public URI builderUriArea(int contentType, double longitude, double latitude, int page) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL+AREA)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 50)
                .queryParam("pageNo", page)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("listYN", "N")
                .queryParam("arrange", "A")
                .queryParam("mapX", longitude)
                .queryParam("mapY", latitude)
                .queryParam("radius", 20000)
                .queryParam("contentTypeId", contentType)
                .build()
                .toUriString();

        return URI.create(url);
    }

    public URI builderUriAreaPage(int contentType, double longitude, double latitude) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL+AREA)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 50)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("listYN", "N")
                .queryParam("arrange", "A")
                .queryParam("mapX", longitude)
                .queryParam("mapY", latitude)
                .queryParam("radius", 20000)
                .queryParam("contentTypeId", contentType)
                .build()
                .toUriString();

        return URI.create(url);
    }
}
