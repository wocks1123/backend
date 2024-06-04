package com.swygbro.trip.backend.domain.user.util;

@FunctionalInterface
public interface CheckDuplication {
    boolean check(String data);
}
