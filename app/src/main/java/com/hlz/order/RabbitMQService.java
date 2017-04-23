package com.hlz.order;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

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
        factory.setHost("localhost");
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
                        channel.exchangeDeclare("indent","fanout");
                        String queueName=channel.queueDeclare().getQueue();
                        //创建消费者
                        channel.queueBind(queueName,"indent","");
                        Consumer consumer=new DefaultConsumer(channel){
                            @Override
                            public void handleDelivery(String consumerTag, Envelope envelope,AMQP.BasicProperties properties,byte[] body)throws
                                    IOException{
                                String message=new String(body,"UTF-8");
                                handler.sendEmptyMessage(1);
                            }
                        };
                        channel.basicConsume(queueName, true, consumer);
                    } catch (Exception e1) {
                        Log.d(TAG, "Connection broken: " + e1.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    } finally {
                        Log.d(TAG, "Success InterruptedException finally");
                        try {
                            if (channel != null) {
                                channel.close();
                            }
                            if (connection != null) {
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
