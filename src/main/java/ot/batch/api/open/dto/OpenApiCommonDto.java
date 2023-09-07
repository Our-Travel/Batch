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
public class OpenApiCommonDto {

    private String telName;
    private String homePage;
    private String overView;

    public static OpenApiCommonDto of(JSONObject item){
        return OpenApiCommonDto.builder().
                telName((String) item.get("telname")).
                homePage((String) item.get("homepage")).
                overView((String) item.get("overview")).
                build();
    }
}
