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

import java.net.URI;
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

    public OpenApiCommonDto requestCommon(int contentId, int contentType){
        URI uri = uriBuilderService.builerUriCommon(contentId, contentType);
        JSONArray jsonItemList = parseJsonToItemsArray(uri);
        OpenApiCommonDto commonDto = null;
        for(Object o : jsonItemList){
            JSONObject item = (JSONObject) o;
            commonDto = OpenApiCommonDto.of(item);
        }
        return commonDto;
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
            result.add(OpenApiInfoDto.of(item));
        }
        return result;
    }
}
