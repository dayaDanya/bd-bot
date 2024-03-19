package com.goncharov.bdbot.exceptions;

public class WrongMessageException extends RuntimeException{
    public WrongMessageException(String message) {
        super("Это что: " + message + "?");
    }
}
