package me.adrjan.apj.result;

import io.javalin.http.HttpStatus;

public record RequestResult(HttpStatus status, Object body) {
}