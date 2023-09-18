package com.misc.javaagent.senior;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * 此处使用的asm可以换成其他类库的，这里使用的是JDK自带的
 */
public class TimeAgent {
    static class TimeClassFileTransformer implements ClassFileTransformer{
        //入参classFileBuffer表示原始的字节码，方法返回值表示真正要进行加载的字节码
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            //classFileBuffer是原始的字节码，方法返回值byte[]表示真正要加载的字节码。
            if (className.startsWith("java")||className.startsWith("jdk")||className.startsWith("sun")||className.startsWith("com/sun")||className.startsWith("com/misc/javaagent/senior")){
                //return null或者抛出异常会执行原来的字节码
                return null;
            }
            System.out.println("loaded class: " + className);
            //asm处理代码？？？
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(reader,ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            reader.accept(new TimeClassVisitor(writer), ClassReader.EXPAND_FRAMES);
            return writer.toByteArray();
        }
    }
    static class TimeClassVisitor extends ClassVisitor{
        public TimeClassVisitor(ClassVisitor visitor){
            super(Opcodes.ASM5, visitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = cv.visitMethod(access,name,desc,signature,exceptions);
            return new TimeAdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc);
        }
    }
    static class TimeAdviceAdapter extends AdviceAdapter{
        private String methodName;
        protected TimeAdviceAdapter(int api, MethodVisitor methodVisitor, int methodAccess, String methodName, String methodDesc){
            super(api, methodVisitor, methodAccess, methodName, methodDesc);
            this.methodName = methodName;
        }
        @Override
        protected void onMethodEnter() {
            //调用TimeHolder的start方法并初始化当前的方法名
            //在方法入口处植入
            if ("<init>".equals(methodName) || "<clinit>".equals(methodName)){
                return;
            }
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder","<init>","()V", false);
            mv.visitVarInsn(ALOAD,0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn(".");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESTATIC, "org/xunche/agent/TimeHolder", "start", "(Ljava/lang/String;)V", false);
        }
        @Override
        protected void onMethodExit(int i) {
            //调用TimeHolder的cost方法并合并当前的方法名，并打印cost方法的返回值
            //在方法退出前植入
            if ("<init>".equals(methodName) || "<clinit>".equals(methodName)){
                return;
            }
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn(".");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn(": ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "org/xunche/agent/TimeHolder", "cost", "(Ljava/lang/String;)J", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }
    public static void premain(String args, Instrumentation inst) {
        //使用Instrumentation#addTransformer注册转换器，自定义转换器TimeClassFileTransformer重写了transform方法.
        inst.addTransformer(new TimeClassFileTransformer());
    }
    /**
     * premain是通过agent在应用启动之前对字节码进行修改来实现想要的功能，实际上jdk提供了attach api，通过此api访问已启动的java进程
     * 并通过agentmain方法来拦截类加载
     */
}
