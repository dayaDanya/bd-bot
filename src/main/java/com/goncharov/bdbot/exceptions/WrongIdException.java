package com.goncharov.bdbot.exceptions;

public class WrongIdException extends RuntimeException{
    public WrongIdException(int id) {
        super("Скорее всего номер " + id + " меньше 1 или больше 18. " +
                "Или я где-то накосячил, хз))\nПопробуй еще раз.");
    }
}
