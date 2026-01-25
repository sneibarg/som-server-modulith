package org.springy.som.modulith.domain.shop;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Shops")
public class Shop {
    private String areaId;
    private String vnum;
    private String profitBuy;
    private String profitSell;
    private String openHour;
    private String closeHour;
    private String ownerName;
    private List<String> tradeItems;


    @Id
    private String id;
}
