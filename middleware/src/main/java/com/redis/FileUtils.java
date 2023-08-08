package com.redis;

import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class FileUtils {
    //仿照commons.io写的readFileToString
    public static String readClassPathFileToString(String classpathFile){
        ClassPathResource resource = new ClassPathResource(classpathFile);
        String fileStr = null;
        try (InputStream is = resource.getInputStream()){
            try (StringBuilderWriter builderWriter = new StringBuilderWriter()){
                InputStreamReader streamReader = new InputStreamReader(is, Charset.forName("UTF-8"));
                int count = 0,EOF = -1;
                char[] DEFAULT_BUFFER = new char[4*1024];
                while (EOF != (count = streamReader.read(DEFAULT_BUFFER))){
                    builderWriter.write(DEFAULT_BUFFER,0,count);
                }
                fileStr = builderWriter.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return fileStr;
    }
}
