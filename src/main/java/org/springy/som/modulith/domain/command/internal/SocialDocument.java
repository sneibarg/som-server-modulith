package org.springy.som.modulith.domain.command.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Socials")
public class SocialDocument {
    @NotBlank(message = "name must not be blank")
    private String name;
    private String charNoArg;
    private String othersNoArg;
    private String charFound;
    private String othersFound;
    private String victFound;
    private String charNotFound;
    private String charAuto;
    private String othersAuto;

    @Id
    private String id;
}
