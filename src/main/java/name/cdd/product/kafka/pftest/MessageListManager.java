package name.cdd.product.kafka.pftest;

import name.cdd.product.kafka.pftest.common.MessageListCallback;
import name.cdd.product.kafka.pftest.messagesource.LimitedMessageSource;

public class MessageListManager
{
    private MessageListCallback callback;
    private LimitedMessageSource msgSrc;
    
    private int totalCount;
    private int currIndex = 0;

    public MessageListManager(MessageListCallback callback)
    {
        this.callback = callback;
    }
    
    public void setMessageSource(LimitedMessageSource msgSrc)
    {
        this.msgSrc = msgSrc;
        this.totalCount = msgSrc.getTotalCount();
    }

    public String getMessage(int index)
    {
        return msgSrc.getMessage(index);
    }

    public synchronized int[] reserve(int number)
    {
        if(isFinish())
        {
            return null;
        }
        
        int endIndex = Math.min(currIndex + number, totalCount);
        
        if(endIndex == totalCount)
        {
            this.callback.onMessageSentFinish();
        }
        int[] result = new int[] {currIndex, endIndex};
        this.currIndex = endIndex;
        
        return result;
    }

    private boolean isFinish()
    {
        return this.currIndex == totalCount;
    }
}
