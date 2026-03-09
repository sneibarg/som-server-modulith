package org.springy.som.modulith.domain.shop.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Shops")
public class ShopDocument {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String keeper;
    private String buyType0;
    private String buyType1;
    private String buyType2;
    private String buyType3;
    private String buyType4;
    private String profitBuy;
    private String profitSell;
    private String openHour;
    private String closeHour;
    private String comment;

    @Id
    private String id;
}
