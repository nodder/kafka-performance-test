package name.cdd.product.kafka.pftest.common;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

public class KafkaProducerFactory {
    public static Producer<String, String> createSimpleProducer(String bootstrapServers) {
        return new KafkaProducer<String, String>(assembleProperties(bootstrapServers));
    }
    
    private static Properties assembleProperties(String bootstrapServers) {
        Properties props = new Properties();
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("bootstrap.servers", bootstrapServers);
        
        return props;
    }
}
