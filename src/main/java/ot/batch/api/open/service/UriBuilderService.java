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
    private static final int NUM_OF_ROWS = 50;


    @Value("${open.api.key}")
    private String serviceKey;

    public URI builderUriArea(int contentType, int areaCode, int page) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL + AREA)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", page)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("listYN", "Y")
                .queryParam("arrange", "A")
                .queryParam("areaCode", areaCode)
                .queryParam("contentTypeId", contentType)
                .build()
                .toUriString();

        return URI.create(url);
    }

    public URI builderUriFestival(int page, String eventStartDate) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL + FESTIVAL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", page)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("listYN", "Y")
                .queryParam("arrange", "A")
                .queryParam("eventStartDate", eventStartDate)
                .build()
                .toUriString();

        return URI.create(url);
    }

    public URI builderUriStay(int page) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL + STAY)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", page)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("listYN", "Y")
                .queryParam("arrange", "A")
                .build()
                .toUriString();

        return URI.create(url);
    }

    public URI builderUriAreaPage(int contentType, int areaCode) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL + AREA)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("listYN", "N")
                .queryParam("arrange", "A")
                .queryParam("contentTypeId", contentType)
                .queryParam("areaCode", areaCode)
                .build()
                .toUriString();

        return URI.create(url);
    }

    public URI builderUriFestivalPage(String eventStartDate) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL + FESTIVAL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("listYN", "N")
                .queryParam("arrange", "A")
                .queryParam("eventStartDate", eventStartDate)
                .build()
                .toUriString();

        return URI.create(url);
    }

    public URI builderUriStayPage() {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL + STAY)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("listYN", "N")
                .queryParam("arrange", "A")
                .build()
                .toUriString();

        return URI.create(url);
    }

    public URI builerUriCommon(int contentId, int contentType) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL + COMMON)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 1)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("arrange", "A")
                .queryParam("contentTypeId", contentType)
                .queryParam("contentId", contentId)
                .queryParam("defaultYN", "Y")
                .queryParam("firstImageYN", "N")
                .queryParam("areacodeYN", "N")
                .queryParam("catcodeYN", "N")
                .queryParam("addrinfoYN", "N")
                .queryParam("mapinfoYN", "N")
                .queryParam("overviewYN", "Y")
                .build()
                .toUriString();

        return URI.create(url);
    }

    public URI builderUriIntro(int contentId, int contentType) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_API_URL + INTRO)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 1)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "OT")
                .queryParam("_type", "json")
                .queryParam("contentTypeId", contentType)
                .queryParam("contentId", contentId)
                .build()
                .toUriString();

        return URI.create(url);
    }
}
