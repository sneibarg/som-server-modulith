package org.springy.som.modulith.domain.rom.shop;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Shops")
public class Shop {
    private String areaId;
    private String vnum;
    private String profit_buy;
    private String profit_sell;
    private String open_hour;
    private String close_hour;
    private String owner_name;
    private List<String> trade_items;


    @Id
    private String id;
}
