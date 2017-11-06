package name.cdd.product.kafka.pftest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import name.cdd.product.kafka.pftest.common.MessageListCallback;
import name.cdd.product.kafka.pftest.messagesource.LimitedMessageSource;


public class PFTestProcess implements MessageListCallback
{
    private int threadNum;
    private ScheduledExecutorService ses;
    private MessageListManager manager;
    private int numberPerBatch;
    private String kafkaTopic;
    private String bootstrapServers;
    
    public PFTestProcess(int threadNum, int numberPerBatch, String bootstrapServers, String kafkaTopic)
    {
        this.threadNum = threadNum;
        this.ses = Executors.newScheduledThreadPool(threadNum);
        this.manager = new MessageListManager(this);
        
        this.numberPerBatch = numberPerBatch;
        this.bootstrapServers = bootstrapServers;
        this.kafkaTopic = kafkaTopic;
    }
    
    public void setMessageSource(LimitedMessageSource source)
    {
        manager.setMessageSource(source);
    }
    
    public void process()
    {
        Stream.iterate(0, x -> x + 1).limit(threadNum)
                                     .forEach(num -> ses.scheduleAtFixedRate(new KafkaSendingTask(num, manager, numberPerBatch, kafkaTopic, bootstrapServers), 
                                                                             0, 1000, TimeUnit.MILLISECONDS));
    }
     
    @Override
    public void onMessageSentFinish()
    {
        ses.shutdown();
    }
}
