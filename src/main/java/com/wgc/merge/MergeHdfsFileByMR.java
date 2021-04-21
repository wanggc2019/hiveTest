package com.wgc.merge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MergeHdfsFileByMR {

    /**
     * 程序功能：合并HDFS文件
     *
     * 注意事项：本程序只适合合并文本文件、gz压缩文件以及所有可直接拼接的文件
     * 		   可直接拼接文件：在Linux中表现为  cat file1 >> file2 ,表示将file1合并到file2中，
     *       所有可以这样操作的文件，都适用与本程序
     *
     * args： inputPath outPath reducePath  fileSize
     * inputPath : 待合并文件路径
     * outPath : 合并后文件输出路径（目录需提前新建）
     * reducePath : reduce 生成路径（没什么用，但必须要有，并且不能为已有目录，程序执行完后建议删除）
     * fileSize : 合并后的文件大小，支持K、M、G、无标志，无标识默认为Byte，建议为128M
     *
     * 例子：
     * hadoop jar /home/shjs_wlpt/test/wwl/script/dpi/merge/merge_test.jar com.ustcinfo.hadoop.MergeHdfsFileByMR \
     * /user/shjs_wlpt/dpi/bak_4g/20180105/12 \
     * /user/shjs_wlpt/data/test111/merge/out \
     * /user/shjs_wlpt/data/test111/merge/re \
     * 128M
     *
     * 在程序执行中，可以通过查看outPath/list_file.log 来知道文件被合并流向，
     * 同一行的文件都被合并到第一个文件中，程序执行成功后该文件将会被删除
     *
     */

    public static void main(String[] args) throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException {
        final Logger log = LoggerFactory.getLogger(MergeHdfsFileByMR.class);

        final String inputPath = args[0];
        final String outPath = args[1];
        final String reducePath = args[2];
        final String fileSize = args[3].toLowerCase(); //128M -> 128m 方便下面判断

/*        final String inputPath = "/user/hive/warehouse/eda.db/table_89";
        final String outPath = "/tmp/merge/out";
        final String reducePath = "/tmp/merge/reduce";
        final String fileSize = "128M".toLowerCase();*/

        long mergeSize;
        //转换文件单位从MB --> Byte
        if (fileSize.endsWith("k")) {
            mergeSize = Long.parseLong(fileSize.replace("k", "")) * 1024; //全部转换成字节单位
        } else if (fileSize.endsWith("m")){
            mergeSize = Long.parseLong(fileSize.replace("m", "")) * 1024 * 1024;
        } else if (fileSize.endsWith("g")) {
            mergeSize = Long.parseLong(fileSize.replace("g", "")) * 1024 * 1024 * 1024;
        }else {
            mergeSize = Long.parseLong(fileSize);
        }
        //计算每个map工作量，按每个map处理1G数据，来估算
        //目的在于动态提高工作效率
        float re = 1374389534f / (float) mergeSize;
        int linePerMap = (int) Math.ceil(re); //向上取整， 即小数部分直接舍去，并向正数部分进1

/*        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader urlclassLoader = (URLClassLoader) classLoader;
        URL[] urls = urlclassLoader.getURLs();
        for (URL url: urls) {
            System.out.println(url);
        }*/

        Configuration con = new Configuration();
        //设置自定义变量
        //合并结果输出路径
        con.set("merge_out_path", outPath); // /user/shjs_wlpt/data/test111/merge/out
        //作业提交资源队列
        con.set("mapreduce.job.queuename", "eda");
        //设置每个map接收行数
        con.setInt("mapreduce.input.lineinputformat.linespermap", linePerMap);
/*        //kerberos
        System.setProperty("java.security.krb5.conf", "E:\\IdeaProjects\\hiveTest\\src\\main\\resources\\krb5.conf");
        con.set("hadoop.security.authentication", "Kerberos");
        UserGroupInformation.setConfiguration(con);
        try {
            UserGroupInformation.loginUserFromKeytab("eda@MYCDH", "E:\\IdeaProjects\\hiveTest\\src\\main\\resources\\eda.keytab");
            // 显示当前的登陆用户
            log.info("UserGroupInformation.getLoginUser():"+UserGroupInformation.getLoginUser());
            log.info("-->kerberos登陆成功.");
        } catch (IOException e) {
            log.error(String.format("--kerberos登陆异常:%s",e.getMessage()));
        }*/

        FileSystem fs = null;
        //fs = FileSystem.get(URI.create("hdfs://nameservice1/"),con);
        fs = FileSystem.get(URI.create("hdfs://134.64.14.230/"),con);
        FileStatus[] fileStatus = fs.listStatus(new Path(inputPath)); // /user/shjs_wlpt/dpi/bak_4g/20180105/12
        //生成中间文件，存储需要合并的文件路径，将同一行的多个文件合并成一个文件
        OutputStream fso =  fs.create(new Path(outPath + "/list_file.log")); // /user/shjs_wlpt/data/test111/merge/out/list_file.log
        long currentFileSize = 0L;
        //按照文件大小计算需要合并的文件
        for (int i = 0;i < fileStatus.length; i++){
            // 待合并文件大小 Byte
            currentFileSize += fileStatus[i].getLen();
            fso.write(fileStatus[i].getPath().toString().getBytes());
            if (i == (fileStatus.length -1)){
                continue; //到最后一个 跳出循环到下一步
            }
            if (currentFileSize >= mergeSize){
                fso.write("\n".getBytes());
                currentFileSize = 0L;
            } else {
                fso.write("|".getBytes());
            }
        }
        fso.flush();
        fso.close();

        //定义合并程序
        //获取job对象
        Job job = Job.getInstance(con, "merge_file_mapreduce");
        job.setJarByClass(MergeHdfsFileByMR.class);
        //指定inputsplit具体实现类，如果要每n行作为一个单元，传入map处理。就要使用NLineInputFormat了。
        job.setInputFormatClass(NLineInputFormat.class);
        // 设置map类和map处理的 key  value 对应的数据类型
        job.setMapperClass(FileMap.class);
        //设置reduce 这里reduce设置为0
        job.setNumReduceTasks(0);
        //设置最终输出端的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //设置输入输出路径
        FileInputFormat.setInputPaths(job, new Path(outPath + "/list_file.log"));
        FileOutputFormat.setOutputPath(job, new Path(reducePath));
        //提交job
        job.waitForCompletion(true);

        //删除中间文件
        fs.delete(new Path(outPath + "/list_file.log"), false);
        fs.close();
    }
    /**
     * 自定义map程序，将多个文件合并为一个
     * @author wen
     *
     */
    public static class FileMap extends Mapper<LongWritable, Text, NullWritable, NullWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException {
            Configuration con = context.getConfiguration();
            String merge_output_path = con.get("merge_out_path");
            FileSystem fs = null;
            fs = FileSystem.get(con);
            String[] strs = value.toString().split("\\|");
            if (strs[0] != null || strs[0] != ""){
                String fileName = new Path(strs[0]).getName();
                //File.separator路径分隔符
                OutputStream os = fs.create(new Path(merge_output_path + File.separator + "merge-" +fileName));
                for (String st : strs){
                    InputStream is = fs.open(new Path(st));
                    IOUtils.copyBytes(is, os, con, false);
                    is.close();
                }
                os.flush();
                os.close();
            }
        }
    }
}
