# cxDynamicProxy
纯手写轻量级JDK动态代理
纯手写JDK动态代理

JDK动态代理原理分析
 
1.	在使用jdk动态代理的时候，必须要实现InvocationHandler接口，invoke方法
Invoke 方法中该三个参数分别表示为: 代理对象、被代理执行的方法、参数


 
2.	使用jdk动态代理获取代理类对象（JDK自动生成代理类） $Proxy0.class
 

纯手写动态代理原理分析
1.	创建代理类$Proxy0源代码文件实现被代理的接口。
public final class $Proxy0 extends java.lang.reflect.Proxy implements com.cx.service.OrderService {

2.	 使用JavaCompiler技术编译该$Proxy0文件获取到$Proxy0.class
3.	 使用ClassLoader将该$Proxy0.class加入到当前JVM内存中


ClassLoader 顾名思义就是类加载器,ClassLoader 作用：
负责将 Class 加载到 JVM 中
审查每个类由谁加载（父优先的等级加载机制）
将 Class 字节码重新解析成 JVM 统一要求的对象格式

Jdk动态代理的简单实现

public interface MyExtInvocationHandler {
    /**
     * 纯手写模拟 jdk动态模拟 InvocationHandler接口
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException;
}
public class ExtJdkInvocationHandler implements MyExtInvocationHandler {
    /**
     * 目标执行对象 (被代理的对象)
     */
    private Object target;

    public ExtJdkInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        System.out.println("纯手写JDK动态代理日志拦截开始>>>>>");
        Object result = method.invoke(target, args);// 执行代理对象的方法
        System.out.println("纯手写JDK动态代理日志拦截结束>>>>>");
        return result;
    }

    //    /**
//     * 获取代理对象
//     *
//     * @param <T>
//     * @return
//     */
    public <T> T getProxy() {
        return (T) new $Proxy0(this);
    }
}

public class $Proxy0 implements OrderService {
    private MyExtInvocationHandler h;

    /**
     * 使用构造函数传递myExtInvocationHandler
     *
     * @param myExtInvocationHandler
     */
    public $Proxy0(MyExtInvocationHandler myExtInvocationHandler) {
        this.h = myExtInvocationHandler;
    }

    @Override
    public void order() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //传递被代理对象的方法
        Method orderMethod = OrderService.class.getMethod("order", new Class[]{});
        this.h.invoke(this, orderMethod, null);
    }
}

ExtJdkInvocationHandler extJdkInvocationHandler = new ExtJdkInvocationHandler(new OrderServiceImpl());
OrderService proxy = extJdkInvocationHandler.getProxy();
proxy.order();



完全逼真模拟Jdk动态代理
 
public interface MyExtInvocationHandler {
    /**
     * 纯手写模拟 jdk动态模拟 InvocationHandler接口
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException;
}

public class JavaClassLoader extends ClassLoader {

    private File classPathFile;

    public JavaClassLoader(){
//        String classPath=JavaClassLoader.class.getResource("").getPath();
        String classPath="D:\\code";
        this.classPathFile=new File(classPath);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        String className= JavaClassLoader.class.getPackage().getName()+"."+name;
        if(classPathFile!=null){
          File classFile=new File(classPathFile,name.replaceAll("\\.","/")+".class");
          if(classFile.exists()){
              FileInputStream in=null;
              ByteArrayOutputStream out=null;
              try {
                  in=new FileInputStream(classFile);
                  out=new ByteArrayOutputStream();
                  byte[] buff=new byte[1024];
                  int len;
                  while ((len=in.read(buff))!=-1){
                     out.write(buff,0,len);
                  }
                  return defineClass(className,out.toByteArray(),0,out.size());
              }catch (Exception e){
                  e.printStackTrace();
              }finally {
                  if(in!=null){
                      try {
                          in.close();
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
                  if(out!=null){
                      try {
                          out.close();
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
              }
          }
        }
        return null;
    }
}

public class ExtJdkInvocationHandler implements MyExtInvocationHandler {
    /**
     * 目标执行对象 (被代理的对象)
     */
    private Object target;

    public ExtJdkInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        System.out.println("纯手写JDK动态代理日志拦截开始>>>>>");
        Object result = method.invoke(target, args);// 执行代理对象的方法
        System.out.println("纯手写JDK动态代理日志拦截结束>>>>>");
        return result;
    }

    //    /**
//     * 获取代理对象
//     *
//     * @param <T>
//     * @return
//     */
    public <T> T getProxy() {
        return (T) MyProxy.newProxyInstance(new JavaClassLoader(), OrderService.class, this);
    }
}

public class MyProxy {
    static String rt = "\r\t";

    /**
     * @param classInfo 被代理实现的接口信息 <br>
     * @param h
     * @return
     */
    public static Object newProxyInstance(JavaClassLoader javaClassLoader, Class classInfo, MyExtInvocationHandler h) {
        try {
            // 1.创建代理类java源码文件,写入到硬盘中..
            Method[] methods = classInfo.getMethods();
            String proxyClass = "package com.cx.ext.jdk.proxy;" + rt
                    + "import java.lang.reflect.Method;" + rt
                    + "import com.cx.ext.jdk.proxy.MyExtInvocationHandler;" + rt
                    + "public class $Proxy0 implements " + classInfo.getName() + "{" + rt
                    + "MyExtInvocationHandler h;" + rt
                    + "public $Proxy0(MyExtInvocationHandler h)" + "{" + rt
                    + "this.h= h;" + rt + "}"
                    + getMethodString(methods, classInfo) + rt + "}";
            // 2. 将代理类源码文件写入硬盘中
            String filename = "d:/code/$Proxy0.java";
            File f = new File(filename);
            FileWriter fw = new FileWriter(f);
            fw.write(proxyClass);
            fw.flush();
            fw.close();

            // 3.使用JavaJavaCompiler 编译该$Proxy0源代码 获取class文件
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
            Iterable units = fileMgr.getJavaFileObjects(filename);
            JavaCompiler.CompilationTask t = compiler.getTask(null, fileMgr, null, null, null, units);
            t.call();
            fileMgr.close();

            //4.使用classClassLoader 将$Proxy0.class读取到内存中...
            Class proxy0Class = javaClassLoader.findClass("$Proxy0");
            //5.使用java反射机制给函数中赋值
            Constructor m = proxy0Class.getConstructor(MyExtInvocationHandler.class);
            Object o = m.newInstance(h);
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
        return null;
    }


    public static String getMethodString(Method[] methods, Class intf) {
        String proxyMe = "";
        for (Method method : methods) {
            proxyMe += "public void " + method.getName() + "() throws Throwable {" + rt
                    + "Method md= " + intf.getName() + ".class.getMethod(\"" + method.getName()
                    + "\",new Class[]{});" + rt
                    + "this.h.invoke(this,md,null);" + rt + "}" + rt;

        }
        return proxyMe;
    }
}



csdn地址：https://blog.csdn.net/qq_28056571/article/details/90452643

