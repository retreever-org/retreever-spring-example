package dev.retreever.example.exception;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException(String cause) {
        super("User not found", cause);
    }
}
