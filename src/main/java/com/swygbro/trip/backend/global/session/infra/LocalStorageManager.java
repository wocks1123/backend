package com.swygbro.trip.backend.global.session.infra;

import com.swygbro.trip.backend.global.session.domain.SessionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class LocalStorageManager implements SessionManager {
    private final Map<String, Object> storage = new ConcurrentHashMap<>();

    @Override
    public void setAttribute(@NotNull String key, @Nullable Object value) {
        storage.put(key, value);
    }

    @Override
    public Object getAttribute(@NotNull String key) {
        return storage.get(key);
    }

    @Override
    public void removeAttribute(@NotNull String key) {
        storage.remove(key);
    }

    @Override
    public void invalidate() {
        storage.clear();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return storage;
    }

}
