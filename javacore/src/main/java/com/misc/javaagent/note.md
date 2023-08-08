# 关于java agent
[refto](https://developer.aliyun.com/article/763389?spm=a2c6h.12873581.0.dArticle763389.4605322cEfG1ce)
## 简易DEMO

- 准备源码 HelloWorld HelloAgent ，并编译成class文件

- 准备文件 MANIFEST.MF 文件内容
```
Premain-Class: misc.javaagent.HelloAgent
```
注意后面留一空行

- class文件与MF文件归档目录：
\-- root
    META_INF
        MANIFEST.MF
    misc
        javaagent
            HelloWorld.class
            HelloAgent.class
直接对root文件夹进行压缩zip，改扩展名为.jar, 命令 `jar cvmf MANIFEST.MF agent.jar root/`也可以，未验证

- 食用方法：`java --javaagent:agent.jar=nihao misc.javaagent.HelloWorld`

打印内容：
```
Hello Agent with Instrument: nihao
hello world
```
## 进阶DEMO 使用ASM植入代码
在JVM加载类时，为类的每个方法加上统计方法调用耗时的代码

验证方案待定

## 实践：使用javaagent实现btrace的功能
agent对指定的方法进行字节码的修改，采集方法的入参和返回值。通过socket将请求和返回发送到



