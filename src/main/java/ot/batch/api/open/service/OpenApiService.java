package ot.batch.api.open.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ot.batch.api.open.dto.OpenApiByAreaDto;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final RestTemplate restTemplate;
    private final UriBuilderService uriBuilderService;

    public int maxPage(int contentType, double longitude, double latitude) {
        URI uri = uriBuilderService.builderUriAreaPage(contentType, longitude, latitude);
        String jsonString = restTemplate.getForObject(uri, String.class);
        System.out.println(jsonString);
        JSONArray jsonItem = parseJsonToItemsArray(jsonString);
        JSONObject itemObject = (JSONObject) jsonItem.get(0);
        Long totalCnt = (Long) itemObject.get("totalCnt");
        return (int) (totalCnt / 50 + 1);
    }

    public List<OpenApiByAreaDto> requestAreaBased(int contentType, double longitude, double latitude, int page) {
        URI uri = uriBuilderService.builderUriArea(contentType, longitude, latitude, page);
        String jsonString = restTemplate.getForObject(uri, String.class);
        JSONArray jsonItemList = parseJsonToItemsArray(jsonString);
        List<OpenApiByAreaDto> result = new ArrayList<>();
        for(Object o : jsonItemList){
            JSONObject item = (JSONObject) o;
            result.add(makeAreaDto(item));
        }
        return result;
    }

    private OpenApiByAreaDto makeAreaDto(JSONObject item) {
        OpenApiByAreaDto dto = OpenApiByAreaDto.builder().
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

    private JSONArray parseJsonToItemsArray(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = null;
        try {
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
}
