package com.hlz.order;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.hlz.net.UrlManager;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQService extends Service {
    private String TAG="RabbitMQService";
    //RabbitMQ连接工厂
    private ConnectionFactory factory=new ConnectionFactory();
    private Thread subscribeThread;
    private  static Handler handler;//用于更新ui
    public static void setHandler(Handler handler1) {
        handler=handler1;
    }
    private void setupConnectionFactory() {
        factory.setHost(UrlManager.getUrlManager().findURL(MyApplication.getContext(),"rabbitMQ").getUrl());
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //连接设置
        setupConnectionFactory();
        subscribeThread=subscribe(handler);
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw  new RuntimeException("no support");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        subscribeThread.interrupt();
    }
    /**
     * 消费者线程
     */
    Thread subscribe(final Handler handler) {
        Thread subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                Channel channel = null;
                while (true) {
                    try {
                        //使用之前的设置，建立连接
                        connection = factory.newConnection();
                        //创建一个通道
                        channel = connection.createChannel();
                        channel.basicQos(1);
                        AMQP.Queue.DeclareOk q = channel.queueDeclare();
                        channel.queueBind(q.getQueue(),"indent","fanout");
                        //创建消费者
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(q.getQueue(), true, consumer);
                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            String message = new String(delivery.getBody());
                            System.out.println(" [x] Received '" + message + "'");
                            handler.sendEmptyMessage(1);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    } finally {
                        Log.d(TAG, "Success InterruptedException finally");
                        try {
                            if (channel != null&&channel.isOpen()) {
                                channel.close();
                            }
                            if (connection != null&&connection.isOpen()) {
                                connection.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        subscribeThread.start();
        return subscribeThread;
    }
}
