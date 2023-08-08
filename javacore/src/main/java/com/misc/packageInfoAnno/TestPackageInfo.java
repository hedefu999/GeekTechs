package com.misc.packageInfoAnno;

import java.lang.annotation.Annotation;

public class TestPackageInfo {
    private static final String PACKAGE_NAME = "misc.packageInfoAnno";
    public static void main(String[] args) {
        getAllAnnos(PACKAGE_NAME);

    }
    //注解的全解析
    private static void getAllAnnos(String packageName){
        Package pack = Package.getPackage(packageName);
        Annotation[] annotations = pack.getDeclaredAnnotations();
        for (Annotation anno : annotations){
            if (anno.annotationType().equals(PkgAnno.class)){
                System.out.println(((PkgAnno) anno).value());
            }
            System.out.println(anno.toString());
            //@misc.packageInfoAnno.PkgAnno(value=包注解)
        }
    }
    //获取指定类型的注解
    private static void getAnno(String packgeName){
        Package pack = Package.getPackage(packgeName);
        PkgAnno anno = pack.getAnnotation(PkgAnno.class);
        System.out.println(anno.value());
    }
}
