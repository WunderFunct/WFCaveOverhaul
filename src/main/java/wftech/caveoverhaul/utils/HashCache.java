package wftech.caveoverhaul.utils;

import java.util.HashMap;

/*
Pseudo cache - will this method work best? It's just a hashmap at this moment; replaces real caches for certain
optimization strategies I'm testing.

Used in a ThreadLocal environment, so it shouldn't grow to be too large or impact memory too much
 */
public class HashCache<T, K>{

    @FunctionalInterface
    public interface ILambdaProvider<K> {
        K execute();
    }

    private HashMap<T, K> fakeCache = new HashMap<T, K>();
    private int maxSize = 0;

    public HashCache(int size){
        this.maxSize = size;
    }

    public K get(T ipair, ILambdaProvider<K> provider) {
        K val = fakeCache.get(ipair);

        if (val == null) {
            //For testing something regarding efficiency, was originally a semirandomized eviction strategy due to
            //#statistics
            if(fakeCache.size() > this.maxSize) {
                this.fakeCache = new HashMap<>();
            }

            val = (K) provider.execute();
            try {
                fakeCache.put(ipair, val);
            } catch (ClassCastException e) {
                //do nothing for now, testing something regarding concurrency
            }
        }

        return val;
    }
}