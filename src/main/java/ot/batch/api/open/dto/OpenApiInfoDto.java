package ot.batch.api.open.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenApiInfoDto {

    private int contentId;
    private int contentTypeId;
    private String title;
    private String address;
    private String tel;
    private double longitude;
    private double latitude;
    private String image;

    public static OpenApiInfoDto of(JSONObject item){
        return OpenApiInfoDto.builder().
                contentId((int) item.get("contentid")).
                title((String) item.get("title")).
                address((String) item.get("addr1")).
                contentTypeId((int) item.get("contenttypeid")).
                tel((String) item.get("tel")).
                longitude((double) item.get("mapx")).
                latitude((double) item.get("mapy")).
                image((String) item.get("firstimage")).
                build();
    }
}
