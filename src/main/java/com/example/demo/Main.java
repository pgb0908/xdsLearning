package com.example.demo;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {


        NewXdsClient xdsClient = new NewXdsClient("localhost", 9002);
        xdsClient.subscribeCds();
        xdsClient.subscribeEds();
        xdsClient.detectRequestQueue();

        xdsClient.shutdown();

        sleep(50000);

    }

}
