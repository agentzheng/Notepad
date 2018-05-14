package elliott;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class test {

    public static void main(String[] args) {
        String cmd = "notepad";
        Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象
        try {
            run.exec(cmd);// 启动另一个进程来执行命令
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
