package name.cdd.product.kafka.pftest.messagesource;

/**
 * 有界的数据源
 * @author A175703
 *
 */
public interface LimitedMessageSource
{
    String getMessage(int index);
    int getTotalCount();
}
