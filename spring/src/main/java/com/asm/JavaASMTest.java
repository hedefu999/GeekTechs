package com.asm;

import com.google.gson.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.asm.*;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaASMTest {
    private static Class<?> targetClass = null;
    private static Method method = null;
    @Before
    public void before(){
        try {
            targetClass = Class.forName("com.asm.Student");
            Method[] declaredMethods = targetClass.getDeclaredMethods();
            List<Method> methods = Stream.of(declaredMethods).filter(method -> method.getName().equalsIgnoreCase("repeatContent")).collect(Collectors.toList());
            method = methods.get(0);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test(){
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        System.out.println(Arrays.toString(parameterNames));
    }
    @Test
    public void getParamStr(){
        Student student = new Student();
        student.setName("jack");
        student.setAge(10);
        Teacher teacher = new Teacher("lucy",22,new Date());
        System.out.println(new Gson().toJson(teacher));

    }
    @Test
    public void testMethodExecutionWithOnlyString(){
        String methodReference = "com.asm.Student#repeatContent";
        String paramStr = "{\"name\":\"lucy\",\"number\":22,\"birth\":\"Dec 15, 2019 3:16:03 PM\"}";
        //Teacher teacher = new Teacher("lucy",22,new Date());
        //Map<String,Object> data = new HashMap<>();
        String methodReference1 = "com.asm.Student#sayHello2Teacher";
        String paramStr1 = "{\"teacher\":{\"name\":\"lucy\",\"age\":22,\"dutyTime\":\"Dec 15, 2019 3:54:22 PM\"},\"name\":\"jack\"}";
        testFakeASMMethod(methodReference1,paramStr1);

    }

    private Object testFakeASMMethod(String methodReference, String paramStr) {
        JsonObject gsonObject = new JsonParser().parse(paramStr).getAsJsonObject();
        String[] split = methodReference.split("#");
        String className = split[0];
        String methodName = split[1];
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
        }
        Map<Member, String[]> methodParamsMap = getClassAllMethodParamNames(clazz);
        String[] classMethodParamNames = null;
        for (Map.Entry<Member,String[]> entry:methodParamsMap.entrySet()){
            Member member = entry.getKey();
            String[] paramNames = entry.getValue();
            if (member.getName().equalsIgnoreCase(methodName)){
                classMethodParamNames = paramNames;
                break;
            }
        }
        System.out.println(Arrays.toString(classMethodParamNames));

        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method targetMethod = null;
        for (Method method : declaredMethods){
            if (method.getName().equalsIgnoreCase(methodName)){
                targetMethod = method;
                break;
            }
        }
        Class<?>[] parameterTypes = targetMethod.getParameterTypes();
        int length = classMethodParamNames.length;
        if (parameterTypes.length != classMethodParamNames.length){
            System.out.println("无法执行");
        }
        Object[] realParams = new Object[length];
        Gson gson = new Gson();
        for (int i = 0; i < length; i++) {
            Class<?> paramType = parameterTypes[i];
            String paramName = classMethodParamNames[i];
            JsonElement jsonElement = gsonObject.get(paramName);
            String paramstr = jsonElement.toString();
            System.out.println("paramstr = "+paramstr);
            System.out.println("paramType = "+paramType.getName());
            realParams[i] = gson.fromJson(paramstr,paramType);//fastjson做不到这一点
        }
        try {
            System.out.println("开始执行");
            Stream.of(realParams).forEach(param -> System.out.println(param.getClass()+":-:"+param.toString()));
            targetMethod.setAccessible(true);
            Object newInstance = clazz.newInstance();
            return targetMethod.invoke(newInstance, realParams);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<Member, String[]> getClassAllMethodParamNames(Class<?> clazz){
        InputStream is = clazz.getResourceAsStream(ClassUtils.getClassFileName(clazz));
        if (is == null) {
            return null;
        }
        try {
            Map<Member, String[]> map = new ConcurrentHashMap<>(32);
            MyClassVisitor myClassVisitor = new MyClassVisitor(clazz, map);
            ClassReader classReader = new ClassReader(is);
            classReader.accept(myClassVisitor, 0);
            return map;
        }catch (Exception ex) {
        }finally {
            try {is.close();
            }catch (IOException ex) {}
        }
        return null;
    }
}
