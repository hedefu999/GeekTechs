package com.javase;

public class Enumerations {
    /**
     * 枚举可以实现接口
     */
    static class EnumImplementInterface {
        public interface IOperation {
            double apply(double x, double y);
        }
        public enum Operation implements IOperation{
            PLUS("+") {
                @Override
                public double apply(double x, double y) {
                    return x + y;
                }
            }, MINUS("-") {
                @Override
                public double apply(double x, double y) {
                    return x - y;
                }
            }, TIMES("*") {
                @Override
                public double apply(double x, double y) {
                    return x * y;
                }
            }, DIVIDE("/") {
                @Override
                public double apply(double x, double y) {
                    return x / y;
                }
            };
            private final String symbol;
            Operation(String symbol) {
                this.symbol = symbol;
            }
            public static void main(String[] args) {
                double x = 4;
                double y = 2;
                Operation.PLUS.apply(x,y);
            }
        }
        public enum ExtOperation implements IOperation {
            EXP("^") {
                @Override
                public double apply(double x, double y) {
                    return Math.pow(x, y);
                }
            };
            private final String symbol;
            ExtOperation(String symbol) {
                this.symbol = symbol;
            }
        }
        static class TestCases{
            private static <T extends Enum<T> & IOperation> void testIOperationEnums(Class<T> operation, double x, double y){
                for (T op : operation.getEnumConstants()) {
                    op.apply(x, y);
                }
            }
            public static void main(String[] args) {
                testIOperationEnums(ExtOperation.class, 2,3);
            }
        }
    }
    /**
     * 使用枚举写一个单例类，摘录自Caffine
     */
    static class EnumSingleton{
        interface Buffer<E> {
            int put(E e);
            E acquire();
        }
        //使用枚举实现一个单例，枚举类DisabledBuffer只有一个枚举INSTANCE，所以接口实现方法可以放在父类DisabledBuffer，不必写在枚举里
        enum DisabledBuffer implements Buffer<Object>{
            //INSTANCE2{
            //    @Override
            //    public int put(Object o) {
            //        return super.put(o);
            //    }
            //},
            INSTANCE;

            @Override
            public int put(Object o) {
                return 1;
            }
            @Override
            public Object acquire() {
                return new Object();
            }
        }
        public static void main(String[] args) {
            DisabledBuffer instance = DisabledBuffer.INSTANCE;
        }
    }
}
