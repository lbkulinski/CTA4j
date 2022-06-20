package com.cta4j.utils;

public record Body<T>(Status status, T content) {
    public enum Status {
        /**
         * The singleton instance representing the success status.
         */
        SUCCESS,

        /**
         * The singleton instance representing the error status.
         */
        ERROR
    } //Status

    public static <T> Body<T> success(T content) {
        return new Body<>(Status.SUCCESS, content);
    } //success

    public static <T> Body<T> error(T content) {
        return new Body<>(Status.ERROR, content);
    } //error
}