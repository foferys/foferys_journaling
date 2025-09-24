package com.foferys_journal.fofejournal.exceptions;

//questa classe è stata creata per gesitre errori specifici nel controllo della password, in particolare nella classe UserService
public class PasswordMismatchException extends RuntimeException{
    public PasswordMismatchException(String message) {
        super(message);
    }
}
