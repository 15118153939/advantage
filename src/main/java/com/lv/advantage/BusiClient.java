package com.lv.advantage;

import java.util.Scanner;

/**
 * @author 吕明亮
 * @Date : 2019/11/19 17:10
 * @Description:
 */
public class BusiClient {
    public static void main(String[] args) throws InterruptedException {
        NettyClient nettyClient = new NettyClient();
        new Thread(nettyClient).start();


        while (!nettyClient.isConnected()) {
            synchronized (nettyClient) {
                nettyClient.wait();
            }
        }
        System.out.println("网络通信已准备好，可以进行业务操作了........");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.next();
            if (msg == null) {
                continue;
            } else if ("q".equals(msg.toLowerCase())) {
                nettyClient.close();
                while (nettyClient.isConnected()) {
                    synchronized (nettyClient) {
                        nettyClient.wait();
                    }
                }
                scanner.close();
                System.exit(1);
            } else {
                nettyClient.send(msg);
            }
        }

    }
}
