package com.mytest.mybatis.test;

import com.mytest.mybatis.enums.SexEnum;
import com.mytest.mybatis.enums.TestUserTypeEnum;
import com.mytest.mybatis.mapper.RoleMapper;
import com.mytest.mybatis.mapper.TestBlobMapper;
import com.mytest.mybatis.mapper.TestUserMapper;
import com.mytest.mybatis.model.Role;
import com.mytest.mybatis.model.TestBlob;
import com.mytest.mybatis.model.TestUser;
import com.mybatis.utils.FileUtils;
import com.mybatis.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Chapter04Test {
    //构建SqlSessionFactory
    private static SqlSessionFactory buildSqlSessionFactory(){
        SqlSessionFactory sessFac = null;
        String configFile = "mybatis-config.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(configFile);
            SqlSessionFactoryBuilder sessFacBuilder = new SqlSessionFactoryBuilder();
            sessFac = sessFacBuilder.build(inputStream);
            //与mybatis-config.xml中的mapper结点功能相同
            //Configuration configuration = sessFac.getConfiguration();
            //configuration.addMapper(RoleMapper.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return sessFac;
    }
    //标准的SqlSession的使用
    public static void standardSqlSessionUsage(){
        SqlSessionFactory sessionFactory = buildSqlSessionFactory();
        SqlSession sqlSession = null;
        try {
            //sqlSession采用门面模式，实际执行SQL发送的是底层的Executor
            sqlSession = sessionFactory.openSession();
            //发送SQL方式1 - 使用SqlSession
            Role role = sqlSession.selectOne("com.mytest.mybatis.mybatis.RoleMapper.queryRole",1L);
            //发送SQL方式2 - 使用Mapper接口(主流)
                //MapperProxyFactory里表明这是JDK动态代理创建了接口的代理
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            Role role2  = roleMapper.queryRoleWithAnno(1L);
            //do something
            sqlSession.commit();
        }catch (Exception ex){
            sqlSession.rollback();
        }finally {
            if (sqlSession != null){
                sqlSession.close();
            }
        }
    }

    public static <T> T getMapper(Class<T> clazz){
        SqlSessionFactory sessFact = SqlSessionFactoryUtils.getSqlSessionFactory("mybatis-config.xml");
        SqlSession sqlSession = sessFact.openSession();
        T t = sqlSession.getMapper(clazz);
        return t;
    }

    public static void test(){
        SqlSessionFactory sessFact = SqlSessionFactoryUtils.getSqlSessionFactory("mybatis-config.xml");
        SqlSession sqlSession = sessFact.openSession();
        RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);

        Role newRole = new Role.Builder().buildId(7L).buildRoleName("musician").buildNote("音乐家").build();
        System.out.println(roleMapper.updateRole(newRole));

        //sqlSession不commit，数据库插入操作就不会生效，但id会增加
        sqlSession.commit();
        sqlSession.close();
    }

    public static void testGetUserByName(){
        TestUserMapper testUserMapper = getMapper(TestUserMapper.class);
        TestUser testUser = testUserMapper.getTestUserByName("lucy");
        System.out.println(testUser);
    }
    public static void testGetTypeByName(){
        TestUserMapper mapper = getMapper(TestUserMapper.class);
        String typeEnum = mapper.getUserTypeByName("jack");
        System.out.println(typeEnum);
    }
    public static void testGetById(){
        TestUserMapper mapper = getMapper(TestUserMapper.class);
        TestUser testUser = mapper.getTestUserById(12);
        System.out.println(testUser);
    }
    public static void testInsertTestUser(){
        TestUser testUser = new TestUser();
        testUser.setName("terminator");
        testUser.setType(TestUserTypeEnum.MIDDLE);
        //使用TestUserSexEnum.valueOf("FEMALE")会报Data truncated for column 'sex' at row 1异常
        testUser.setSex(SexEnum.TRANSMALE);
        testUser.setAge(999);
        SqlSessionFactory sessFact = SqlSessionFactoryUtils.getSqlSessionFactory("mybatis-config.xml");
        SqlSession sqlSession = sessFact.openSession();
        TestUserMapper mapper = sqlSession.getMapper(TestUserMapper.class);
        int result = mapper.insertTestUser(testUser);
        System.out.println(result);
        sqlSession.commit();
        sqlSession.close();
    }
    public static void testEnum(){
        SexEnum sexEnum1 = SexEnum.valueOf("FEMALE");
        SexEnum sexEnum2 = SexEnum.FEMALE;
        System.out.println(sexEnum1 == sexEnum2);
    }

    public static void testInsertBlobTable(){
        SqlSessionFactory sessFact = SqlSessionFactoryUtils.getSqlSessionFactory("mybatis-config.xml");
        SqlSession sqlSession = sessFact.openSession();
        TestBlobMapper mapper = sqlSession.getMapper(TestBlobMapper.class);

        String filePath = "mybatis/src/main/resources/mybatis/TestUserMapper.xml";
        byte[] content = FileUtils.readFile(filePath);
        TestBlob testBlob = new TestBlob();
        testBlob.setContent("content");
        int result = mapper.insertBlobFile(testBlob);
        System.out.println(result);

        sqlSession.commit();
        sqlSession.close();

        //TestBlob testBlob = mybatis.getBlobFile(1);
        //System.out.println(testBlob);
    }
    public static void testReadTestBlob(){
        TestBlobMapper mapper = getMapper(TestBlobMapper.class);
        String content = mapper.getBlobFile(4).getContent();
        System.out.println(content.substring(0,20));
    }

    public static void testObjectFactory(){
        RoleMapper mapper = getMapper(RoleMapper.class);
        Role role = mapper.queryWithResultMap(4L);
        System.out.println(role.toString());

    }

    public static void main(String[] args) {
        //mysql的枚举类型如何映射到java enum defaultEnumTypeHandler callSettersOnNulls logprefix
        //testInsertTestUser();
        //testGetById();
        //testEnum();
        //testGetById();
        testReadTestBlob();
        //testObjectFactory();

    }
}
