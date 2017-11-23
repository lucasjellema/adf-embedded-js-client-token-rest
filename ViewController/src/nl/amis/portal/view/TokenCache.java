package nl.amis.portal.view;

import java.util.HashMap;
import java.util.Map;

public class TokenCache {

    static private Map<String, Object> cache = new HashMap<String, Object>();

    public TokenCache() {
        super();
    }

    public void storeInCache(String key, Object value) {
        // the assumption is that the key is a valid JWT token
        // we could add checks on validity and freshness of token
        cache.put(key, value);
    }

    public Object retrieveFromCache(String key) {
        // the assumption is that the key is a valid JWT token
        // we could add checks on validity and freshness of token
        return cache.get(key);
    }

}
