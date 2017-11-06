package name.cdd.product.kafka.pftest;

import java.util.stream.IntStream;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import name.cdd.product.kafka.pftest.common.KafkaProducerFactory;

public class KafkaSendingTask implements Runnable
{
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    private String threadNumber;
    private MessageListManager messageManager;
    private int numberPerBatch;
    private String kafkaTopic;
    private String bootstrapServers;

    public KafkaSendingTask(int number, MessageListManager messageManager, int numberPerBatch, String kafkaTopic, String bootstrapServers)
    {
        this.threadNumber = "KafkaSendingTask_Thread_" + number;
        this.messageManager = messageManager;
        this.numberPerBatch = numberPerBatch;
        this.kafkaTopic = kafkaTopic;
        this.bootstrapServers = bootstrapServers;
    }

    @Override
    public void run()
    {
        Producer<String, String> producer = KafkaProducerFactory.createSimpleProducer(bootstrapServers);
        
        int[] startEndIndexes = messageManager.reserve(numberPerBatch);
        
        if(startEndIndexes != null)
        {
            IntStream.range(startEndIndexes[0], startEndIndexes[1])
                     .mapToObj(index -> messageManager.getMessage(index))
                     .forEach(message -> sendToKafka(producer, message));
   
            logger.warn(threadNumber + " end running.");
        }
        
        producer.close();
    }
    
    private void sendToKafka(Producer<String, String> producer, String message)
    {
        ProducerRecord<String, String> produceRecord = new ProducerRecord<>(kafkaTopic, message);
        logger.info("sending message: " + message);
        producer.send(produceRecord, (metaData, exception) -> {if(exception != null) {logger.error("sendToKafka callback exception:", exception);}});
    }
}
