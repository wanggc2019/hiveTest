package com.wgc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class GenerateData3 {
    public static void main(String[] args) throws IOException {
        final Logger log = LoggerFactory.getLogger(GenerateData3.class);
        //生成的文件
        File logFile = new File("GenData3.txt");
        Random random = new Random();

        //String[] hosts = {"www.taobao.com"};
        String[] time = {"2018-03-05 06:25:22","2018-03-05 15:32:39","2018-03-05 22:23:33","2018-03-05 08:29:30","2018-03-05 22:25:33",
                "2018-03-05 08:36:15","2018-03-05 22:28:39","2018-03-05 12:54:33","2018-03-05 22:26:30","2018-03-05 10:28:33",
                "2018-03-05 13:45:22","2018-03-05 23:28:39","2018-03-05 11:55:33","2018-03-05 21:33:30","2018-03-05 08:26:33",
                "2018-03-05 22:26:22","2018-03-05 12:56:39","2018-03-05 15:33:33","2018-03-05 14:29:30","2018-03-05 17:28:53",
                "2018-03-05 16:33:22","2018-03-05 09:28:39","2018-03-05 14:28:33","2018-03-05 10:26:30","2018-03-05 16:34:33"
        }; //25个

        StringBuffer sbBuffer = new StringBuffer();
        //构造的记录数1400
        for (int i = 0 ; i<1400; i++){
            //sbBuffer.append(hosts[0]+"\t"+GetData.getRandomString(30)+"\t"+time[random.nextInt(25)]+"\n");
            sbBuffer.append(GenerateData3.getRandomString(30)+"\t"+time[random.nextInt(25)]+"\n");
        }//G9jBt3GpEPKwhjIe3Ie0O0hE34zfk2	2018-03-05 23:28:39

        //检查数据写入的文件存在，不在则创建
        if(! logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                log.error("Create logFile fail !");
            }
        }

        byte[] b = (sbBuffer.toString()).getBytes();

        FileOutputStream fs;

        try {
            fs = new FileOutputStream(logFile);
            fs.write(b);
            fs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getRandomString(int length){
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<length; i++){
            int number=random.nextInt(3);// [0,3)
            long result;
            switch(number){
                case 0:
                    /**
                     * ASCII码 ：
                     * 小写 a -- z : 97~ 122
                     * 大写 A -- Z ：65~ 90
                     * Math.random() ： [0.0,1.0)
                     * Math.random()*25 : [0.0,25.0)
                     * Math.random()*25+65 : [65.0,90.0)
                     * Math.random()*25+97 : [97,122)
                     * Math.round(Math.random()*25+65): 区间随机选取
                     * (char) result ： 将对应的ASCII强制转换为字符（a~z,A~Z）
                     */
                    result=Math.round(Math.random()*25+65); // a~z
                    sb.append((char) result);
                    break;
                case 1:
                    result=Math.round(Math.random()*25+97); // A~Z
                    sb.append((char) result);
                    break;
                case 2:
                    sb.append(new Random().nextInt(10)); // [0,10)
                    break;
            }
        }
        return sb.toString(); //指定的length长度的数字加大小写字母的伪随机数
    }
}
/*
* (char) (Math.random() * 26 + 'A')
* random()产生的随机数的范围是[0,1)，乘以26之后这个范围就扩大到0—
26；然后，你要搞清楚数字与字符之间的转换关系以及运算时的原理，小
写字母的ASCII码是65—90；现在分析一下这行代码具体是怎么运行的：
首先，Math.random()*26产生了一个0—26之间的数，这个就不解释了，
开始已经提到了，然后这个数要与字符A做运算，可是字符怎么能与数字运
算呢？我们知道，字符在计算机中是以ASCII码的形式存储的(其实也就是
数字)，所以，前边的运算，就是数字与数字的运算，表面上整型数字与字
符的运算；好了，现在运算关系搞清楚了，那么，前面产生的0—26的随机
数加上一个A(也就是加上a的ASCII码65)，就对应到26个字母中了，这个
可以这样理解，比如产生的随机数就是0，那么0加65还是65，就是字母A，
如果随机数是1，那加上65就变成66，对应的字母就是B，以此类推，后边
都是一样的；最后再将这个运算后的数字强制转换成字符就行了
原文链接：https://blog.csdn.net/ld513508088/article/details/8717892
* */

