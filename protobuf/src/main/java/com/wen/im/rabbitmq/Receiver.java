package com.wen.im.rabbitmq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import com.wen.im.proto.AddressBookProtos;
import com.wen.im.rabbitmq.utils.ConnectionUtil;

import java.io.IOException;

/**
 * @classname: Receiver
 * @description: Receiver
 * @data: 2020-07-17 00:47
 * @author: Musk
 */
public class Receiver {

    private final static String QUEUE = "MQ_Protobuf";//队列的名字

    public static void main(String[] args) throws Exception{
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE,false,false,false,null);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        /* Since RabbitMQ Server will push messages asynchronously, we provide a callback
           in the form of an object that will buffer the messages until we're ready to use them
        * */
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                try {
                    AddressBookProtos.Person PersonCopy = AddressBookProtos.Person.parseFrom(body);
                    System.out.println("Received Message!");
                    System.out.println("Person ID: " + PersonCopy.getId());
                    System.out.println("Name: " + PersonCopy.getName());
                    System.out.println("Email: " + PersonCopy.getEmail());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };
        //接收消息 ,参数2是自动确认
        channel.basicConsume(QUEUE, true, consumer);
    }

}
