package name.cdd.product.kafka.pftest.messagesource;

import java.util.List;

import com.google.common.collect.Lists;

public class SimpleMessageSource implements LimitedMessageSource
{
    List<String> messageList = Lists.newArrayList(
        "aaaaaaa",
        "bbbbbb",
        "cccccc",
        "dddd",
        "eeee",
        "ffff",
        "gggg"
                    );
    
    @Override
    public String getMessage(int index)
    {
        return messageList.get(index);
    }

    @Override
    public int getTotalCount()
    {
        return messageList.size();
    }
    
}
