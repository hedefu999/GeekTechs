package com.netty.primary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class BasicComponents {
    static class AboutByteBuf{
        public static void printByteBuf(ByteBuf byteBuf){
            System.out.printf("ByteBuf printed: [readerIndex, writerIndex, capacity] = (%d,%d,%d) content = %s \n",
                    byteBuf.readerIndex(), byteBuf.writerIndex(), byteBuf.capacity(), byteBuf.toString(CharsetUtil.UTF_8));
        }
        /**
         此方法的打印结果：
         ByteBuf printed: [readerIndex, writerIndex, capacity] = (0,0,10) content =
         ByteBuf printed: [readerIndex, writerIndex, capacity] = (0,8,10) content = abcdefgh
         读取内容，让readIndex增加：97
         读取内容，让readIndex增加：98
         读取内容，让readIndex增加：99
         读取内容，让readIndex增加：100
         读取内容，让readIndex增加：101
         ByteBuf printed: [readerIndex, writerIndex, capacity] = (0,8,10) content = abcdefgh
         用readByte方法读取：97
         用readByte方法读取：98
         用readByte方法读取：99
         用readByte方法读取：100
         用readByte方法读取：101
         ByteBuf printed: [readerIndex, writerIndex, capacity] = (5,8,10) content = fgh
         在nio的ByteBuffer里有个position limit capacity的概念
         */
        static void elaborateReadWriteIndexOfByteBuf(){
            ByteBuf byteBuf = Unpooled.buffer(10);
            printByteBuf(byteBuf);
            for (int i = 0; i < 8; i++) {
                byteBuf.writeByte(i+'a');
            }
            printByteBuf(byteBuf);
            for (int i = 0; i < 5; i++) {
                System.out.println("读取内容，让readIndex增加：" + byteBuf.getByte(i));
            }//getByte(int index)方法不会让readerIndex前进，但readByte()会
            printByteBuf(byteBuf);
            for (int i = 0; i < 5; i++) { //5改成大于8的数会抛出IndexOutOfBoundsException
                System.out.println("用readByte方法读取：" + byteBuf.readByte());
            }
            printByteBuf(byteBuf);
        }
        static void readByteBuf(){
            String testContent = "今天依然是核平的一天呢";//33个字节，一个汉字占3个字节
            String testPhrase = "east new sound";//会分配42个字节，后面都是空的
            ByteBuf byteBuf = Unpooled.copiedBuffer(testPhrase, CharsetUtil.UTF_8);
            if (byteBuf.hasArray()){
                byte[] content = byteBuf.array();
                System.out.println(new String(content, CharsetUtil.UTF_8));
                printByteBuf(byteBuf);
            }
            System.out.println("readable bytes length = " + byteBuf.readableBytes());
            System.out.println((char)byteBuf.getByte(3));
            System.out.println(byteBuf.getCharSequence(0,3,CharsetUtil.UTF_8));//汉字要按3的整数倍取
        }

        public static void main(String[] args) {
            //elaborateReadWriteIndexOfByteBuf();
            readByteBuf();
        }
    }
}
