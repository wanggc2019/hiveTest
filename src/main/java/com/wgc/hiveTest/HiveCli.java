package com.wgc.hiveTest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

/*
* 通过 Java 获取 Hive 元数据信息有两种方式：
1、hive-metastore 包，通过 Hive Metastore Server 获取；
2、hive-jdbc 包，通过 Hive ThriftServer2 获取。
对比两种方式，如果仅获取 Hive 元数据信息，而不操作底层数据，建议使用第一种方式。

* 下面采用jdbc测试hive
* */

/**
 * @author wanggc
 * @date 2019/09/10 星期二 10:19
 */
public class HiveCli {

    //driver used for hiveserver2
    private static final Logger log = LoggerFactory.getLogger(HiveCli.class);
    final private static String url = "jdbc:hive2://134.64.14.230:10000/default;principal=hive/bigdata014230@MYCDH";
    final private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static Connection conn = null;
    private static ResultSet resultSet = null;
    private static Configuration conf = null;
    private static Statement statement = null;
    private static HiveCli hiveCli = null;

    /**
     * 构造函数
     */
    public HiveCli() {
        /**
         * kerberos验证
         */
        // 获取kdc信息
        System.setProperty("java.security.krb5.conf", "E:\\IdeaProjects\\hiveTest\\src\\main\\resources\\krb5.conf");

        conf = new Configuration();
        conf.set("hadoop.security.authentication", "Kerberos");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab("eda@MYCDH", "E:\\IdeaProjects\\hiveTest\\src\\main\\resources\\eda.keytab");
            // 显示当前的登陆用户
            log.info("UserGroupInformation.getLoginUser():"+UserGroupInformation.getLoginUser());
            log.info("-->kerberos登陆成功.");
        } catch (IOException e) {
            log.error(String.format("--kerberos登陆异常:%s",e.getMessage()));
        }

        /**
         * 获取连接
         */
        try {
            // 载入hiveserver2的JDBC驱动，hive1.2.0以上版本无需这样导入
            log.info("-->导入JDBC驱动");
            Class.forName(driverName);
            log.info("-->JDBC导入成功");
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            log.info("-->获取连接成功");
        } catch (ClassNotFoundException e) {
            log.error(String.format("导入JDBC异常:%s",e.getMessage()));
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 单例模式
     */

    public static HiveCli getInstance(){
        if (hiveCli == null){
            synchronized (HiveCli.class){
                if (hiveCli == null){
                    hiveCli = new HiveCli();
                }
            }
        }
        return hiveCli;
    }

    /**
     * 查询所有数据库
     */
    public void showDatabases(){
        try {
            String sql = "show databases";
            System.out.println("Running sql: " + sql);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println("result: " + resultSet.getString(1));
            }
        } catch (SQLException e){
            log.error(e.getMessage());
        }
    }

    /**
     * 创建hive数据库
     * @param databaseName 要创建的数据库名
     */
    public void createDatabase(String databaseName){
        try {
            String sql = "create database if not exists" + " " + databaseName;
            System.out.println("-->执行sql: " + sql);
            statement.execute(sql);
            System.out.println("-->hive数据库创建成功");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 创建指定的hive表
     */
    public void createTable(){
        try {
            String sql = "CREATE TABLE eda.wgctest20190912(foo INT, bar STRING)";
            System.out.println("-->执行的sql是：" + sql);
            statement.execute(sql);
            System.out.println("-->hive表创建成功");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 查询指定hive库下的所有表
     * @param databaseName 要查询的hive库名
     */
    public void showTables(String databaseName){
        try {
            String sql = "use " + databaseName;
            String sql2 = "show tables";
            System.out.println("-->执行的sql是：\n" + sql + "\n" + sql2);

            statement.execute(sql);
            resultSet = statement.executeQuery(sql2);
            System.out.println("-->"+databaseName + "库下的表是：");
            while (resultSet.next()){
                System.out.println(resultSet.getString(1));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 查看指定表的表结构
     * @param tableName
     */
    public void descTable(String tableName){
        try {
            String sql = "desc " + tableName;
            System.out.println("-->执行的sql是：" + sql);
            resultSet = statement.executeQuery(sql);
            System.out.println("-->"+ tableName + "表的结构是：");
            while (resultSet.next()){
                System.out.println(resultSet.getString(1) +
                        "\t" + resultSet.getString(2));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 查询表数据
     */
    public void select(){
        try {
            String sql = "select * from eda.pokes";
            System.out.println("-->执行查询的sql是：" + sql);
            resultSet = statement.executeQuery(sql);
            System.out.println("-->执行sql的结果是：\n" + "pokes.foo\t" + "pokes.bar\t");
            while (resultSet.next()){
                System.out.println(resultSet.getString("foo")+ "\t" + resultSet.getString("bar"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * 查询统计
     * 会运行mapreduce作业
     */
    public void count(){
        try {
            String sql = "select count(*) from eda.pokes";
            System.out.println("-->执行查询统计的sql是：" + sql);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                System.out.println("-->执行sql的结果是：" + resultSet.getInt(1));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void close(){
        if (resultSet != null){
            try {
                resultSet.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        if (statement != null){
            try {
                statement.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }




}
