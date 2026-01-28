package org.springy.som.modulith.domain.area;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataMongoTest
class AreaTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6")
            .withExposedPorts(27017);

    @Autowired
    private MongoTemplate mongoTemplate;

    private Area area;

    @BeforeEach
    void setUp() {
        // Initialize a new Area object before each test
        area = new Area();
        area.setAuthor("Author1");
        area.setName("Test Area");
        area.setSuggestedLevelRange("1-10");
        area.setRooms(Arrays.asList("Room1", "Room2"));
        area.setMobiles(Arrays.asList("Mobile1", "Mobile2"));
        area.setObjects(Arrays.asList("Object1", "Object2"));
        area.setShops(Arrays.asList("Shop1"));
        area.setResets(Arrays.asList("Reset1"));
        area.setSpecials(Arrays.asList("Special1"));
        area.setId("area1");
    }

    @Test
    void testGettersAndSetters() {
        // Act & Assert: Verify getters return the values set in setUp
        assertEquals("Author1", area.getAuthor());
        assertEquals("Test Area", area.getName());
        assertEquals("1-10", area.getSuggestedLevelRange());
        assertEquals(Arrays.asList("Room1", "Room2"), area.getRooms());
        assertEquals(Arrays.asList("Mobile1", "Mobile2"), area.getMobiles());
        assertEquals(Arrays.asList("Object1", "Object2"), area.getObjects());
        assertEquals(Arrays.asList("Shop1"), area.getShops());
        assertEquals(Arrays.asList("Reset1"), area.getResets());
        assertEquals(Arrays.asList("Special1"), area.getSpecials());
        assertEquals("area1", area.getId());

        // Act: Update values using setters
        area.setAuthor("Author2");
        area.setName("Updated Area");
        area.setSuggestedLevelRange("11-20");
        area.setRooms(Arrays.asList("Room3"));
        area.setMobiles(Arrays.asList("Mobile3"));
        area.setObjects(Arrays.asList("Object3"));
        area.setShops(Arrays.asList("Shop2"));
        area.setResets(Arrays.asList("Reset2"));
        area.setSpecials(Arrays.asList("Special2"));
        area.setId("area2");

        // Assert: Verify updated values
        assertEquals("Author2", area.getAuthor());
        assertEquals("Updated Area", area.getName());
        assertEquals("11-20", area.getSuggestedLevelRange());
        assertEquals(Arrays.asList("Room3"), area.getRooms());
        assertEquals(Arrays.asList("Mobile3"), area.getMobiles());
        assertEquals(Arrays.asList("Object3"), area.getObjects());
        assertEquals(Arrays.asList("Shop2"), area.getShops());
        assertEquals(Arrays.asList("Reset2"), area.getResets());
        assertEquals(Arrays.asList("Special2"), area.getSpecials());
        assertEquals("area2", area.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange: Create another Area object with the same values
        Area area2 = new Area();
        area2.setAuthor("Author1");
        area2.setName("Test Area");
        area2.setSuggestedLevelRange("1-10");
        area2.setRooms(Arrays.asList("Room1", "Room2"));
        area2.setMobiles(Arrays.asList("Mobile1", "Mobile2"));
        area2.setObjects(Arrays.asList("Object1", "Object2"));
        area2.setShops(Arrays.asList("Shop1"));
        area2.setResets(Arrays.asList("Reset1"));
        area2.setSpecials(Arrays.asList("Special1"));
        area2.setId("area1");

        // Act & Assert: Verify equality
        assertEquals(area, area2, "Areas with the same values should be equal");
        assertEquals(area.hashCode(), area2.hashCode(), "Hash codes should be equal for equal objects");

        // Act: Change a field in area2
        area2.setName("Different Area");

        // Assert: Verify inequality
        assertNotEquals(area, area2, "Areas with different names should not be equal");
        assertNotEquals(area.hashCode(), area2.hashCode(), "Hash codes should differ for unequal objects");
    }

    @Test
    void testToString() {
        // Act
        String toString = area.toString();

        // Assert: Verify toString contains key fields
        assertTrue(toString.contains("author=Author1"), "toString should contain author");
        assertTrue(toString.contains("name=Test Area"), "toString should contain name");
        assertTrue(toString.contains("suggestedLevelRange=1-10"), "toString should contain suggestedLevelRange");
        assertTrue(toString.contains("id=area1"), "toString should contain id");
    }

    @Test
    void testMongoDBPersistence() {
        // Act: Save the Area object to MongoDB
        mongoTemplate.save(area, "Areas");

        // Act: Retrieve the Area object from MongoDB
        Area retrievedArea = mongoTemplate.findById("area1", Area.class, "Areas");

        // Assert: Verify the retrieved object matches the original
        assertNotNull(retrievedArea, "Retrieved Area should not be null");
        assertEquals(area, retrievedArea, "Retrieved Area should match the saved Area");
        assertEquals("Author1", retrievedArea.getAuthor());
        assertEquals("Test Area", retrievedArea.getName());
        assertEquals("1-10", retrievedArea.getSuggestedLevelRange());
        assertEquals(Arrays.asList("Room1", "Room2"), retrievedArea.getRooms());
        assertEquals(Arrays.asList("Mobile1", "Mobile2"), retrievedArea.getMobiles());
        assertEquals(Arrays.asList("Object1", "Object2"), retrievedArea.getObjects());
        assertEquals(Arrays.asList("Shop1"), retrievedArea.getShops());
        assertEquals(Arrays.asList("Reset1"), retrievedArea.getResets());
        assertEquals(Arrays.asList("Special1"), retrievedArea.getSpecials());
        assertEquals("area1", retrievedArea.getId());
    }
}