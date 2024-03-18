package com.goncharov.bdbot.exceptions;

public class IdAlreadyUsedException extends RuntimeException{
    public IdAlreadyUsedException(int id) {
        super("Номер " + id + " уже используется((\nПопробуй еще раз плс");
    }
}
