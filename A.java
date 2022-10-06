package glimmer.zsh.rpc1;

import com.alibaba.fastjson.JSONObject;
import org.reflections.Reflections;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;



public class A {
    public static void main(String[] args){
        try{
            Reflections reflections = new Reflections("glimmer.zsh.rpc1");
            Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(GlimmerClass.class);
            Set<SpecificClass> set = new HashSet<>();
            for (Class c : classSet) {
                Object obj = c.newInstance();
                SpecificClass specificClass = new SpecificClass();
                specificClass.classname = c.getName();
                set.add(specificClass);
                Method[] methods = c.getMethods();
                for (Method m : methods) {
                    for(Annotation a:m.getAnnotations()){
                        if(a.annotationType() == GlimmerMethod.class) {
                            specificClass.methodNames.add(m.getName());
                        }
                    }
                }
            }
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket socket1 = serverSocket.accept();
            Socket socket2 = new Socket("127.0.0.1",9999);

            //Socket1读入
            InputStreamReader socin = new InputStreamReader(socket1.getInputStream());
            BufferedReader socbuf = new BufferedReader(socin);
            //Socket2写出
            PrintWriter socout = new PrintWriter(socket2.getOutputStream());

            System.out.println("Start to receive message");
            String str = socbuf.readLine();
            System.out.println("reply length:"+str.length());
            System.out.println("received json:"+str);
            JSONObject json = JSONObject.parseObject(str);
            String methodName = (String) json.get("methodName");
            List list = (List) json.get("args");
            int k =0;
            for(SpecificClass s:set){
                for(String str1:s.methodNames){
                    if(str1.equals(methodName)){
                        k = 1;
                        Object obj = Class.forName(s.classname).newInstance();
                        Method[] methods = Class.forName(s.classname).getMethods();
                        Method m = null;
                        for(Method method:methods){
                            if(method.getName().equals(methodName)){
                                m = method;
                                break;
                            }
                        }
                        int num = m.getParameterCount();
                        Class[] cls = m.getParameterTypes();
                        Object[] o = new Object[num];
                        int i;
                        for(i = 0;i < num;i++){
                            if(cls[i] == int.class){
                                o[i] = (int)list.get(i);
                            }else if(cls[i] == String.class){
                                o[i] = (String)list.get(i);
                            }
                        }
                        Class cls1 = m.getReturnType();
                        JSONObject json1 = new JSONObject();
                        json1.put("res",m.invoke(obj,o));
                        json1.put("success",true);
                        String str2 = json1.toJSONString();
                        socout.println(str2);
                        socout.flush();
                        System.out.println("Connect to the socket successfully");
                        System.out.println("Have send message:"+str2);
                        break;
                    }
                }
            }
            if(k == 0){
                JSONObject json2 = new JSONObject();
                json2.put("success",false);
                String str2 = json2.toJSONString();
                socout.println(str2);
                socout.flush();
                System.out.println("Connect to the socket successfully");
                System.out.println("Have send message:"+str2);
            }

            //关闭IO和Socket
            socbuf.close();
            socket1.close();
            socket2.close();
            serverSocket.close();
        }catch(Exception e){

        }
    }
}
@GlimmerClass
class demo{
    int a = 1;
    public demo(){
    }
    public demo(int a){
        this.a = a;

    }
    @GlimmerMethod
    public int add(int b){
        return a+b;
    }
    @GlimmerMethod
    public void shout(){

    }

}

class SpecificClass{
    String classname;
    Set<String> methodNames = new HashSet<>();

    @Override
    public String toString() {
        return "SpecificClass{" +
                "classname='" + classname + '\'' +
                ", methodNames=" + methodNames +
                '}';
    }
}
