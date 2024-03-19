package com.goncharov.bdbot.exceptions;

public class UsernameAlreadyInUseException extends RuntimeException{
    public UsernameAlreadyInUseException() {
        super("Бро, не надо...\nИспользуй кнопки");
    }
}
