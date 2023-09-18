package com.asm;

import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.SpringAsmInfo;
import org.springframework.asm.Type;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Member;
import java.util.Map;

/**
 * 基于ASM的方法读取器，优于标准反射
 */
public class MyMethodVisitor extends MethodVisitor {

    private static final String CONSTRUCTOR = "<init>";

    private final Class<?> clazz;

    private final Map<Member, String[]> memberMap;

    private final String name;

    private final Type[] args;

    private final String[] parameterNames;

    private final boolean isStatic;

    private boolean hasLvtInfo = false;

    private final int[] lvtSlotIndex;

    public MyMethodVisitor(Class<?> clazz, Map<Member, String[]> map, String name, String desc, boolean isStatic) {
        super(SpringAsmInfo.ASM_VERSION);
        this.clazz = clazz;
        this.memberMap = map;
        this.name = name;
        this.args = Type.getArgumentTypes(desc);
        this.parameterNames = new String[this.args.length];
        this.isStatic = isStatic;
        this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
    }

    @Override
    public void visitLocalVariable(String name, String description, String signature, Label start, Label end, int index) {
        this.hasLvtInfo = true;
        for (int i = 0; i < this.lvtSlotIndex.length; i++) {
            if (this.lvtSlotIndex[i] == index) {
                this.parameterNames[i] = name;
            }
        }
    }

    @Override
    public void visitEnd() {
        if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0)) {
            this.memberMap.put(resolveMember(), this.parameterNames);
        }
    }

    private Member resolveMember() {
        ClassLoader loader = this.clazz.getClassLoader();
        Class<?>[] argTypes = new Class<?>[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            argTypes[i] = ClassUtils.resolveClassName(this.args[i].getClassName(), loader);
        }
        try {
            if (CONSTRUCTOR.equals(this.name)) {
                return this.clazz.getDeclaredConstructor(argTypes);
            }
            return this.clazz.getDeclaredMethod(this.name, argTypes);
        }catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
        int[] lvtIndex = new int[paramTypes.length];
        int nextIndex = (isStatic ? 0 : 1);
        for (int i = 0; i < paramTypes.length; i++) {
            lvtIndex[i] = nextIndex;
            if (isWideType(paramTypes[i])) {
                nextIndex += 2;
            }
            else {
                nextIndex++;
            }
        }
        return lvtIndex;
    }

    private static boolean isWideType(Type aType) {
        return (aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE);
    }
}