package glimmer.zsh.rpc1;

import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class B {
    public static void main(String[] args) {

        try {


            Socket socket1 = new Socket("127.0.0.1",8888);

            ServerSocket serverSocket = new ServerSocket(9999);
            Socket socket2 = serverSocket.accept();

            //Socket2读入
            InputStreamReader socin = new InputStreamReader(socket2.getInputStream());
            BufferedReader socbuf = new BufferedReader(socin);
            //Socket1写出
            PrintWriter socout = new PrintWriter(socket1.getOutputStream());

            System.out.println("Connection to the socket successfully");
            JSONObject json = new JSONObject();
            List list = new ArrayList<>();
            list.add(1);
            json.put("args", list);
            json.put("methodName","add");
            String str = json.toJSONString();
            socout.println(str);
            socout.flush();
            System.out.println("Have send message:"+str);
            System.out.println("Start to receive message");
            String str1 = socbuf.readLine();
            System.out.println("reply length:"+str1.length());
            System.out.println("received json:"+str1);
            JSONObject json1 = JSONObject.parseObject(str1);
            if((boolean)json1.get("success")){
                System.out.println("The result:"+json1.get("res"));
            }else{
                System.out.println("The result: 查询失败");
            }


            //关闭IO和Socket
            socbuf.close();
            socket1.close();
            serverSocket.close();
            socket2.close();
        } catch (Exception e) {

        }
    }
}
