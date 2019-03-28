package services;

import annotations.Cache;

import java.util.Date;
import java.util.List;

import static annotations.CacheType.*;


public interface Service_1 {
//    @Cache(cacheType = IN_MEMORY, identityBy = {1}, listSize=10)
    @Cache(cacheType = FILE, zip=false, identityBy = {1}, listSize=10)
    List<String> run(String item, int value);
}
