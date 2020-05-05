package io.liberty.note;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.underlake.sdk.jackson2.MapperCache;

public class MapperUtil {
    final public static MapperCache CACHE = new MapperCache(newObjectMapper());

    private static ObjectMapper newObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

}
