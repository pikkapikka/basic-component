package utils;

import com.softisland.common.utils.HttpClientUtil;
import com.softisland.common.utils.bean.SoftHttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by liwx on 16/3/23.
 */
public class HttpClientTest {
    public static void main(String[] args)throws Exception {
        testStock();
    }

    private static void mulThread()throws Exception{
        List<String> list = new ArrayList<>();
        for(int i=0;i<50;i++){
            Runnable runnable = ()-> {
                SoftHttpResponse response = null;
                try {
                    for(int j=0;j<1;j++){
                        try {
                            TimeUnit.MILLISECONDS.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        response = HttpClientUtil.getSteamStock("76561198264327736");
                        if(null == response.getContent()){
                            list.add("1");
                        }
                        System.out.println(Thread.currentThread().getName()+"@@@@@@@@@"+response.getContent());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Thread rr = new Thread(runnable);
            rr.join();
            rr.start();
        }

        System.out.println("+++++++++++"+list.size());
    }

    private static void testStock()throws Exception{
        //76561198276810732
        SoftHttpResponse response = HttpClientUtil.getSteamStock("76561198276810732");
        System.out.println(response.getContent());
    }
}
