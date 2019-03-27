package annotations;
import static annotations.CacheType.*;

public @interface Cache {
    CacheType cacheType() default FILE;
    Class[] identityBy() default {String.class};
    int listList() default -1;
    String fileNamePrefix() default "";
    boolean zip() default false;
}


