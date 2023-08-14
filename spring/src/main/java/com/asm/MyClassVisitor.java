package com.asm;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.SpringAsmInfo;

import java.lang.reflect.Member;
import java.util.Map;

public class MyClassVisitor extends ClassVisitor {
    //class二进制文件中的构造器函数标识
    private static final String STATIC_CLASS_INIT = "<clinit>";

    private final Class<?> clazz;

    private final Map<Member, String[]> memberMap;

    public MyClassVisitor(Class<?> clazz, Map<Member, String[]> memberMap) {
        super(SpringAsmInfo.ASM_VERSION);
        this.clazz = clazz;
        this.memberMap = memberMap;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!isSyntheticOrBridged(access) && !STATIC_CLASS_INIT.equals(name)) {
            return new MyMethodVisitor(this.clazz, this.memberMap, name, desc, isStatic(access));
        }
        return null;
    }
    //判断是否是语义或桥接方法，这种方法要忽略
    private static boolean isSyntheticOrBridged(int access) {
        return (((access & Opcodes.ACC_SYNTHETIC) | (access & Opcodes.ACC_BRIDGE)) > 0);
    }

    private static boolean isStatic(int access) {
        return ((access & Opcodes.ACC_STATIC) > 0);
    }
}