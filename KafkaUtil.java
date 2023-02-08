package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KafkaUtil {

    private static HashMap<String, KafkaConsumer<String, Object>> kafkaConsumerMap = new HashMap<>();
    private static HashMap<String, KafkaProducer<String, Object>> kafkaProducerMap = new HashMap<>();
    //private static String brokerList = "192.168.0.212:9092,192.168.0.213:9092,192.168.0.214:9092,192.168.0.215:9092";
    //private static String brokerList = "47.94.149.36:9092";
    private static String brokerList = "iZ2zehk94dstsat5cjspl6Z:9092";


    //topic列表
    public static List<String> kafkaListTopics() throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);
        // 获取 topic 列表
        Set topics = client.listTopics().names().get();
        System.out.println("=======================");
        System.out.println(topics);
        System.out.println("=======================");

        List<String> topicList = new ArrayList<String>();
        topicList.addAll(topics);
        client.close();
        return topicList;
    }

    //topic创建
    public static void createTopic(String topic) throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);
        // 获取 topic 列表
        NewTopic newTopic = new NewTopic(topic,4, (short)3);
        client.createTopics(Arrays.asList(newTopic)).all().get();
        System.out.println("=======================");

        //System.out.println("CreateTopicsResult : " + createTopicsResult);
        System.out.println("=======================");

        client.close();
        return;
    }

    //topic删除
    public static void delTopic(String topic) throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);
        // 获取 topic 列表
        client.deleteTopics(Arrays.asList(topic)).all().get();

        System.out.println("=======================");
        //System.out.println("deleteTopicsResult  : " + deleteTopicsResult );
        System.out.println("=======================");

        client.close();
        return;
    }

    //topic的分区列表
    public static List<String> partitionsTopic(String topic) throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);
        // 获取 topic 列表
        List<String> descGroups = client.describeTopics(Arrays.asList(topic)).all().get().get(topic).partitions().stream().flatMap(partition -> Stream.of(String.valueOf(partition.partition())+":"+String.valueOf(partition.replicas().size()))).collect(Collectors.toList());;
        //List<St> descGroups1 = client.describeTopics(Arrays.asList(topic)).all().get().get(topic).partitions().stream().map(TopicPartitionInfo::replicas.).collect(Collectors.toList());;
        //AdminClient.deleteRecords()

        System.out.println("=======================");
        System.out.println("descGroups  : " + descGroups );
        //System.out.println("descGroups1  : " + descGroups1 );
        System.out.println("=======================");

        client.close();
        return descGroups;
    }

    //删除groupId
    public static void delGroupId(String groupId) throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);
        // 获取 topic 列表
        client.deleteConsumerGroups(Arrays.asList(groupId)).all().get();

        System.out.println("=======================");
        //System.out.println("deleteTopicsResult  : " + deleteTopicsResult );
        System.out.println("=======================");

        client.close();
        return;
    }

    //集群的节点列表
    public static List<String> descCluster() throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);
        // 获取 topic 列表
        List<String> descGroups = client.describeCluster().nodes().get().stream().flatMap(node -> Stream.of(node.host()+":"+String.valueOf(node.port()))).collect(Collectors.toList());
        System.out.println("=======================");
        System.out.println("descGroups  : " + descGroups );
        //System.out.println("descGroups1  : " + descGroups1 );
        System.out.println("=======================");
        client.close();
        return descGroups;
    }


    //消费者列表
    public static List<String> kafkaConsumerGroups() throws ExecutionException, InterruptedException, TimeoutException {
        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);
        List<String> allGroups = client.listConsumerGroups()
                .valid()
                .get(10, TimeUnit.SECONDS)
                .stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());

        //System.out.println(collection);
        System.out.println("=======================");
        System.out.println(JSON.toJSONString(allGroups));
        //System.out.println(JSON.toJSONString(filteredGroups));
        System.out.println("=======================");
        client.close();
        return allGroups;


    }

    //消费者列表,值得注意的是，上面这个函数无法获取非运行中的consumer group，即虽然一个group订阅了某topic，但是若它所有的consumer成员都关闭的话这个函数是不会返回该group的。
    public static List<String> kafkaConsumerGroups(String topic) throws ExecutionException, InterruptedException, TimeoutException {
        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);
        List<String> allGroups = client.listConsumerGroups()
                .valid()
                .get(10, TimeUnit.SECONDS)
                .stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());

        Map<String, ConsumerGroupDescription> allGroupDetails =
                client.describeConsumerGroups(allGroups).all().get(10, TimeUnit.SECONDS);

        final List<String> filteredGroups = new ArrayList<>();
        allGroupDetails.entrySet().forEach(entry -> {
            String groupId = entry.getKey();
            ConsumerGroupDescription description = entry.getValue();
            boolean topicSubscribed = description.members().stream().map(MemberDescription::assignment)
                    .map(MemberAssignment::topicPartitions)
                    .map(tps -> tps.stream().map(TopicPartition::topic).collect(Collectors.toSet()))
                    .anyMatch(tps -> tps.contains(topic));
            if (topicSubscribed)
                filteredGroups.add(groupId);
        });
        //System.out.println(collection);
        System.out.println("=======================");
        System.out.println(JSON.toJSONString(allGroups));
        System.out.println(JSON.toJSONString(filteredGroups));
        System.out.println("=======================");
        client.close();
        return filteredGroups;


    }


    ///维护内部客户端池
    private static KafkaConsumer<String, Object> getKafkaConsumer(String topic, String groupId) throws ExecutionException, InterruptedException {
        //long ThreadId = Thread.currentThread().getId();
        //System.out.println("topic+groupId+ThreadId = " + topic+groupId+ThreadId);
        KafkaConsumer<String, Object> kafkaConsumer = kafkaConsumerMap.get(topic+groupId);
        if (kafkaConsumer==null){
            //创建 kafka 消费者实例
            kafkaConsumer = getNewKafkaConsumer(topic, groupId);
            kafkaConsumerMap.put(topic+groupId,kafkaConsumer);
        }

        return kafkaConsumer;
    }
    ///维护内部客户端池
    private static KafkaProducer<String, Object> getKafkaProducer() throws ExecutionException, InterruptedException {

        KafkaProducer<String, Object> kafkaProducer = kafkaProducerMap.get("default");
        if (kafkaProducer==null){

            //创建一个生产者对象kafkaProducer
            kafkaProducer = getNewKafkaProducer();
            kafkaProducerMap.put("default",kafkaProducer);
        }

        return kafkaProducer;
    }
    ///正常不需要这个接口，本身支持多线程（不会抛出 ConcurrentModificationException），这个接口仅在想自己在多线程内初始化多个客户端时使用,依旧要受Kafka的“一个Partition只能被该Group里的一个Consumer线程消费”规则的限制,就是如果线程小于分区，也没问题，只是负载不见得均衡，如果大于分区，就会有一些线程消费不到数据
    public static KafkaConsumer<String, Object> getNewKafkaConsumer(String topic, String groupId) throws ExecutionException, InterruptedException {

        //String groupId = "group1";
        //String topic = "hello-kafka";
        //配置消费者客户端
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "2");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //可以根据需求修改序列化类型，其他代码都不需要修改，暂时不支持为特定topic指定序列化类型，只能全局使用一种序列化类型
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        //properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1048576);
        //properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 1100000000);
        //消费消息-fetch.min.bytes:服务器为消费者获取请求返回的最小数据量。如果可用的数据不足，请求将等待大量的数据在回答请求之前累积。默认1B
        //消费消息-fetch.max.wait.ms:我们通过 fetch.min.bytes 告诉 Kafka，等到有足够的数据时才把它返回给消费者。而 fetch.max.wait.ms 则用于指定 broker 的等待时间，默认是 500ms。
        //消费消息-fetch.max.bytes:

        //一次调用poll()操作时返回的最大记录数，默认值为500
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 2000);

        //创建 kafka 消费者实例
        KafkaConsumer<String, Object> kafkaConsumer = new KafkaConsumer<String, Object>(properties);
        //订阅主题
        kafkaConsumer.subscribe(Collections.singletonList(topic));


        return kafkaConsumer;
    }
    ///正常不需要这个接口，本身都支持多线程，这个接口仅在想自己在多线程内初始化多个客户端时使用
    public static KafkaProducer<String, Object> getNewKafkaProducer() throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        //生产消息-ACK机制。0-意味着producer不等待broker同步完成的确认，继续发送下一条(批)信息；1-意味着producer要等待leader成功收到数据并得到确认，才发送下一条message；-1--意味着producer得到follwer确认，才发送下一条数据。
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        // 生产消息-生产者可以重发消息的次数，如果达到这个次数，生产者会放弃重试并返回错误。
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //生产消息-能发送的单个消息的最大值，单位为B，默认为10M
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 10485760);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //可以根据需求修改序列化类型，其他代码都不需要修改，暂时不支持为特定topic指定序列化类型，只能全局使用一种序列化类型
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        //创建一个生产者对象kafkaProducer
        KafkaProducer<String, Object> kafkaProducer = new KafkaProducer<String, Object>(properties);


        return kafkaProducer;
    }


    //生产数据到指定的topic,同步接口 {"topic":"RULEa93304e6d844000","partition":1,"offset":681}
    //KafkaProducer是线程安全的，鼓励用户在多个线程中共享一个KafkaProducer实例，这样通常都要比每个线程维护一个KafkaProducer实例效率要高。
    public static LinkedHashMap<String, Object> sendToKafka(String topic, String key, Object value) throws ExecutionException, InterruptedException {
        long start=System.currentTimeMillis();   //获取开始时间
        KafkaProducer<String, Object> kafkaProducer = getKafkaProducer();
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic,key,value);
        RecordMetadata recordMetadata = kafkaProducer.send(producerRecord).get();

        System.out.println("recordMetadata = " + recordMetadata);
        System.out.println("topic=" + recordMetadata.topic());
        System.out.println("partition=" + recordMetadata.partition());
        System.out.println("offset=" + recordMetadata.offset());

        LinkedHashMap<String, Object> recordMeta = new LinkedHashMap<String, Object>();

        recordMeta.put("topic",recordMetadata.topic());
        recordMeta.put("partition",recordMetadata.partition());
        recordMeta.put("offset",recordMetadata.offset());
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");
        return recordMeta;
    }


    //生产数据到指定的topic，异步接口，默认回调
    public static void sendToKafkaAsync(String topic, String key, Object value) throws ExecutionException, InterruptedException {
        long start=System.currentTimeMillis();   //获取开始时间
        KafkaProducer<String, Object> kafkaProducer = getKafkaProducer();
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic,key,value);

        Callback callback = new Callback() {
            long start=System.currentTimeMillis();   //获取开始时间
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception
                    exception) {
                if (exception == null) {
                    // 消息发送成功
                    System.out.println("消息发送成功");
                    long end=System.currentTimeMillis(); //获取结束时间
                    System.out.println("回调等待时间： "+(end-start)+"ms");
                    System.out.println(TimeUtil.getCurrentDateStringMillisecond());
                    System.out.println("recordMetadata = " + recordMetadata);
                    System.out.println("topic=" + recordMetadata.topic());
                    System.out.println("partition=" + recordMetadata.partition());
                    System.out.println("offset=" + recordMetadata.offset());
                } else {
                    System.out.println("消息发送失败");
                    // 消息发送失败，需要重新发送
                }
            }
        };
        Future<RecordMetadata> recordMetadata = kafkaProducer.send(producerRecord,callback);

        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");
        return;
    }


    //生产数据到指定的topic，异步接口，自定义回调
    public static void sendToKafkaAsync(String topic, String key, Object value,Callback callback) throws ExecutionException, InterruptedException {
        long start=System.currentTimeMillis();   //获取开始时间
        KafkaProducer<String, Object> kafkaProducer = getKafkaProducer();
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic,key,value);
        Future<RecordMetadata> recordMetadata = kafkaProducer.send(producerRecord,callback);
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");
        return;
    }


    //按groupId消费指定topic的数据 [{"topic":"RULEa93304e6d844000","key":"222","value":"aaaa","partition":1,"offset":681}]
    public static ArrayList<LinkedHashMap<String, Object>> recvFromKafka(String topic, String groupId) throws ExecutionException, InterruptedException {

        KafkaConsumer<String, Object> kafkaConsumer = getKafkaConsumer(topic, groupId);
        //用于保存消息的list
        ArrayList buffer = new ArrayList<>();

        long start=System.currentTimeMillis();   //获取开始时间
        ConsumerRecords<String, Object> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(5000));
        kafkaConsumer.commitAsync();
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");
        //System.out.println("i = " + i);
        System.out.println("consumerRecords = " + consumerRecords.count());

        for (ConsumerRecord<String, Object> record : consumerRecords) {
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("topic", record.topic());
            data.put("key", record.key());
            data.put("value", record.value());
            data.put("partition", record.partition());
            data.put("offset", record.offset());
            buffer.add(data);
            System.out.println("订阅主题=" + record.topic());
            System.out.println("消息键值=" + record.key());
            System.out.println("消息内容=" + record.value());
            System.out.println("消息内容分区=" + record.partition());
            System.out.println("消息内容的偏移量=" + record.offset());
        }
        return buffer;
    }


    //消费指定topic指定partition对应的offset数据
    public static ArrayList<LinkedHashMap<String, Object>> recvFromKafkaByOffset(String topic, String groupId,int partition,long offset) throws ExecutionException, InterruptedException {

        KafkaConsumer<String, Object> kafkaConsumer = getKafkaConsumer(topic, groupId);
        //用于保存消息的list
        ArrayList buffer = new ArrayList<>();

        long start=System.currentTimeMillis();   //获取开始时间
//        // 指定位置开始消费
//        Set<TopicPartition> assignment= new HashSet<>();
//        while (assignment.size() == 0) {
//            kafkaConsumer.poll(Duration.ofSeconds(1));
//            // 获取消费者分区分配信息（有了分区分配信息才能开始消费）
//            assignment = kafkaConsumer.assignment();
//        }
        // 遍历所有分区，并指定 offset 从 100 的位置开始消费
        TopicPartition topicPartition = new TopicPartition(topic,partition);
        kafkaConsumer.seek(topicPartition,offset); // 指定offset

        ConsumerRecords<String, Object> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(5000));
        kafkaConsumer.commitAsync();
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");
        //System.out.println("i = " + i);
        System.out.println("consumerRecords = " + consumerRecords.count());

        for (ConsumerRecord<String, Object> record : consumerRecords) {
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("topic", record.topic());
            data.put("key", record.key());
            data.put("value", record.value());
            data.put("partition", record.partition());
            data.put("offset", record.offset());
            buffer.add(data);
            System.out.println("订阅主题=" + record.topic());
            System.out.println("消息键值=" + record.key());
            System.out.println("消息内容=" + record.value());
            System.out.println("消息内容分区=" + record.partition());
            System.out.println("消息内容的偏移量=" + record.offset());
        }
        return buffer;
    }

    //消费指定topic指定partition对应的timestamp以后的数据
    public static ArrayList<LinkedHashMap<String, Object>> recvFromKafkaByTimestamp(String topic, String groupId,int partition,long timestamp) throws ExecutionException, InterruptedException {

        KafkaConsumer<String, Object> kafkaConsumer = getKafkaConsumer(topic, groupId);
        //用于保存消息的list
        ArrayList buffer = new ArrayList<>();

        long start=System.currentTimeMillis();   //获取开始时间
//        // 指定位置开始消费
//        Set<TopicPartition> assignment= new HashSet<>();
//        while (assignment.size() == 0) {
//            kafkaConsumer.poll(Duration.ofSeconds(1));
//            // 获取消费者分区分配信息（有了分区分配信息才能开始消费）
//            assignment = kafkaConsumer.assignment();
//        }
        // 遍历所有分区，并指定 offset 从 100 的位置开始消费
        TopicPartition topicPartition = new TopicPartition(topic,partition);

        Map<TopicPartition, Long> query = new HashMap<>();//构造offsetsForTimes参数，通过时间戳找到offset
        query.put(topicPartition, timestamp);
        Map<TopicPartition, OffsetAndTimestamp> result = kafkaConsumer.offsetsForTimes(query);
        long offset = result.get(topicPartition).offset();

        kafkaConsumer.seek(topicPartition,offset); // 指定offset

        ConsumerRecords<String, Object> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(5000));
        kafkaConsumer.commitAsync();
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");
        //System.out.println("i = " + i);
        System.out.println("consumerRecords = " + consumerRecords.count());

        for (ConsumerRecord<String, Object> record : consumerRecords) {
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("topic", record.topic());
            data.put("key", record.key());
            data.put("value", record.value());
            data.put("partition", record.partition());
            data.put("offset", record.offset());
            buffer.add(data);
            System.out.println("订阅主题=" + record.topic());
            System.out.println("消息键值=" + record.key());
            System.out.println("消息内容=" + record.value());
            System.out.println("消息内容分区=" + record.partition());
            System.out.println("消息内容的偏移量=" + record.offset());
        }
        return buffer;
    }

    //重置指定topic的offset到对应的timestamp
    public static boolean resetOffsetToTimestamp(String topic, String groupId, long timestamp) throws ExecutionException, InterruptedException {

        KafkaConsumer<String, Object> kafkaConsumer = getKafkaConsumer(topic, groupId);
        //用于保存消息的list
        ArrayList buffer = new ArrayList<>();

        long start=System.currentTimeMillis();   //获取开始时间
        // 指定位置开始消费
        Set<TopicPartition> assignment= new HashSet<>();
        while (assignment.size() == 0) {
            kafkaConsumer.poll(Duration.ofSeconds(1));
            // 获取消费者分区分配信息（有了分区分配信息才能开始消费）
            assignment = kafkaConsumer.assignment();
        }
        // 遍历所有分区，并指定 offset 从 100 的位置开始消费
        for (TopicPartition topicPartition : assignment) {
            Map<TopicPartition, Long> query = new HashMap<>();//构造offsetsForTimes参数，通过时间戳找到offset
            query.put(topicPartition, timestamp);
            Map<TopicPartition, OffsetAndTimestamp> result = kafkaConsumer.offsetsForTimes(query);
            if (result.get(topicPartition) !=null){
                long offset = result.get(topicPartition).offset();
                kafkaConsumer.seek(topicPartition,offset); // 指定offset
                kafkaConsumer.commitAsync();
            }

        }
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");

        return true;
    }
    //重置指定topic的offset到最早
    public static boolean resetOffsetToEarliest(String topic, String groupId) throws ExecutionException, InterruptedException {

        KafkaConsumer<String, Object> kafkaConsumer = getKafkaConsumer(topic, groupId);

        long start=System.currentTimeMillis();   //获取开始时间
        // 指定位置开始消费
        Set<TopicPartition> assignment= new HashSet<>();
        while (assignment.size() == 0) {
            kafkaConsumer.poll(Duration.ofSeconds(1));
            // 获取消费者分区分配信息（有了分区分配信息才能开始消费）
            assignment = kafkaConsumer.assignment();
        }
        // 遍历所有分区，并指定 offset 从 100 的位置开始消费
        for (TopicPartition topicPartition : assignment) {
            Map<TopicPartition, Long> query = new HashMap<>();//构造offsetsForTimes参数，通过时间戳找到offset
            long begin = kafkaConsumer.beginningOffsets(Arrays.asList(topicPartition)).get(topicPartition);
            kafkaConsumer.seek(topicPartition,begin); // 指定offset
            kafkaConsumer.commitAsync();
        }
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");

        return true;
    }
    //重置指定topic的offset到最晚，一般在跳过测试脏数据时候使用
    public static boolean resetOffsetToLatest(String topic, String groupId) throws ExecutionException, InterruptedException {

        KafkaConsumer<String, Object> kafkaConsumer = getKafkaConsumer(topic, groupId);

        long start=System.currentTimeMillis();   //获取开始时间
        // 指定位置开始消费
        Set<TopicPartition> assignment= new HashSet<>();
        while (assignment.size() == 0) {
            kafkaConsumer.poll(Duration.ofSeconds(1));
            // 获取消费者分区分配信息（有了分区分配信息才能开始消费）
            assignment = kafkaConsumer.assignment();
        }
        // 遍历所有分区，并指定 offset 从 100 的位置开始消费
        for (TopicPartition topicPartition : assignment) {
            Map<TopicPartition, Long> query = new HashMap<>();//构造offsetsForTimes参数，通过时间戳找到offset
            long end = kafkaConsumer.endOffsets(Arrays.asList(topicPartition)).get(topicPartition);
            kafkaConsumer.seek(topicPartition,end); // 指定offset
            kafkaConsumer.commitAsync();
        }
        long end=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(end-start)+"ms");

        return true;
    }

    ///获取当前消费偏移量情况
//    {
//        "partitionNum": 5,
//            "dataNum": 1,
//            "lagNum": 0,
//            "positions": [{
//        "partition": 0,
//                "begin": 0,
//                "end": 0,
//                "current": 0,
//                "current1": 0,
//                "size": 0,
//                "lag": 0
//    }, {
//        "partition": 4,
//                "begin": 0,
//                "end": 0,
//                "current": 0,
//                "current1": 0,
//                "size": 0,
//                "lag": 0
//    }, {
//        "partition": 3,
//                "begin": 0,
//                "end": 0,
//                "current": 0,
//                "current1": 0,
//                "size": 0,
//                "lag": 0
//    }, {
//        "partition": 2,
//                "begin": 72,
//                "end": 72,
//                "current": 72,
//                "current1": 72,
//                "size": 0,
//                "lag": 0
//    }, {
//        "partition": 1,
//                "begin": 681,
//                "end": 682,
//                "current": 682,
//                "current1": 682,
//                "size": 1,
//                "lag": 0
//    }]
//    }
    private static LinkedHashMap<String, Object> consumerPositions(String topic, String groupId) throws ExecutionException, InterruptedException {
        KafkaConsumer<String, Object> kafkaConsumer = getKafkaConsumer(topic, groupId);

        Properties props = new Properties();
        // 只需要提供一个或多个 broker 的 IP 和端口
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        // 创建 AdminClient 对象
        AdminClient client = KafkaAdminClient.create(props);


        List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor(topic);
        System.out.println("Get the partition info as below:");
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        List<LinkedHashMap<String, Object>> positions = new ArrayList<>();
        long lagNum = 0L;
        long dataNum = 0L;
        for (PartitionInfo partitionInfo : partitionInfos) {
            LinkedHashMap<String, Object> offsetMeta = new LinkedHashMap<String, Object>();
            TopicPartition topicPartition = new TopicPartition(topic, partitionInfo.partition());
            try {
                // 5.通过Consumer.endOffsets(Collections<TopicPartition>)方法获取
                // 指定TopicPartition对应的lastOffset
                long begin = kafkaConsumer.beginningOffsets(Arrays.asList(topicPartition)).get(topicPartition);
                long end = kafkaConsumer.endOffsets(Arrays.asList(topicPartition)).get(topicPartition);
                Set<TopicPartition> partitions = new HashSet<TopicPartition>();
                partitions.add(topicPartition);
                long current = kafkaConsumer.committed(partitions).get(topicPartition).offset();

                long current1 = client.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get().get(topicPartition).offset();
                long partition = topicPartition.partition();

                long lag = end - current;
                long size = end - begin;
                lagNum+=lag;
                dataNum+=size;
                System.out.println("partition = " + partition);
                System.out.println("begin = " + begin);
                System.out.println("end = " + end);
                System.out.println("current = " + current);
                System.out.println("current1 = " + current1);
                System.out.println("size = " + size);
                System.out.println("lag = " + lag);
                offsetMeta.put("partition", partition);
                offsetMeta.put("begin", begin);
                offsetMeta.put("end", end);
                offsetMeta.put("current", current);
                offsetMeta.put("current1", current1);
                offsetMeta.put("size", size);
                offsetMeta.put("lag", lag);
                positions.add(offsetMeta);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        result.put("partitionNum",partitionInfos.size());
        result.put("dataNum",dataNum);
        result.put("lagNum",lagNum);
        result.put("positions",positions);

        return result;
    }

    ///获取指定topic数据量详情情况
    //[{
    //		"partition": 0,
    //		"begin": 0,
    //		"end": 0,
    //		"size": 0
    //	}]
    private static List<LinkedHashMap<String, Object>> topicSize(String topic) throws ExecutionException, InterruptedException{

        String groupId = "guest";
        KafkaConsumer<String, Object> kafkaConsumer = getKafkaConsumer(topic, groupId);

        List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor(topic);
        System.out.println("Get the partition info as below:");
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        for (PartitionInfo partitionInfo : partitionInfos) {
            LinkedHashMap<String, Object> offsetMeta = new LinkedHashMap<String, Object>();
            TopicPartition topicPartition = new TopicPartition(topic, partitionInfo.partition());
            try {
                // 5.通过Consumer.endOffsets(Collections<TopicPartition>)方法获取
                // 指定TopicPartition对应的lastOffset
                long begin = kafkaConsumer.beginningOffsets(Arrays.asList(topicPartition)).get(topicPartition);
                long end = kafkaConsumer.endOffsets(Arrays.asList(topicPartition)).get(topicPartition);
                //Set<TopicPartition> partitions = new HashSet<TopicPartition>();
                //partitions.add(topicPartition);
                long partition = topicPartition.partition();

                long size = end - begin;
                System.out.println("partition = " + partition);
                System.out.println("begin = " + begin);
                System.out.println("end = " + end);
                System.out.println("size = " + size);
                offsetMeta.put("partition",partition);
                offsetMeta.put("begin",begin);
                offsetMeta.put("end",end);
                offsetMeta.put("size",size);
                result.add(offsetMeta);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return result;
    }





    ///获取所有topic数据量详情情况
    // {
    //	"TAG6f6c4f162844000": [{
    //		"partition": 0,
    //		"begin": 0,
    //		"end": 0,
    //		"size": 0
    //	}],
    //	"TAG77362004b844000": [{
    //		"partition": 0,
    //		"begin": 65,
    //		"end": 65,
    //		"size": 0
    //	}]
    //	}
    private static LinkedHashMap<String, Object> topicSizeAll() throws ExecutionException, InterruptedException{
        List<String> topicList = kafkaListTopics();
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        for (String topic : topicList) {
            List<LinkedHashMap<String, Object>> list= topicSize(topic);
            System.out.println("list = " + list);;
            result.put(topic,list);
        }
        return result;
    }


    ///获取指定topic数据量统计{"partitionNum":5452,"dataNum":41570647}
    private static LinkedHashMap<String, Object> topicSizeStatistics(String topic) throws ExecutionException, InterruptedException {

        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        long partitionNum = 0L;
        long dataNum = 0L;

        List<LinkedHashMap<String, Object>> list = topicSize(topic);
        partitionNum = list.size();
        //list.forEach(item->dataNum=dataNum+item.get("size"));
        dataNum = list.stream().mapToLong(item -> (long) item.get("size")).sum();


        result.put("partitionNum", partitionNum);
        result.put("dataNum", dataNum);

        return result;
    }

    ///获取所有topic数据量统计{"topicNum":2550,"partitionNum":5452,"dataNum":41570647}
    private static LinkedHashMap<String, Object> topicSizeStatisticsAll() throws ExecutionException, InterruptedException{
        List<String> topicList = kafkaListTopics();
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        long partitionNum = 0L;
        long dataNum = 0L;
        for (String topic : topicList) {
            List<LinkedHashMap<String, Object>> list= topicSize(topic);
            partitionNum = partitionNum + list.size();
            //list.forEach(item->dataNum=dataNum+item.get("size"));
            dataNum += list.stream().mapToLong(item -> (long) item.get("size")).sum();
        }
        result.put("topicNum",topicList.size());
        result.put("partitionNum",partitionNum);
        result.put("dataNum",dataNum);

        return result;
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException,TimeoutException {
        long start=System.currentTimeMillis();   //获取开始时间

//        List<String> list = KafkaUtil.kafkaListTopics();
//
//        for (String topic : list) {
//
//            System.out.println("list = " + topicSize(topic,"group1"));;
//
//            //TimeUnit.DAYS.sleep(1);
//        }
        //System.out.println("map = " + JSON.toJSONString(topicSizeAll()));
       // System.out.println("map = " + JSON.toJSONString(topicSizeStatistics("RULEa93304e6d844000")));;


        //resetOffsetToEarliest("RULEa93304e6d844000", "group1");
        //LinkedHashMap<String, Object> recordMeta = sendToKafka("RULEa93304e6d844000","222","aaaa");
        //JSONObject object = JSONUtil.parseObj(recordMeta);
        //System.out.println("object.toJSONString(4) = " + object.toJSONString(4));
//        sendToKafka("RULEa93304e6d844000","333","aaaa");
        //delGroupId("group1");
        //System.out.println("kafkaListTopics() = " + kafkaListTopics());
        //LinkedHashMap<String, Object> result = sendToKafka("RULEa93304e6d844000", "222", "aaaa");
        //System.out.println("result = " + JSON.toJSONString(result));
        //ArrayList<LinkedHashMap<String, Object>> buffer = recvFromKafka("RULEa93304e6d844000", "group1");
        //System.out.println("buffer = " + JSON.toJSONString(buffer));
        LinkedHashMap<String, Object> consumerPosition= consumerPositions("RULEa93304e6d844000", "group1");
        System.out.println("consumerPosition = " + JSON.toJSONString(consumerPosition));

       // recvFromKafka("RULEa93304e6d844000", "group1");
        //recvFromKafka("RULEa93304e6d844000", "group1");
        //recvFromKafka("RULEa93304e6d844000", "group3");

        //kafkaConsumerGroups("RULEa93304e6d844000");
        //kafkaConsumerGroups();
//        sendToKafka("RULEa93304e6d844000", "222", "aaaa");
//
//        for (int i = 0; i < 100; i++) {
//            sendToKafkaAsync("RULEa93304e6d844000", "222", "aaaa");
//            //sendToKafka("RULEa93304e6d844000", "333", "aaaa");
//        }

        //createTopic("atest0908");
        //partitionsTopic("atest0908");
        //delTopic("atest0908");
        //partitionsTopic("RULE735e0e863c44000");//50分区
        //descCluster();
        //consumerPositions("RULEa93304e6d844000","group1");
        //delGroupId("group1");
        //recvFromKafka("RULE735e0e863c44000", "group1");

        TimeUnit.DAYS.sleep(1);

 /*       for (int i = 0; i < 100; i++) {

            try {
                sendToKafka("RULEa93304e6d844000","222","aaaa");
               sendToKafka("RULEa93304e6d844000","333","aaaa");
//                sendToKafka("RULEa93304e6d844000","222","aaaa");
//                sendToKafka("RULEa93304e6d844000","333","aaaa");
//                ArrayList buffer1 = recvFromKafka("RULEa93304e6d844000", "group1");
//                System.out.println("buffer1.size() = " + buffer1.size());
//                TimeUnit.SECONDS.sleep(30); // 休眠 1s
                //resetOffsetToTimestamp("RULEa93304e6d844000", "group1",1661853600000L);
                resetOffsetToEarliest("RULEa93304e6d844000", "group1");

                //ArrayList buffer1 = recvFromKafkaByTimestamp("RULEa93304e6d844000", "group1",1,1661853600000L);
                //recvFromKafkaByOffset("RULEa93304e6d844000", "group1",1,10);
                //System.out.println("buffer1.size() = " + buffer1.size());
                TimeUnit.SECONDS.sleep(30); // 休眠 1s

                List<LinkedHashMap<String, Object>> result = consumerPositions("RULEa93304e6d844000", "group1");
                JSONArray array = JSONUtil.parseArray(result);
                System.out.println("array.toJSONString(4) = " + array.toJSONString(4));
                //TimeUnit.DAYS.sleep(1); // 休眠 1 天
            } catch (Exception e) {
                e.printStackTrace();
            }

        }*/
        
        
        
        
        
        
        //long end=System.currentTimeMillis(); //获取结束时间
        //System.out.println("程序运行时间： "+(end-start)+"ms");

    }
}
