package ot.batch.api.open.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ot.batch.api.open.dto.OpenApiInfoDto;
import ot.batch.api.open.dto.OpenApiCommonDto;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
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
        JSONArray jsonArray = new JSONArray();
        try {
            String responseString = restTemplate.getForObject(uri, String.class);
            if (responseString != null) {
                if (responseString.trim().startsWith("<")) {
                    // XML 응답 처리
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new InputSource(new StringReader(responseString)));
                    NodeList itemNodes = doc.getElementsByTagName("item");
                    for (int i = 0; i < itemNodes.getLength(); i++) {
                        Node itemNode = itemNodes.item(i);
                        if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                            JSONObject jsonObject = new JSONObject();
                            NodeList childNodes = itemNode.getChildNodes();
                            for (int j = 0; j < childNodes.getLength(); j++) {
                                Node childNode = childNodes.item(j);
                                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element childElement = (Element) childNode;
                                    jsonObject.put(childElement.getTagName(), childElement.getTextContent());
                                }
                            }
                            jsonArray.add(jsonObject);
                        }
                    }
                } else {
                    // JSON 응답 처리
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(responseString);
                    JSONObject jsonResponse = (JSONObject) jsonObject.get("response");
                    JSONObject jsonBody = (JSONObject) jsonResponse.get("body");
                    JSONObject jsonItems = (JSONObject) jsonBody.get("items");
                    jsonArray = (JSONArray) jsonItems.get("item");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private int getMaxPage(JSONArray jsonItem) {
        if (jsonItem == null || jsonItem.isEmpty()) {
            return 0;
        }
        JSONObject itemObject = (JSONObject) jsonItem.get(0);
        Long totalCnt = (Long) itemObject.get("totalCnt");
        return (int) (totalCnt / 50 + 1);
    }

    private List<OpenApiInfoDto> infoData(JSONArray jsonItemList) {
        List<OpenApiInfoDto> result = new ArrayList<>();
        for(Object o : jsonItemList){
            JSONObject item = (JSONObject) o;
            System.out.println(item);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            result.add(OpenApiInfoDto.of(item));
        }
        return result;
    }
}
