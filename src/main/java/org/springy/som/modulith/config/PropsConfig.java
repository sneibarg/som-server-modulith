import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springy.som.modulith.security.KeycloakRefreshProps;
import org.springy.som.modulith.security.SomProps;

@EnableConfigurationProperties({SomProps.class, KeycloakRefreshProps.class})
@Configuration
class PropsConfig {}
