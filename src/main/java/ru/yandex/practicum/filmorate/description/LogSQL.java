package ru.yandex.practicum.filmorate.description;

public enum LogSQL {
    SQL_EXCEPTION("SQL exception");

    private final String message;
    LogSQL(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}