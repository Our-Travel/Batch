package ot.batch.api.open.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ot.batch.api.open.dto.OpenApiInfoDto;
import ot.batch.api.open.dto.OpenApiCommonDto;
import ot.batch.api.open.dto.OpenApiIntroDto;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final RestTemplate restTemplate;
    private final UriBuilderService uriBuilderService;

    public int areaBasedMaxPage(int contentType, int areaCode) {
        URI uri = uriBuilderService.builderUriAreaPage(contentType, areaCode);
        JSONArray jsonItem = parseJsonToItemsArray(uri);
        return getMaxPage(jsonItem);
    }

    public int festivalMaxPage(String eventStartDate){
        URI uri = uriBuilderService.builderUriFestivalPage(eventStartDate);
        JSONArray jsonItem = parseJsonToItemsArray(uri);
        return getMaxPage(jsonItem);
    }

    public int stayMaxPage(){
        URI uri = uriBuilderService.builderUriStayPage();
        JSONArray jsonItem = parseJsonToItemsArray(uri);
        return getMaxPage(jsonItem);
    }

    public List<OpenApiInfoDto> requestAreaBased(int contentType, int areaCode, int page) {
        URI uri = uriBuilderService.builderUriArea(contentType, areaCode, page);
        JSONArray jsonItemList = parseJsonToItemsArray(uri);
        return infoData(jsonItemList);
    }

    public List<OpenApiInfoDto> requestFestival(int page, String eventStartDate) {
        URI uri = uriBuilderService.builderUriFestival(page, eventStartDate);
        JSONArray jsonItemList = parseJsonToItemsArray(uri);
        return infoData(jsonItemList);
    }

    public List<OpenApiInfoDto> requestStay(int page) {
        URI uri = uriBuilderService.builderUriStay(page);
        JSONArray jsonItemList = parseJsonToItemsArray(uri);
        return infoData(jsonItemList);
    }

    public List<OpenApiCommonDto> requestCommon(int contentId, int contentType){
        URI uri = uriBuilderService.builerUriCommon(contentId, contentType);
        JSONArray jsonItemList = parseJsonToItemsArray(uri);
        List<OpenApiCommonDto> result = new ArrayList<>();
        for(Object o : jsonItemList){
            JSONObject item = (JSONObject) o;
            result.add(makeCommonDto(item));
        }
        return result;
    }

//    public List<OpenApiIntroDto> requestIntro(int contentId, int contentType){
//        URI uri = uriBuilderService.builderUriIntro(contentId, contentType);
//        String jsonString = restTemplate.getForObject(uri, String.class);
//        JSONArray jsonItemList = parseJsonToItemsArray(jsonString);
//        List<OpenApiIntroDto> result = new ArrayList<>();
//        for(Object o : jsonItemList){
//            JSONObject item = (JSONObject) o;
//            result.add(makeIntroDto(item));
//        }
//        return result;
//    }
//
//    private OpenApiIntroDto makeIntroDto(JSONObject item){
//        String eventStartString = (String) item.get("eventstartdate");
//        String eventEndString = (String) item.get("eventenddate");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        LocalDate startDate = LocalDate.parse(eventStartString, formatter);
//        LocalDate endDate = LocalDate.parse(eventEndString, formatter);
//        OpenApiIntroDto dto = OpenApiIntroDto.builder().
//                eventStartDate(startDate).
//                eventEndDate(endDate).
//                playTime((String) item.get("playtime")).
//
//                build()
//    }

    private OpenApiCommonDto makeCommonDto(JSONObject item) {
        OpenApiCommonDto dto = OpenApiCommonDto.builder().
                telName((String) item.get("telname")).
                homePage((String) item.get("homepage")).
                overView((String) item.get("overview")).
                build();
        return dto;
    }

    private OpenApiInfoDto makeAreaDto(JSONObject item) {
        OpenApiInfoDto dto = OpenApiInfoDto.builder().
                contentId((Long) item.get("contentid")).
                title((String) item.get("title")).
                address((String) item.get("addr1")).
                contentTypeId((Long) item.get("contenttypeid")).
                tel((String) item.get("tel")).
                longitude((double) item.get("mapx")).
                latitude((double) item.get("mapy")).
                image((String) item.get("firstimage")).
                build();
        return dto;
    }

    private JSONArray parseJsonToItemsArray(URI uri) {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            String jsonString = restTemplate.getForObject(uri, String.class);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
            JSONObject jsonResponse = (JSONObject) jsonObject.get("response");
            JSONObject jsonBody = (JSONObject) jsonResponse.get("body");
            JSONObject jsonItems = (JSONObject) jsonBody.get("items");
            jsonArray = (JSONArray) jsonItems.get("item");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    private int getMaxPage(JSONArray jsonItem) {
        JSONObject itemObject = (JSONObject) jsonItem.get(0);
        Long totalCnt = (Long) itemObject.get("totalCnt");
        return (int) (totalCnt / 50 + 1);
    }

    private List<OpenApiInfoDto> infoData(JSONArray jsonItemList) {
        List<OpenApiInfoDto> result = new ArrayList<>();
        for(Object o : jsonItemList){
            JSONObject item = (JSONObject) o;
            result.add(makeAreaDto(item));
        }
        return result;
    }
}
