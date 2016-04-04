package main;

import java.util.*;
import java.util.stream.Stream;

public class MapUtil
{
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(Map.Entry::getValue))
                .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc( Map<K, V> map )
    {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> st = map.entrySet().stream();

        st.sorted(Collections.reverseOrder(Comparator.comparing(Map.Entry::getValue)))
                .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }
}