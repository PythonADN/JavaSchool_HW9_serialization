package services;

import annotations.Cache;

import java.util.List;

import static annotations.CacheType.IN_MEMORY;

public interface Service_2 {
    @Cache(cacheType = IN_MEMORY, listSize = 100_000)
    List<String> work(String item);
}
