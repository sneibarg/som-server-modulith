package org.springy.som.modulith.domain.area.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springy.som.modulith.domain.item.internal.ItemAreaCleanupListener;
import org.springy.som.modulith.domain.item.internal.ItemDocument;
import org.springy.som.modulith.domain.item.internal.ItemRepository;
import org.springy.som.modulith.domain.mobile.internal.MobileAreaCleanupListener;
import org.springy.som.modulith.domain.mobile.internal.MobileDocument;
import org.springy.som.modulith.domain.mobile.internal.MobileRepository;
import org.springy.som.modulith.domain.reset.internal.ResetAreaCleanupListener;
import org.springy.som.modulith.domain.reset.internal.ResetDocument;
import org.springy.som.modulith.domain.reset.internal.ResetRepository;
import org.springy.som.modulith.domain.room.internal.RoomAreaCleanupListener;
import org.springy.som.modulith.domain.room.internal.RoomDocument;
import org.springy.som.modulith.domain.room.internal.RoomRepository;
import org.springy.som.modulith.domain.shop.internal.ShopAreaCleanupListener;
import org.springy.som.modulith.domain.shop.internal.ShopDocument;
import org.springy.som.modulith.domain.shop.internal.ShopRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({
        AreaService.class,
        RoomAreaCleanupListener.class,
        MobileAreaCleanupListener.class,
        ItemAreaCleanupListener.class,
        ShopAreaCleanupListener.class,
        ResetAreaCleanupListener.class
})
class AreaDeletionIntegrationTest {

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.embedded.version", () -> "4.0.2");
        registry.add("spring.mongodb.embedded.port", () -> 0);
        registry.add("spring.data.mongodb.database", () -> "som-test");
    }

    @Autowired
    private AreaService areaService;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MobileRepository mobileRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ResetRepository resetRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        resetRepository.deleteAll();
        shopRepository.deleteAll();
        itemRepository.deleteAll();
        mobileRepository.deleteAll();
        roomRepository.deleteAll();
        areaRepository.deleteAll();
    }

    @Test
    void deleteAreaById_removesAllAreaScopedDocuments() {
        AreaDocument areaA = area("A1", "Midgaard");
        AreaDocument areaB = area("A2", "Ofcol");
        areaRepository.save(areaA);
        areaRepository.save(areaB);

        roomRepository.save(room("R1", "A1", "Square"));
        roomRepository.save(room("R2", "A2", "Market"));

        mobileRepository.save(mobile("M1", "A1", "Guard"));
        mobileRepository.save(mobile("M2", "A2", "Merchant"));

        itemRepository.save(item("I1", "A1", "Sword"));
        itemRepository.save(item("I2", "A2", "Potion"));

        shopRepository.save(shop("S1", "A1"));
        shopRepository.save(shop("S2", "A2"));

        resetRepository.save(reset("RS1", "A1"));
        resetRepository.save(reset("RS2", "A2"));

        areaService.deleteAreaById("A1");

        assertThat(areaRepository.existsById("A1")).isFalse();
        assertThat(areaRepository.existsById("A2")).isTrue();

        assertThat(countByArea(RoomDocument.class, "A1")).isZero();
        assertThat(countByArea(MobileDocument.class, "A1")).isZero();
        assertThat(countByArea(ItemDocument.class, "A1")).isZero();
        assertThat(countByArea(ShopDocument.class, "A1")).isZero();
        assertThat(countByArea(ResetDocument.class, "A1")).isZero();

        assertThat(countByArea(RoomDocument.class, "A2")).isEqualTo(1);
        assertThat(countByArea(MobileDocument.class, "A2")).isEqualTo(1);
        assertThat(countByArea(ItemDocument.class, "A2")).isEqualTo(1);
        assertThat(countByArea(ShopDocument.class, "A2")).isEqualTo(1);
        assertThat(countByArea(ResetDocument.class, "A2")).isEqualTo(1);
    }

    private long countByArea(Class<?> entityClass, String areaId) {
        return mongoTemplate.count(Query.query(Criteria.where("areaId").is(areaId)), entityClass);
    }

    private static AreaDocument area(String id, String name) {
        AreaDocument doc = new AreaDocument();
        doc.setId(id);
        doc.setName(name);
        return doc;
    }

    private static RoomDocument room(String id, String areaId, String name) {
        RoomDocument doc = new RoomDocument();
        doc.setId(id);
        doc.setAreaId(areaId);
        doc.setName(name);
        return doc;
    }

    private static MobileDocument mobile(String id, String areaId, String name) {
        MobileDocument doc = new MobileDocument();
        doc.setId(id);
        doc.setAreaId(areaId);
        doc.setName(name);
        return doc;
    }

    private static ItemDocument item(String id, String areaId, String name) {
        ItemDocument doc = new ItemDocument();
        doc.setId(id);
        doc.setAreaId(areaId);
        doc.setName(name);
        return doc;
    }

    private static ShopDocument shop(String id, String areaId) {
        ShopDocument doc = new ShopDocument();
        doc.setId(id);
        doc.setAreaId(areaId);
        return doc;
    }

    private static ResetDocument reset(String id, String areaId) {
        ResetDocument doc = new ResetDocument();
        doc.setId(id);
        doc.setAreaId(areaId);
        return doc;
    }

}
