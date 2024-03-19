package com.goncharov.bdbot.exceptions;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException() {
        super("Сначала напиши /start");
    }
}
