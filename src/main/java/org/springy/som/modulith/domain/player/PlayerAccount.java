package org.springy.som.modulith.domain.player;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Players")
public class PlayerAccount {
    @NotBlank(message = "first name must not be blank")
    private String firstName;
    @NotBlank(message = "last name must not be blank")
    private String lastName;
    @NotBlank(message = "account name must not be blank")
    private String accountName;
    @NotBlank(message = "email address must not be blank")
    private String emailAddress;
    @NotBlank(message = "password must not be blank")
    private String password;
    private List<String> playerCharacterList;

    @Id
    private String id;
}
