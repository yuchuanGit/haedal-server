package com.sui.haedal.common;

import com.google.common.collect.Lists;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CollectionUtils extends org.springframework.util.CollectionUtils {

    public static <T> boolean isNotEmpty(T[] objects) {
        return !isEmpty(objects);
    }

    public static <T> boolean isEmpty(T[] objects) {
        return Objects.isNull(objects) || objects.length < 1;
    }

    /**
     * 集合分组
     * @param data 集合
     * @param size 每组大小
     * @return 集合分组结果
     */
    public static  List<List<?>> partition(Collection<?> data, int size) {
        if (CollectionUtils.isEmpty(data)) {
            return Lists.newArrayList();
        }

        List<List<?>> result = new ArrayList<>();
        if (size <= 0) {
            result.add(Lists.newArrayList(data.iterator()));
            return result;
        }

        for (int index = 0; index < data.size(); index += size) {
            result.add(data.stream().skip(index).limit(size).collect(Collectors.toList()));
        }
        return result;
    }

    /**
     * 集合转换
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 指定的值在集合中是否存在
     *
     * @param collection
     * @param element
     * @param <T>
     * @return
     */
    public static <T> boolean containsValue(Collection<T> collection, T element) {
        if (isEmpty(collection)) {
            return false;
        }

        for (T t : collection) {
            if (element.equals(t)) {
                return true;
            }
        }

        return false;
    }

    public static <T> Collection<T> emptyIfNull(final Collection<T> collection) {
        return collection == null ? Lists.newArrayList() : collection;
    }

    public static <T> T getFirst(List<T> collection) {
        return isEmpty(collection) ? null : collection.get(0);
    }

    /**
     * Check whether the given Array contains the given element.
     *
     * @param array   the Array to check
     * @param element the element to look for
     * @param <T>     The generic tag
     * @return {@code true} if found, {@code false} else
     */
    public static <T> boolean contains(@Nullable T[] array, final T element) {
        if (array == null) {
            return false;
        }
        return Arrays.stream(array).anyMatch(x -> ObjectUtil.nullSafeEquals(x, element));
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            return false;
        }
        return obj.getClass().isArray();
    }

    /**
     * Determine whether the given Collection is not empty:
     * i.e. {@code null} or of zero length.
     *
     * @param coll the Collection to check
     * @return boolean
     */
    public static boolean isNotEmpty(@Nullable Collection<?> coll) {
        return !org.springframework.util.CollectionUtils.isEmpty(coll);
    }

    /**
     * Determine whether the given Map is not empty:
     * i.e. {@code null} or of zero length.
     *
     * @param map the Map to check
     * @return boolean
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !org.springframework.util.CollectionUtils.isEmpty(map);
    }

    /**
     * 合并两个列表
     * @param list1
     * @param list2
     * @param <T>
     * @return
     */
    public static <T> List<T> union(List<T> list1, List<T> list2) {
        if (CollectionUtils.isEmpty(list1)) {
            return list2;
        }
        if (CollectionUtils.isEmpty(list2)) {
            return list1;
        }
        list1.addAll(list2);
        return list1;
    }

    /**
     * 升序合并
     * @param mergeLists
     * @param <T>
     * @return
     */
    public static <T> List<T> merge(List<T>[] mergeLists, Comparator<T> comparator) {
        if (CollectionUtils.isEmpty(mergeLists)) {
            return Lists.newArrayList();
        }
        if (mergeLists.length == 1) {
            return mergeLists[0];
        }
        Arrays.stream(mergeLists)
                .forEach(list -> {
                    Collections.sort(list, comparator);
                });
        return mergeInternal(mergeLists, 0, mergeLists.length - 1, comparator);
    }

    /**
     *
     * @param mergeLists
     * @param s
     * @param e
     * @param comparator
     * @param <T>
     * @return
     */
    private static <T> List<T> mergeInternal(List<T>[] mergeLists, int s, int e, Comparator<T> comparator) {
        if (s == e) {
            return mergeLists[s];
        }
        if (s > e) {
            return null;
        }
        int mid = (s + e) >> 1;
        return mergeTwoLists(mergeInternal(mergeLists, s, mid, comparator), mergeInternal(mergeLists, mid + 1, e, comparator), comparator);
    }

    /**
     * 升序合并列表
     * @param firstList
     * @param secondList
     * @param comparator
     * @param <T>
     * @return
     */
    public static <T> List<T> mergeTwoLists(List<T> firstList, List<T> secondList, Comparator<T> comparator) {
        firstList.addAll(secondList);
        Collections.sort(firstList, comparator);
        return firstList;
    }

    /**
     * 升序合并列表
     * @param firstList
     * @param secondList
     * @param comparator
     * @param <T>
     * @return
     */
    public static <T> List<T> mergeTwoListsReverse(List<T> firstList, List<T> secondList, Comparator<T> comparator) {
        firstList.addAll(secondList);
        Collections.sort(firstList, comparator.reversed());
        return firstList;
    }

    /**
     * 集合判重, 并输出重复元素集合
     * @param list
     * @param keyExtractor
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T, R> List<T> distinct(List<T> list, Function<? super T, R> keyExtractor) {
        List<T> distinctList = list.stream()
                .filter(distinctByKey(keyExtractor))
                .collect(Collectors.toList());
        return distinctList;
    }

    /**
     * 去重内部实现
     * @param keyExtractor
     * @return
     * @param <T>
     */
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * 判断集合是否有序排序
     * @param forward true：从小到大 false: 从大到小
     * @param nums 集合
     * @return 是否有序集合
     */
    public static boolean isSorted(boolean forward, List<Integer> nums) {
        if (nums.size() == 1) {
            return true;
        }
        for (int i = 0; i < nums.size() - 1; i++) {
            if (forward) {
                if (nums.get(i) > nums.get(i + 1)) {
                    return false;
                }
            } else {
                if (nums.get(i) < nums.get(i + 1)) {
                    return false;
                }
            }
        }
        return true;
    }

//    public static void main(String[] args) {
//        List<Long>[] arrs = new List[5];
//        arrs[0] = Lists.newArrayList(1L , 12L, 13L, 17L, 15L);
//        arrs[1] = Lists.newArrayList(2L, 37L, 13L, 15L);
//        arrs[2] = Lists.newArrayList(1L, 3L, 5L, 7L, 4L, 9L, 11L);
//        arrs[3] = Lists.newArrayList(3L, 6L, 10L, 107L, 52L, 44L, 14L);
//        arrs[4] = Lists.newArrayList(1L, 2L, 8L, 16L, 32L, 64L, 128L);
//
//        // 升序
//        System.out.println("升序序:");
//        merge(arrs, Long::compareTo).stream().forEach(ele -> System.out.println(ele));
//
//        // 降序
//        System.out.println("降序序:");
//        merge(arrs, Comparator.reverseOrder()).forEach(ele -> System.out.println(ele));
//    }

//    @Data
//    @AllArgsConstructor
//    static class TestClass {
//        private Long id;
//
//        private String desc;
//
//        private String attr1;
//
//        private String attr2;
//
//        private String attr3;
//
//        private String attr4;
//
//        public String buildIdempotency() {
//            return this.attr1 + this.attr2 + this.attr3 + this.attr4;
//        }
//    }
//
//    public static void main(String[] args) {
//        List<TestClass> all = Lists.newArrayList();
//        List<TestClass> redundants = Lists.newArrayList();
//
//        CollectionUtils.distinct(all, redundants, Comparator.comparing(testClass -> testClass.buildIdempotency()), TestClass::getId);
//
//        all.add(new TestClass(1L, "testclass1", "attr11",
//                "attr21", "attr31", "attr41"));
//        all.add(new TestClass(2L, "testclass2", "attr12",
//                "attr22", "attr32", "attr42"));
//        all.add(new TestClass(3L, "testclass1", "attr11",
//                "attr21", "attr31", "attr41"));
//        all.add(new TestClass(4L, "testclass3", "attr13",
//                "attr23", "attr33", "attr43"));
//        all.add(new TestClass(5L, "testclass1", "attr11",
//                "attr21", "attr31", "attr41"));
//
//        CollectionUtils.distinct(all, redundants, Comparator.comparing(testClass -> testClass.buildIdempotency()), TestClass::getId);
//
//        all.forEach(item -> System.out.println(item));
//        System.out.println();
//        redundants.forEach(item -> System.out.println(item));
//    }
}
