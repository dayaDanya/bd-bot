package com.goncharov.bdbot.config;

import com.goncharov.bdbot.models.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BeanConfig {
    @Bean
    List<Player> playerList() {
        return new ArrayList<Player>();
    }
}
