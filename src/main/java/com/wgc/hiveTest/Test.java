package com.wgc.hiveTest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.sql.*;

/**
 * @author wanggc
 * @date 2019/09/10 星期二 16:10
 */
public class Test {
    public static void main(String[] args) {
        //定义beeline url地址
        String url = "jdbc:hive2://134.64.14.230:10000/eda;principal=hive/bigdata014230@MYCDH";

        try {
            //kerberors登陆
            System.out.println("-->Kerberos登陆...");

            System.setProperty("java.security.krb5.conf", "E:\\IdeaProjects\\hiveTest\\src\\main\\resources\\krb5.conf");
            Configuration conf = new Configuration();
            conf.set("hadoop.security.authentication", "Kerberos");
            UserGroupInformation.setConfiguration(conf);

            UserGroupInformation.loginUserFromKeytab("eda@MYCDH","E:\\IdeaProjects\\hiveTest\\src\\main\\resources\\eda.keytab");

            System.out.println("-->当前用户是：" + UserGroupInformation.getCurrentUser());
            System.out.println("-->登陆用户是：" + UserGroupInformation.getLoginUser());

            System.out.println("--Kerberos登陆完毕");

            System.out.println("-->寻找驱动...");
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            System.out.println("-->找到驱动");

            //连接
            System.out.println("-->开始连接...");
            Connection conn;
            conn = DriverManager.getConnection(url);
            System.out.println("-->连接成功");

            //执行sql
            Statement statement = conn.createStatement();
            String sql = "show databases";
            System.out.println("-->开始执行sql: " + sql);
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("-->执行结果是: ");
            while (resultSet.next()){
                System.out.println(resultSet.getString(1));
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
