package com.goncharov.bdbot.config;

import com.goncharov.bdbot.models.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class BeanConfig {
    @Bean
    Map<Integer, Player> players() {
        return new HashMap<Integer, Player>();
    }
    @Bean
    Map<Integer, List<String>> victims(){
        return new HashMap<Integer, List<String>>();
    }
    @Bean
    Queue<String> citizens(){
        return new LinkedList<String>();
    }
}
