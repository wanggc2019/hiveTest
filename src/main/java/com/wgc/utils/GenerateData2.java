package com.wgc.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GenerateData2 {
    public static void main(String[] args) throws IOException {
        //生成数据的文件
        File logFile = new File("GenData2.txt");
        Random random = new Random();
        DecimalFormat format = new DecimalFormat("#.0");

        int max=20000;
        int min=5000;

        int sale_max = 10000;
        int sale_min = 1;

        String[] URL = {"\"www.taobao.com\""};
        String[] line = {"\"-\""};
        String[] method = {"\"GET /enrol/users.php?id=16 HTTP/1.0\""
                ,"\"GET /course/view.php?id=11&section=16 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=3213&section=62 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=3214&section=62 HTTP/1.1\""
                ,"\"GET /course/view.php?id=12&section=16 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=3073&section=56 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=3216&section=62 HTTP/1.1\""
                ,"\"GET /course/view.php?id=47 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=30&section=56 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=32&section=62 HTTP/1.1\""
                ,"\"GET /course/view.php?id=16&section=16 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=33&section=62 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=34&section=62 HTTP/1.1\""
                ,"\"GET /course/view.php?id=18&section=16 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=373&section=56 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=316&section=62 HTTP/1.1\""
                ,"\"GET /course/view.php?id=147 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=3&section=56 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=2&section=62 HTTP/1.1\""
                ,"\"GET /course/view.php?id=316 HTTP/1.1\""
                ,"\"GET /mod/page/view.php?id=22&section=62 HTTP/1.1\""};
        String[] state_code = {"\"303\"","\"200\"","\"505\"","\"404\"","\"400\""} ;
        StringBuffer sbBuffer = new StringBuffer();
        for (int i = 0 ; i<14; i++){
            Date randomDate = randomDate("2018-06-26", "2018-06-27");
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(randomDate));

            String randomIp = getRandomIp();
            int s = random.nextInt(max)%(max-min+1) + min;
            int search_keyword = random.nextInt(100)+1;
            int click_category_id = random.nextInt(100)+1;
            int click_product_id = random.nextInt(100)+1;
            int order_category_ids = random.nextInt(100)+1;
            int order_product_ids = random.nextInt(100)+1;
            int pay_category_ids = random.nextInt(100)+1;
            int pay_product_ids = random.nextInt(100)+1;
            int city_id  = random.nextInt(30)+1;
            int page_id = random.nextInt(30)+1;
            String sale_amt = format.format(random.nextInt(sale_max)*random.nextFloat()+sale_min);
            int ad_id = random.nextInt(5);

            sbBuffer.append(URL[0] + "\t"
                    +"\""+randomIp+"\""+"\t"
                    +"\""+getRandomString(30)+"\""+"\t"
                    +line[0]+"\t"
                    +"\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(randomDate)+"\""+"\t"
                    +method[random.nextInt(21)]+"\t"
                    +state_code[random.nextInt(5)]+"\t"
                    +"\""+city_id+"\""+"\t"
                    +"\""+sale_amt+"\""+"\t"
                    +"\n");
        }

        if(! logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Create logFile fail !");
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

    /**
     * 获取随机日期
     *  Mon Feb 19 04:36:05 CST 2007
     *  14fb541a7a3f41389cbe2e4f12281ea4
     *
     * @param beginDate
     *            起始日期，格式为：yyyy-MM-dd
     * @param endDate
     *            结束日期，格式为：yyyy-MM-dd
     * @return
     */

    private static Date randomDate(String beginDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);// 构造开始日期
            Date end = format.parse(endDate);// 构造结束日期
            // getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());

            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }


    public static String getRandomString(int length){
        //产生随机数
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        //循环length次
        for(int i=0; i<length; i++){
            //产生0-2个随机数，既与a-z，A-Z，0-9三种可能
            int number=random.nextInt(3);
            long result=0;
            switch(number){
                //如果number产生的是数字0；
                case 0:
                    //产生A-Z的ASCII码
                    result=Math.round(Math.random()*25+65);
                    //将ASCII码转换成字符
                    sb.append(String.valueOf((char)result));
                    break;
                case 1:
                    //产生a-z的ASCII码
                    result=Math.round(Math.random()*25+97);
                    sb.append(String.valueOf((char)result));
                    break;
                case 2:
                    //产生0-9的数字
                    sb.append(String.valueOf
                            (new Random().nextInt(10)));
                    break;
            }
        }
        return sb.toString();
    }


    //IP
    public static String getRandomIp(){
        //ip范围
        int[][] range = {{607649792,608174079},//36.56.0.0-36.63.255.255
                {1038614528,1039007743},//61.232.0.0-61.237.255.255
                {1783627776,1784676351},//106.80.0.0-106.95.255.255
                {2035023872,2035154943},//121.76.0.0-121.77.255.255
                {2078801920,2079064063},//123.232.0.0-123.235.255.255
                {-1950089216,-1948778497},//139.196.0.0-139.215.255.255
                {-1425539072,-1425014785},//171.8.0.0-171.15.255.255
                {-1236271104,-1235419137},//182.80.0.0-182.92.255.255
                {-770113536,-768606209},//210.25.0.0-210.47.255.255
                {-569376768,-564133889}, //222.16.0.0-222.95.255.255
        };

        Random rdint = new Random();
        int index = rdint.nextInt(10); // [0,10)
        // rang[0][1] - rang[0][0] =  524287
        //Random().nextInt(524287)
        //rang[0][0] + Random().nextInt(524287) : 607649792 + Random().nextInt(524287)  ---> [607649792,608174079)
        String ip = num2ip(range[index][0]+new Random().nextInt(range[index][1]-range[index][0]));
        return ip;
    }

    public static String num2ip(int ip) {
        int [] b=new int[4] ;
        String x = "";

        b[0] = (int)((ip >> 24) & 0xff);
        b[1] = (int)((ip >> 16) & 0xff);
        b[2] = (int)((ip >> 8) & 0xff);
        b[3] = (int)(ip & 0xff);
        x=Integer.toString(b[0])+"."+Integer.toString(b[1])+"."+Integer.toString(b[2])+"."+Integer.toString(b[3]);

        return x;
    }
}
