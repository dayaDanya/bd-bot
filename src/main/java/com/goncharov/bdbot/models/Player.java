package com.goncharov.bdbot.models;

import lombok.*;

@Builder
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class Player {
    private int id;

    private String username;

    private Role role;
}
