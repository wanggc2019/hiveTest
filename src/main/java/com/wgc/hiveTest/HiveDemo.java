package com.wgc.hiveTest;

/**
 * @author wanggc
 * @date 2019/09/10 星期二 18:04
 */
public class HiveDemo {
    public static void main(String[] args) {
        HiveCli cli = HiveCli.getInstance();
        //查询hive所有的数据库
        //cli.showDatabases();
        //创建hive数据库
        //cli.createDatabase("wgcTest");
        //建hive表
        //cli.createTable();
        //查询指定hive库下的所有表
        //cli.showTables("eda");
        //查看表的结构
        //cli.descTable("eda.wgctest20190912");
        //查询表数据
        //cli.select();
        //查询统计
        cli.count();


        //关闭连接，释放资源
        cli.close();


    }
}
