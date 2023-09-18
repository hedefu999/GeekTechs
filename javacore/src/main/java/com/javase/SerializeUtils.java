package com.javase;

import java.io.*;

public class SerializeUtils {
    public static void serializeObject(Object object,String filepath){
        try{
            File file = new File(filepath);
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static <T> T deserializeObject(String filepath,Class<T> clazz){
        T object = null;
        try {
            File file = new File(filepath);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            object = (T) ois.readObject();
            ois.close();
            fis.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return object;
    }
}
