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
                contentId(Integer.parseInt((String) item.get("contentid"))).
                title((String) item.get("title")).
                address((String) item.get("addr1")).
                contentTypeId(Integer.parseInt((String) item.get("contenttypeid"))).
                tel((String) item.get("tel")).
                longitude(Double.parseDouble((String) item.get("mapx"))).
                latitude(Double.parseDouble((String) item.get("mapy"))).
                image((String) item.get("firstimage")).
                build();
    }

    private static Integer parseInteger(Object object) {
        try {
            return object != null ? Integer.parseInt((String) object) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double parseDouble(Object object) {
        try {
            return object != null ? Double.parseDouble((String) object) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
