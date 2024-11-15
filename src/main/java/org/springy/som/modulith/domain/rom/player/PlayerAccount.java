package org.springy.som.modulith.domain.rom.player;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Players")
public class PlayerAccount {
    private String firstName;
    private String lastName;
    private String accountName;
    private String emailAddress;
    private String password;
    private List<String> playerCharacterList;

    @Id
    private String id;
}
