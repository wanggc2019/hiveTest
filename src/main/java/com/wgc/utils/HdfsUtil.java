package  com.wgc.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Progressable;

import java.io.*;

/**
 * @author wanggc
 * @date 2019/08/29 星期四 15:34
 */
public class HdfsUtil {

    private static FileSystem fs;

    public HdfsUtil() {
        fs = getFileSystem();
    }

    /**
     * Configuration是配置对象，conf可以理解为包含了所有配置信息的一个集合，可以认为是Map,
     * 在初始化的时候底层会加载一堆配置文件 core-site.xml;hdfs-site.xml;mapred-site.xml;yarn-site.xml
     * 如果需要项目代码自动加载配置文件中的信息，那么就必须把配置文件改成-default.xml或者-site.xml的名称，
     * 而且必须放置在src下,如果不叫这个名，或者不在src下，也需要加载这些配置文件中的参数，必须使用conf对象提供的方法手动加载.
     * 依次加载的参数信息的顺序是：
     * 1.加载core/hdfs/mapred/yarn-default.xml
     * 2.加载通过conf.addResource()加载的配置文件
     * 3.加载conf.set(name,value)
     */
    public static Configuration getConfiguration() {
        Configuration conf = new Configuration();
        //    HA加Kerberos的配置
        //    1.HA模式的配置
        conf.set("fs.defaultFS", "hdfs://nameservice1");
        conf.set("dfs.nameservices", "nameservice1");
        conf.set("dfs.ha.namenodes.nameservice1", "nn1,nn2");
        conf.set("dfs.namenode.rpc-address.nameservice1.nn1", "bigdata014230:8020");
        conf.set("dfs.namenode.rpc-address.nameservice1.nn1", "bigdata014231:8020");
        conf.set("dfs.client.failover.proxy.provider.nameservice1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem"); //防止报错 no FileSystem for scheme: hdfs...

        //   2. 配置Kerberos
        System.setProperty("java.security.krb5.conf", "E:\\IdeaProjects\\hdfsTest\\src\\main\\resources\\krb5.conf");
        conf.set("dfs.datanode.kerberos.principal", "hdfs/_HOST@MYCDH");
        conf.set("dfs.namenode.kerberos.principal", "hdfs/_HOST@MYCDH");
        conf.set("hadoop.security.authentication", "Kerberos");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab("eda@MYCDH", "E:\\IdeaProjects\\hdfsTest\\src\\main\\resources\\eda.keytab");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conf;
    }

    /**
     * 获取文件系统
     * 本地文件系统为LocalFileSystem，URL形式:    file:///c:myProgram
     * HDFS文件系统为DistributedFileSystem，URL形式:    fs.defaultFS=hdfs://hadoop01:9000
     */
    public static FileSystem getFileSystem() {
        Configuration conf = getConfiguration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fs);
        return fs;
    }

    /**
     * 关闭FileSystem
     */
    public void closeFileSystem(){
        if (fs != null){
            try {
                fs.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        System.out.println("filesystem closed success.");
    }
}

