package com.goncharov.bdbot.config;

import com.goncharov.bdbot.models.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class BeanConfig {
    @Bean
    Map<Integer, Player> players() {
        return new HashMap<Integer, Player>();
    }
}
