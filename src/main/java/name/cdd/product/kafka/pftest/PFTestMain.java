package name.cdd.product.kafka.pftest;

import org.apache.log4j.Logger;

import name.cdd.product.kafka.pftest.messagesource.CddMesageSource;
import name.cdd.product.kafka.pftest.messagesource.LimitedMessageSource;

public class PFTestMain
{   
    private static Logger logger = Logger.getLogger(PFTestMain.class);

    private static boolean isFromBat(String[] args)
    {
        return args != null && args.length == 5;
    }
    
    public static void main(String[] args)
    {
        int threadNum = isFromBat(args) ? Integer.parseInt(args[0]) : 3;
        int numberPerBatch = isFromBat(args) ? Integer.parseInt(args[1]) : 10000;
        String bootstrapServers = isFromBat(args) ? args[2] : "localhost:9092";//"182.180.115.73:9092,182.180.115.74:9092,182.180.115.75:9092";
        String kafkaTopic = isFromBat(args) ? args[3] : "sparktest";
        String confFile = isFromBat(args) ? args[4] : "example.cdd";
        
        logger.warn("bootstrapServers=" + bootstrapServers + "; kafkaTopic=" + kafkaTopic);
        logger.warn("threadNum=" + threadNum + "; numberPerBatch=" + numberPerBatch);
        
        LimitedMessageSource msgSrc = new CddMesageSource(confFile);//new SimpleMessageSource();
        PFTestProcess process = new PFTestProcess(threadNum, numberPerBatch, bootstrapServers, kafkaTopic);
        process.setMessageSource(msgSrc);
        process.process();
    }
}
