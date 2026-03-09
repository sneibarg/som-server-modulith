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
    private int keeper;
    private int buyType0;
    private int buyType1;
    private int buyType2;
    private int buyType3;
    private int buyType4;
    private int profitBuy;
    private int profitSell;
    private int openHour;
    private int closeHour;
    private String comment;

    @Id
    private String id;
}
