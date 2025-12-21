package fr.joffreybonifay.ccpp.usermanagement.infrastructure.configuration;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.command.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandBus commandBus() {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        return simpleCommandBus;
    }


}
