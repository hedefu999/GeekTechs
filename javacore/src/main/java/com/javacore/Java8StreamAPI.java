package com.javacore;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 流式API架构
 * interface AutoCloseable{void close()}
 * interface BaseStream<T, S extends BaseStream<T, S>> extends AutoCloseable
 *      提供iterator、spliterator、isParallel、sequential、parallel、unordered onClose(Runnable closeHandler) 方法
 * interface Stream<T> extends BaseStream<T, Stream<T>>
 *      提供 filter map mapToInt flatMap? flatMapToInt? distinct sorted peek? limit? skip? forEach toArray reduce collect min max count anyMatch? allMatch noneMatch findFirst? findAny? builder? empty of iterate? generate? concat
 * interface IntStream/LongStream extends BaseStream<Integer, IntStream>
 * 流式编程API案例展示
 * 对流排序不会修改数据源的顺序
 */
public class Java8StreamAPI {
    private static final Logger logger = getLogger(Java8StreamAPI.class);

    @Data
    @AllArgsConstructor
    public static class Student {
        public static Student DEFAULT = new Student("",0,0.0,"");
        private String name;
        private Integer number;
        private Double balance;
        private String city;
        public static Comparator NUMBER_COMPARATOR = new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o1.getNumber().compareTo(o2.getNumber());
            }
        };
        @Override
        public String toString() {
            return "Student{" + "name='" + name + '\'' + ", number=" + number + ", balance=" + balance + ", city='" + city + '\'' + '}';
        }
    }
    static List<Student> data = new ArrayList<Student>(){{
        add(new Student("jack",12,123.3,"shanghai"));
        add(new Student("java",21,1.8,"beijing"));
        add(new Student("car",1200,151.2,"newyork"));
        add(new Student("alice",134,45.6,"newyork"));
        add(new Student("solman", 160, 32.00,"shanghai"));
        add(new Student("carlo", 7740, 3.0,"newyork"));
    }};

    /**
     * #002-HowToGetStream
     */
    static class HowToGetStream{
        public static void main(String[] args) {
            Stream<String> hugeCityStream = Stream.of("beijing", "shanghai", "guangzhou");
            String[] countries = {"china","america","russia","japan"};
            Stream<String> countryStream = Arrays.stream(countries);

            List<String> strList = new ArrayList<>();
            Stream<String> strListStream = strList.stream();

            Map<Long,String> map = new HashMap<>();
            // map.stream()
            Stream<Entry<Long, String>> mapEntryStream = map.entrySet().stream();
        }
    }

    /**
     * 流操作的分类 #003-StreamOpsFeatures
     */
    static class StreamOpsFeatures{
        public static void main(String[] args) {
            System.out.println(data); //E
            Stream<Student> stream = data.stream();
            Stream<Student> filteredStream = stream.filter(stu -> stu.getNumber() > 3000); //A
            Stream<String> filteredMappedStream = filteredStream.map(Student::getName); //B
            List<String> stuNames = filteredMappedStream.collect(Collectors.toList()); //C
            System.out.println(filteredMappedStream.count());//D
            System.out.println(stuNames);
            System.out.println(data); //F
        }
        /**
         B处复用了A处返回的流，
         C处的collect与D处的count都是终端操作，都对filteredMappedStream进行操作，结果是D处抛出IllegalStateException: stream has already been operated upon or closed
         外层的E F行显示data源数据没有发生改变
         */
    }

    /**
     * filter #004-StreamMatchFindFilterDemo
     * Collectors#allMatch 是否全部匹配
     * anyMatch 是否存在一个或多个满足条件
     * noneMatch = !allMatch
     * findFirst 找到第一个满足条件的item, filter(xxx).findFirst()
     * findAny 在并行流中效率高于串行流，在串行流中 = findFirst
     */
    static class StreamMatchFindFilterDemo{
        public static void main(String[] args) {
            boolean gt7000 = data.stream().anyMatch(stu -> stu.getNumber() > 7000);
            boolean wangExist = data.stream().anyMatch(student -> student.getName().contains("wang"));
            logger.info("gt7000 = {}. wangExist = {}", gt7000, wangExist);
            Arrays.asList(1, 2, 1, 3, 3, 2, 4).stream().filter(i -> i % 2 == 0).distinct().forEach(System.out::println);
            System.out.println(data);
            List<Student> students = data.stream().filter(stu -> stu.getNumber() > 100).limit(3).collect(Collectors.toList());
            System.out.println(students);
        }
    }

    /**
     * #005-CommonMap
     *
     */
    static class CommonMapAndFlatMapDemo{
        public static void main(String[] args) {
            //求和,注意stream的数学计算不能用于金融业务，需要转BigDecimal
            double result;
            result = data.stream().mapToDouble(Student::getBalance).sum();
            System.out.println(result);
            // result = data.stream().map(item -> new BigDecimal(item.getBalance())).reduce(BigDecimal::doubleValue).sum();
            data.stream().mapToDouble(Student::getBalance).max().ifPresent(System.out::println);
            result = data.stream().mapToDouble(Student::getBalance).min().getAsDouble();
            System.out.println(result);
            result = data.stream().mapToInt(Student::getNumber).average().getAsDouble();
            System.out.println(result);
        }
    }

    /**
     * #006-FlatMap
     */
    static class StreamFlatMapDemo{
        @Data @AllArgsConstructor
        static class Teacher{
            private String name;
            private Integer age;
            private List<Student> students;
        }
        @Data @AllArgsConstructor
        static class Student{
            private String name;
            private Integer age;
            private String typeNumber;
        }
        public static void main(String[] args) {
            //使用映射流+并行流处理集合元素扩大2倍计算累乘积的写法
            Integer multiply = Arrays.asList(1, 2, 3, 4).parallelStream().map(item -> item * 2).reduce(1, (a, b) -> a * b);
            /**
             * 关于flatMap：用于一对多场景下，A对象包含B对象List属性，使用flatMap可以直接将A流映射为B流，flat就是将List<B>打扁了放到流里
             * 案例：提供一个List<Teacher> 一个Teacher里有一个List<Student>字段，提取所有的Student组成一个stream并打印
             */
            Student stu1 = new Student("jack",12,"1");
            Student stu2 = new Student("lucy",12,"3");
            Student stu3 = new Student("daniel",12,"3");
            Student stu4 = new Student("lily",12,"4");
            Teacher teacherWang = new Teacher("wang",32, Arrays.asList(stu1, stu2));
            Teacher teacherLee = new Teacher("lee",28,Arrays.asList(stu3, stu4));
            /**
             * 使用 static <T> Stream<T> Stream#concat(Stream<? extends T> a, Stream<? extends T> b) 合并两个流
             */
            Stream.concat(teacherWang.getStudents().stream(),teacherLee.getStudents().stream()).forEach(System.out::println);

            /**
             * 使用映射流 <R> Stream<R> Stream#flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);
             * distinct是终端操作，将使得流操作后关闭，不可重用
             */
            List<Student> students = Arrays.asList(teacherLee, teacherWang).stream().flatMap(teacher -> teacher.getStudents().stream()).collect(Collectors.toList());//.distinct().forEach(System.out::println);
/**
 * 使用映射流提取所有Student的number，去重的同时转为数字放到list里
 * 使用到 flatMapToInt mapToInt
 */
            List<Integer> studentNumerTags = Arrays.asList(teacherLee, teacherWang).stream().flatMapToInt(
                    teacher -> teacher.getStudents().stream().mapToInt(item -> Integer.parseInt(item.getTypeNumber())))
                    .distinct().collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
            System.out.println(studentNumerTags);
        }
    }

    /**
     * #007-StreamReduceDemo
     */
    static class StreamReduceDemo{
        static void testFirstKindOfReduce(){
            /**
             * reduce方法参数有多种
             * 只有一个BinaryOperator接口入参的
             * @see java.util.stream.Stream#reduce(java.util.function.BinaryOperator) 返回 Optional<T>
             * BinaryOperator接口的声明是 public interface BinaryOperator<T> extends BiFunction<T,T,T>
             *     其中声明了两个静态方法 minBy 和 maxBy
             */
            Integer integer = Stream.of(1, 2, 3, 4).reduce(Integer::sum).orElse(0);
            Integer result = Stream.of(2, 5, 4, 3).reduce((left, right) -> {
                logger.info("current: left = {}, right = {}", left, right);
                // left += right;
                // return left;
                right += left;
                return right;
            }).orElse(0);
            System.out.println(result);
        }
        static void testSecondKindOfReduce(){
            /**
             * @see Stream#reduce(T identity, java.util.function.BinaryOperator<T> accumulator) 返回泛型T
             * identity用于指定stream遍历的初始值,当stream中没有元素时，就直接返回这个identity
             * 带有identity入参的方法返回的就不是Optional
             */
            Integer result2 = Stream.of(2, 5, 4, 3).reduce(3, (sum, item) -> {
                sum += item;
                logger.info("summing, current: sum = {}, item = {}", sum, item);
                return sum;//留意第一行日志
            });
            System.out.println(result2);

            Integer result3 = new ArrayList<Integer>(0).stream().reduce(3, (sum, item) -> {
                logger.info("current: sum = {}, item = {}", sum, item);
                sum += item;
                return sum;//注意返回的是中间结果
            });
            System.out.println(result3);
        }
        static void testFullParamsReduce(){
            /**
             * 最复杂入参的reduce，方法签名
             * @see java.util.stream.Stream#reduce(U identity, java.util.function.BiFunction<U, ? super T, U> accumulator, java.util.function.BinaryOperator<U> combiner)
             * 包含一个累加器accumulator和一个合成器combiner,combiner用于将accumulator的结果合并起来。应用于并行流，详细测试见并行流 ParallelStreamDemo
             * 这个方法直接返回结果，而不是Optional
             */
            Integer multiplyResult2 = Stream.of(1,2,3,4,5).reduce(10, (total, item) -> total*item*2, (left, right) -> left*right);
            Integer multiplyResult = Stream.of(1,2,3,4,5).reduce(10, new BiFunction<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer totalMulti, Integer item) {
                    logger.info("accumulator：totalMulti = {}, item = {}", totalMulti, item);
                    return totalMulti * (item * 2);
                }
            }, new BinaryOperator<Integer>() {
                @Override
                public Integer apply(Integer left, Integer right) {
                    logger.info("combiner：left = {}, right = {}", left, right);
                    return left * right;
                }
            });
            System.out.println(multiplyResult);
            //下述是企业级开发中常用的BigDecimal在流中的计算
            double bigDecimalResult = Stream.of(12.8, 13.87, 15.99, 23.0).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
            System.out.println(bigDecimalResult);//65.66
        }
        public static void main(String[] args) {
            // testFirstKindOfReduce();
            // testSecondKindOfReduce();
            testFullParamsReduce();
        }
    }

    /**
     * #008-ParallelStreamDemo
     */
    static class ParallelStreamDemo{
        /**
         * 并行流可以显著提高流处理的速度
         * 应用到并行流的任何操作都必须符合缩减操作的三个约束条件：无状态、不干预、关联性
         */
        static List<Integer> ints = Arrays.asList(1,2,3,4,5,6);
        static class ParallelStreamStart{
            static void firstMeetParallelStream(){
                ints.parallelStream().reduce(Integer::sum).ifPresent(System.out::println);
                Optional<Integer> multiply = ints.parallelStream().reduce((a,b) -> a*b);
                multiply.ifPresent(result -> logger.info("计算总乘积：{}", result));
                Integer parallelResult = ints.parallelStream().reduce(1, (a, b) -> a * b, (a, b) -> a * b);
                logger.info("乘积是：{}", parallelResult);//720
            }
            static void concurrencyFeaturesOfParallelStream(){
                //对集合中的每个元素扩大2倍后计算总和
                Integer summary = Stream.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16).parallel().reduce(0, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer identity, Integer item) {
                        logger.info("{}: accumulator：identity = {}, item = {}", Thread.currentThread().getName(), identity, item);
                        return identity + item;
                    }
                }, new BinaryOperator<Integer>() {
                    @Override
                    public Integer apply(Integer left, Integer right) {
                        logger.info("{}: combiner：left = {}, right = {}", Thread.currentThread().getName(), left, right);
                        return left + right;
                    }
                });
                System.out.println(summary);
                /** 并行流特性总结：
                 * accumulator每次都会使用identity参与运算
                 * accumulator本身是并发取数的，所以从List取数操作的顺序是无序的
                 * accumulator与combiner的各自到执行也是并发的，收集的同时在进行累积操作
                 * 进行流式处理的List中有n个元素(一个元素看作一个任务)，accumulator执行n次，combiner执行(n-1)次
                 */
            }
            /**
             * 如果集合或数组中的元素是有序的，对应的流也是有序的，鉴于无序流可以提升点性能，所以可以主动要求使用无序流unordered()???
             * 如果希望并行流操作时也保持顺序，可使用forEachOrdered()方法
             */
            static void advanceFeaturesOfStream(){
                //并行流里进行最大值比较效率更高
                int intMax = data.stream().parallel().mapToInt(Student::getNumber).max().orElse(0);
                System.out.println(intMax);

                data.stream().map(Student::getName).forEach(System.out::println);
                System.out.println("=-=-=-= 默认并行流是无序的 =-=-=-=");
                data.stream().parallel().map(Student::getName).forEach(System.out::println);
                System.out.println("=-=-=-= 指定有序（原始顺序）的并行流 =-=-=-=");
                data.stream().parallel().map(Student::getName).forEachOrdered(System.out::println);
                System.out.println("=-=-=-= 指定无序,似乎没啥效果 =-=-=-=");
                data.stream().unordered().map(Student::getName).forEach(System.out::println);
            }
            public static void main(String[] args) {
                firstMeetParallelStream();
                // concurrencyFeaturesOfParallelStream();
                /* advanceFeaturesOfStream(); */
            }
        }
    }

    /**
     * #009-CollectorsAPI
     * Stream提供了两种collect方法,collect是一个终端操作
     * - <R, A> R collect(Collector<? super T, A, R> collector);
     * - <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner);
     */
    static class CollectOperationDemo{
        public static void testJoining() {
            String sbuffer = data.stream().map(Student::getName).collect(Collectors.joining());
            String namesWithConnector = data.stream().map(Student::getName).collect(Collectors.joining(",", ":", ";"));//:jack1,java2,car3,soldier4;
            System.out.println(namesWithConnector);
        }

        /**
         * 使用mapping操作将元素放入集合，使用groupingBy确定key和集合作为value组成的map
         * 学生可能在同一个城市，按照城市分组，返回map<cityName, Set<StudentName>>
         */
        public static void testMapping() {
            data.add(new Student("carls",7741,2.0,null));
            Map<String, Set<String>> cityNamesMap = data.stream().collect(
                    Collectors.groupingBy(Student::getCity,
                            Collectors.mapping(Student::getName, Collectors.toSet()) //这里toList也可以，但那样无法去重
                    )
            );
            System.out.println(cityNamesMap);
            /**
             * 相同城市的重名学生toSet时可以达到去重效果
             * name为null不影响：{newyork=[null, car3, carl], shanghai=[jack1, soldier4], beijing=[java2]}
             * cityName为null程序抛出NPE：element cannot be mapped to a null key
             */
            // Collectors.groupingBy(Student::getCity,
            //         Collectors.mapping(Student::getName, Collectors.toSet()) //这里toList也可以，但那样无法去重
        }

        /**
         * 求最值
         */
        public static void testExtremeValueCalc() {
            //3. 找到number最大/最小的学生
            Integer maxNumber = data.stream().collect(Collectors.collectingAndThen(Collectors.reducing((c1, c2) -> c1.getNumber() > c2.getNumber() ? c1 : c2), item -> item.orElse(Student.DEFAULT).getNumber()));
            System.out.println("学生的最大number = " + maxNumber);
            Optional<Student> collect = (Optional<Student>) data.stream().collect(Collectors.minBy(Student.NUMBER_COMPARATOR));
            Student minNumStu = ((Optional<Student>) data.stream().collect(Collectors.reducing(BinaryOperator.minBy(Student.NUMBER_COMPARATOR)))).get();
            //显然，使用自定义Comparator会另代码看起来很奇怪，而且编译器无法推测类型
            Student maxNumStu = data.stream().collect(Collectors.reducing(BinaryOperator.maxBy(Comparator.comparing(Student::getNumber)))).get();
            Integer minNumber = data.stream().collect(Collectors.minBy(Comparator.comparing(Student::getNumber))).get().getNumber();//reducing动作可以去掉
            Integer minNum = data.stream().min(Comparator.comparing(Student::getNumber).thenComparingDouble(Student::getBalance)).orElse(Student.DEFAULT).getNumber();//还可以进一步简化
            System.out.println(maxNumStu);
            System.out.println(minNumber);
            System.out.println("学生最小number = " + minNum);
        }

        public static void testCountSumAndSummarize() {
            //额， list转stream后计数。。。还有三种方式
            data.stream().count();
            Long counting = data.stream().collect(Collectors.counting());
            Long reducing = data.stream().collect(Collectors.reducing(0L, e -> 1L, Long::sum));
            //求number总和
            Integer numberSum = data.stream().collect(Collectors.summingInt(Student::getNumber));
            //IntSummaryStatistics的设计,对int流进行摘要计算
            IntSummaryStatistics numberSummarize = data.stream().collect(Collectors.summarizingInt(Student::getNumber));
            Double numberAverage = data.stream().collect(Collectors.averagingInt(Student::getNumber));
            logger.info("summing = {}, summarize = {}, numberAverage = {}", numberSum, numberSummarize, numberAverage);
        }
        /**
         * 分组
         * Collectors.groupingBy方法类似SQL的group by
         */
        public static void testGroupingBy() {
            //1级分组:按城市分组
            Map<String, List<Student>> groupedStudent = data.stream().collect(Collectors.groupingBy(Student::getCity));
            //上面写法等同于 data.stream().collect(Collectors.groupingBy(Student::getCity, Collectors.toList())); 见源码
            System.out.println(groupedStudent);
            //2级分组：按城市+number分组。。虽然number各不相同
            Map<String, Map<Integer, List<Student>>> cityNumberGroupedStuMap = data.stream().collect(
                    Collectors.groupingBy(Student::getCity, Collectors.groupingBy(Student::getNumber)));
            //groupingBy的第二个参数是Collector，而其本身返回的也是Collector，所以可以嵌套。
            //向groupBy传递其他类型的Collector
            Map<String, Long> groupByCityGetCountMap = data.stream().collect(Collectors.groupingBy(Student::getCity, Collectors.counting()));
            System.out.println(groupByCityGetCountMap);
            //更复杂的Collector见testOther
        }
        /**
         * 分区
         */
        public static void testPartitioningBy() {
            //partitioningBy方法接收一个谓词
            //按number是否大于1000给学生分组
            Map<Boolean, List<Student>> partitionedStus = data.stream().collect(Collectors.partitioningBy(student -> student.getNumber() > 1000));
            System.out.println(partitionedStus);
        }


        /**
         * #010-geekOperation
         * stream api的 "九"重天，效果像SQL筛选数据，代码将追求奇技淫巧
         * collectingAndThen
         * groupingBy
         */
        static void geekOperation() {
            /**
             //1. 生成一个不可变List
             List<Student> immutableStus = data.stream().collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
             immutableStus.iterator().remove();//UnsupportedOperationException
            */
            //需求： 找到每个城市里学生number最大的学生，按cityName - stuName组成map返回，你能给出几种写法？
            //法 1
            Map<String,String> cityMaxStuNumberNameMap = data.stream().collect(
                    Collectors.groupingBy(Student::getCity,
                            Collectors.collectingAndThen(
                                    Collectors.reducing(( c1,  c2) -> c1.getNumber() > c2.getNumber() ? c1 : c2),//为什么最后不能用这个替换？？？ BinaryOperator.maxBy(Student.NUMBER_COMPARATOR)
                                    item -> item.orElse(Student.DEFAULT).getName()) //Optional::get
                    )
            );
            //法 2
            Map<String,String> cityMaxStuNumberNameMap2 = data.stream().collect(
                    Collectors.groupingBy(Student::getCity,
                            Collectors.collectingAndThen(
                                    Collectors.reducing(BinaryOperator.maxBy(Comparator.comparing(Student::getNumber))),//要这样写才可以，似乎Student.NUMBER_COMPARATOR这个内部comparator用不到了，显然stream让你连这个都省了
                                    item -> item.orElse(Student.DEFAULT).getName()) //Optional::get
                    )
            );
            System.out.println(cityMaxStuNumberNameMap2);


            //需求： 将一个城市里number最大和最小的两个学生组成List作为value与cityName作为key组成map返回，如何用一行代码实现！
            Map<String, List<Student>> cityNameMaxMinStudentsMap = data.stream()
                    .collect(Collectors.toMap(Student::getCity, stu -> Arrays.asList(stu, stu),
                            (l1,l2) -> Arrays.asList(
                                    (l1.get(0).getNumber()>l2.get(0).getNumber()? l2: l1).get(0),
                                    (l1.get(1).getNumber()<l2.get(1).getNumber()? l2: l1).get(1))));
            //有点问题，在一个城市只有一个学生的情况下会导致list中有两个重复的数据
            System.out.println(cityNameMaxMinStudentsMap);

        }

        /**
         * java8 下慎用Collectors.toMap
         * 将Student的number作为key，Student作为value组成map返回
         */
        static void findFeaturesOfCollectorsToMap() {
            data.add(new Student("spy", 160, 320.00,""));
            //spy因number重复导致hashMap key重复，从而导致下面一行的写法抛出merge相关的异常！异常的定义见Collectors.throwingMerger
            // Map<Integer, Student> containerMap = data.stream().collect(Collectors.toMap(item -> item.getNumber(), item -> item));

            //解决方案 1:
            //只能返回newValue（表示替换为新值）和null（表示移除这个重复的key）
            HashMap<Integer, Student> complicateAPIMap = data.stream().collect(Collectors.toMap(
                    item -> item.getNumber(),
                    item -> item,
                    (existValue, puttingValue) -> existValue,
                    HashMap::new));
            //下述写法的几个特点：1.这个返回类型是HashMap会泛型推断出错，无法编译；2.兼容key冲突；3.List中有一个元素的value为null会抛出NPE，原因见Map的merge函数
            Map<Integer, String> complicateAPIMap2 = data.stream().collect(Collectors.toMap(
                    item -> item.getNumber(),
                    item -> item.getName(),
                    (existValue, puttingValue) -> existValue));

            //最佳实践：
            Map<Integer, Student> containerMap2 = data.stream().collect(HashMap::new, (map, item) -> map.putIfAbsent(item.getNumber(), item), HashMap::putAll);
            containerMap2 = data.stream().collect(HashMap::new, (map, item) -> map.putIfAbsent(item.getNumber(), item), new BiConsumer<HashMap<Integer, Student>, HashMap<Integer, Student>>() {
                @Override
                public void accept(HashMap<Integer, Student> integerStudentHashMap, HashMap<Integer, Student> integerStudentHashMap2) {
                    integerStudentHashMap.putAll(integerStudentHashMap2);
                    logger.info("串行流不会打印这一行");
                }
            });
            System.out.println(containerMap2);
            // ArrayList<Integer> noDuplicateIds = data.stream().map(Student::getNumber).collect(Collectors.collectingAndThen(Collectors.toSet(), ArrayList::new));
            // System.out.println(noDuplicateIds);
        }

        public static void main(String[] args) {
            // testGroupingBy();
            // testPartitioningBy();
            // testCountSumAndSummarize();
            // testCollectorsToMap();

            // geekOperation();
            findFeaturesOfCollectorsToMap();
        }
    }

    /**
     * 可拆分迭代器Spliterator
     * Spliterator不同于传统iterator的一点是其支持并行迭代
     */
    static class SpliteratorDemo{
        /**
         * Spliterator的基础用法
         */
        static class SpliteratorPrimaryUsage{
            public static void main(String[] args) {
                int[] intArray = new int[]{1,2,3,4,5,6,7,8,9,10};
                Spliterator.OfInt intSpliterator = IntStream.range(0,intArray.length).spliterator();
                intSpliterator.forEachRemaining((int value) -> { //这里不写value的类型编译器因为函数重载会不知道调哪一个
                    System.out.println(Thread.currentThread().getName()+"--"+value);//value是0-9这是声明10个元素的默认值
                });

                Spliterator.OfInt intSpliterator2 = Arrays.spliterator(intArray);
                Spliterator.OfInt splitLeft = intSpliterator2.trySplit();
                Spliterator.OfInt splitRight = intSpliterator2.trySplit();
                System.out.println("-=-=-=-= show left -=-=-=-=");
                splitLeft.forEachRemaining((int value) -> {
                    System.out.println(Thread.currentThread().getName()+"--"+value);
                });
                System.out.println("-=-=-=-- show right -=-=-");
                splitRight.forEachRemaining((int value) -> {
                    System.out.println(Thread.currentThread().getName()+"--"+value);
                });
                System.out.println("-=-=-=-- show origin intSpliterator -=-=-");
                intSpliterator2.forEachRemaining((int value) -> {
                    System.out.println(Thread.currentThread().getName()+"--"+value);
                });
                /* 每次trySplit都是分到剩余元素的一半数量
                 * -=-=-=-= show left -=-=-=-=
                 * main--1
                 * main--2
                 * main--3
                 * main--4
                 * main--5
                 * -=-=-=-- show right -=-=-
                 * main--6
                 * main--7
                 * -=-=-=-- show origin intSpliterator -=-=-
                 * main--8
                 * main--9
                 * main--10
                 */
            }
        }
        static class OracleOfficialSpliteratorDemo{
            static class TaggedArraySpliterator<T> implements Spliterator<T>{
                private final Object[] array;
                private int origin;
                private final int fence;

                public TaggedArraySpliterator(Object[] array, int origin, int fence) {
                    this.array = array;
                    this.origin = origin;//index
                    this.fence = fence;//最大index + 1
                }

                @Override
                public void forEachRemaining(Consumer<? super T> action) {
                    for (;origin < fence;origin += 2){
                        String format = String.format("data = %d, tag = %s;", array[origin], array[origin + 1]);
                        action.accept((T)format);
                    }
                }

                @Override
                public boolean tryAdvance(Consumer<? super T> action) {
                    if (origin < fence){
                        action.accept((T)array[origin]);
                        origin += 2;//成对存储，所以跳2
                        return true;
                    }else
                        return false;
                }

                @Override
                public Spliterator<T> trySplit() {
                    int low = origin;
                    int mid = ((low + fence) >>> 1) & ~1;//通常(low+high)/2但不好控制结果的奇偶。末尾取0，向前取偶数。取偶数的原因是array中的数据是成对存储的
                    if (low < mid){
                        origin = mid;//使用后一般，前一般交给新创建的TaggedArraySpliterator
                        return new TaggedArraySpliterator<>(array, low, mid);
                    }else
                        return null;
                }

                @Override
                public long estimateSize() {
                    return (fence - origin)/2;
                }

                @Override
                public int characteristics() {
                    return ORDERED|SIZED|IMMUTABLE|SUBSIZED;
                }

            }
            static class TaggedArray<T>{
                private final Object[] elements;
                TaggedArray(T[] data, Object[] tags){
                    int size = data.length;
                    //data与tags数量上不一致抛出异常
                    if (tags.length != size) throw new IllegalArgumentException();
                    //将data与tags交替保存到elements中
                    this.elements = new Object[2*size];
                    for (int i = 0, j = 0; i < size; i++) {
                        elements[j++] = data[i];
                        elements[j++] = tags[i];
                    }
                }
                public Spliterator<T> spliterator(){
                    return new TaggedArraySpliterator<>(elements, 0, elements.length);
                }
            }

            public static void main(String[] args) {
                Integer[] data = {1,2,3,4,5,6,7,8};
                Character[] tags = {'A','B','C','D','E','F','G','H'};
                TaggedArray<String> taggedArray = new TaggedArray(data,tags);
                //使用流打印内容，根据parallel的bool值设置决定是否并行打印
                // StreamSupport.stream(taggedArray.spliterator(), false).forEach(System.out::println);
                /*
                 * data = 1, tag = A;
                 * data = 2, tag = B;
                 * data = 3, tag = C;
                 * ...
                 */
                StreamSupport.stream(taggedArray.spliterator(), true).forEach(System.out::println);
                //打印顺序乱了，不知道意味着啥
            }
        }

    }

    static class QuestionToResearch{
        public static void main(String[] args) {
            //使用reduce似乎可以当作collect使用,但在并行流下会乱掉
            ArrayList<Integer> accResult = Stream.of(1,2,3,4).parallel().reduce(new ArrayList<>(), (accumulator, item) -> {
                accumulator.add(item);
                return accumulator;
            }, (left, right) -> {
                left.addAll(right);
                return left;
            });
            System.out.println(accResult);
            /**
             * 打印结果还会变。。。
             * [1, 2, 4, 1, 2, 3, 1, 2, 4, 1, 2, 3, 1, 2, 4, 1, 2, 3, 1, 2, 4, 1, 2, 3]
             * [null, null, 2, null, null, 2, 4, null, null, 2, null, null, 2, 4, null, null, 2, null, null, 2, 4, null, null, 2, null, null, 2, 4]
             */
        }
    }

    /**
     * Collection.stream().forEach() 与 Collection.forEach() 的区别
     */
    static class WhatsTheDifferenceBetweenStreamForEach{
        static List<String> globalList = Arrays.asList("A", "B", "C", "D");
        static ArrayList<String> globalArrayList = new ArrayList<String>(){{
            add("jack");
            add("lucy");
            add("daniel");
            add("java");
        }};
        static Consumer<String> globalConsumer = System.out::print;
        //差异1: stream.forEach在并行流下元素顺序会乱掉。
        static class DifferenceOne{
            public static void main(String[] args) {
                for(String s : globalList) {
                    System.out.print(s);
                }
                System.out.println();
                //将lambda表达式赋值局部变量是怎么写的
                //错误写法：Consumer<String> consumer = s -> { System.out::println };
                // Consumer<String> consumer3 = s -> {System.out.println(s);};
                globalList.forEach(globalConsumer);
                System.out.println();
                globalList.stream().forEach(globalConsumer);//.parallelStream()
            }
        }
        //差异2: stream.forEach会忽略List中定义的iterator，而仅仅简单地顺序取元素。Collection.forEach在自定义List中的iterator
        static class DifferenceTwo{
            static class MyReverseList extends ArrayList<String> {
                @Override
                public Iterator<String> iterator() {
                    int startIndex = this.size() - 1;
                    List<String> list = this;
                    Iterator<String> it = new Iterator<String>() {
                        private int currentIndex = startIndex;
                        @Override
                        public boolean hasNext() {
                            return currentIndex >= 0;
                        }
                        @Override
                        public String next() {
                            String next = list.get(currentIndex);
                            currentIndex--;
                            return next;
                        }
                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                    return it;
                }
            }
            public static void main(String[] args) {
                List<String> myList = new MyReverseList();
                myList.addAll(globalList);
                for (String str : myList){
                    System.out.print(str);
                }
                System.out.println();
                //下面两个是一样的
                myList.forEach(globalConsumer);
                myList.stream().forEach(globalConsumer);
                /*
                  上述两种遍历的差异见源码：java.util.ArrayList.forEach，可见其内部是直接对本地数组做fori遍历
                  而增强foreach是调内部类Iterator不断get(index++)取元素，get底层才是对数组操作
                  这大概就是forEach性能优于foreach
                * */
            }
        }
        /*
        差异3: 移除元素上的差异，foreach时执行ArrayList#remove操作直接抛出ConcurrentModificationException异常(fail-fast机制)
            而forEach并不保证ConcurrentModificationException能被抛出来，所以不可以依赖此异常
        * */
        static class DifferenceThree{
            static void removeItemInforeach(){
                // globalList.remove("A");//众所周知 UnsupportedOperationException
                // globalArrayList.remove("daniel"); 提前remove掉就不会抛异常了
                for (String item : globalArrayList){
                    System.out.println(item + " " + globalList.size());
                    if (item.equals("jack")){
                        // globalArrayList.remove("daniel");//ConcurrentModificationException
                        // globalArrayList.set(2,"hello"); set不会引起啥问题
                    }/*
                       jack 4
                       ConcurrentModificationException */
                }
                globalArrayList.stream().forEach(s -> {
                    System.out.println(s + " " + globalList.size());
                    if (s != null && s.equals("jack")) {
                        // globalArrayList.remove("daniel");
                        // globalArrayList.set(2,"world"); set操作都没啥问题，但在并行流下set元素会因并发问题引起未知结果
                    }/* 打印结果表明:
                       jack 4
                       lucy 4
                       java 4
                       null 4
                       ConcurrentModificationException */
                    System.out.println();
                });
            }
            static void removeItemByIterator(){
                Iterator<String> iterator = globalArrayList.iterator();
                for (String item : globalArrayList){
                    if (item.equals("jack")){
                        // iterator.remove();//IllegalStateException
                    }
                }
                //这才是正统的移除元素的方式
                while (iterator.hasNext()){
                    String next = iterator.next();
                    if (next.equals("jack")){
                        iterator.remove();
                    }
                }
            }
            public static void main(String[] args) {
                removeItemInforeach();
            }
        }
        /*
          性能差异：
        * */
        static class EffectivenessComparison{
            @Data @AllArgsConstructor
            static class User{
                private String name;
                private String id;
            }
            private static void testForeach(List<User> userList) {
                for (User user : userList) {
                    user.hashCode();
                }
            }
            private static void testLambda(List<User> userList) {
                userList.forEach(user -> user.hashCode());
            }
            private static List<User> initList(int size) {
                List<User> userList = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    userList.add(new User("user" + i, String.valueOf(i)));
                }
                return userList;
            }
            public static void main(String[] args) {
                List<User> userList = initList(10000);
                for (int i = 1; i < 11; i++) {
                    System.out.println("--------------------第" + i + "次");
                    long t1 = System.nanoTime();
                    testLambda(userList);
                    long t2 = System.nanoTime();
                    testForeach(userList);
                    long t3 = System.nanoTime();
                    System.out.println("lambda---" + (t2 - t1) / 1000 + "μs");
                    System.out.println("增强for--" + (t3 - t2) / 1000 + "μs");
                }
                /**
                 * 多次重复遍历，按微秒记，两种遍历方式都是耗时越来越少，第一次遍历streamForEach要比enforcedForEach慢很多，
                 * 两次遍历后streamForEach一直比enforcedForEach快
                 * |  +
                 * |     +
                 * |  -     +
                 * |     -   -  +   -
                 * |               +   -
                 * —— —— —— —— —— —— ——
                 * +表示lambda的stream#foreach， - 表示增强foreach
                 */
            }

        }

    }
}
