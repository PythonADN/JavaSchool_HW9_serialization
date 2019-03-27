package services;

import annotations.Cache;

import java.util.Date;
import java.util.List;

import static annotations.CacheType.*;


public interface Service_1 {
    @Cache(cacheType = IN_MEMORY, fileNamePrefix = "data", zip = true, identityBy = {1})
    List<String> run(String item, int value);
}
