package name.cdd.product.kafka.pftest.messagesource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntUnaryOperator;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import name.cdd.product.kafka.pftest.common.TimeNowSetting;

public class CddMesageSource implements LimitedMessageSource
{
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    private String file;
    private List<String> messageList;
    
    private TimeNowSetting tns;
    private boolean isNeedFormatByRegistering_tns;

    public CddMesageSource(String confFile)
    {
        this.file = confFile;
        init();
    }
    
    @Override
    public String getMessage(int index)
    {
        return isNeedFormatByRegistering_tns ? tns.format(messageList.get(index)) : messageList.get(index);
    }

    @Override
    public int getTotalCount()
    {
        return messageList.size();
    }
    
    private void init()
    {
        logger.warn("Intializing source from file " + file + "...");
        
        try
        {
            assembleMessageList();
            registerTimeNowSetting();
        }
        catch(IOException e)
        {
            logger.error("init IOException:" , e);
            messageList = Lists.newArrayList();
        }
    }

    private void assembleMessageList() throws IOException
    {
        messageList = Files.lines(Paths.get(file))
                           .map(line -> toInnaData(line))
                           .filter(op -> op.isPresent())
                           .map(innaData -> toMessageList(innaData.get()))
                           .reduce(new ArrayList<>(), (list1, list2) -> {list1.addAll(list2); return list1;});
    }

    private void registerTimeNowSetting() throws IOException
    {
        tns = new TimeNowSetting();
        Files.lines(Paths.get(file)).forEach(line -> tns.registerMatching(line));
        
        this.isNeedFormatByRegistering_tns = tns.isNeedFormatByRegistering();
    }
    
    
    private List<String> toMessageList(InnaData innaData)
    {
        List<String> result = Lists.newArrayList();
        
        if(isContainsSmartData(innaData.data))
        {
            CddSmartDataParser sdp = new CddSmartDataParser(innaData.data);
            
            for(int i = 0; i < innaData.repeats; i++)
            {
                result.add(sdp.next());
            }
        }
        else
        {
            for(int i = 0; i < innaData.repeats; i++)
            {
                result.add(innaData.data);
            }
        }
        
        return result;
    }

    private boolean isContainsSmartData(String data)
    {
        int index = data.indexOf("[[");
        return index != -1 && index < data.indexOf("]]");
    }

    private Optional<InnaData> toInnaData(String line)
    {
        if(line.trim().equals(""))
        {
            return Optional.empty();
        }
        
        try
        {
            String[] field = line.split("\\|");
            
            InnaData id = new InnaData();
            id.repeats = Integer.parseInt(field[0].trim());
            id.data = field[1].trim();
            
            return Optional.of(id);
        }
        catch(NumberFormatException e)
        {
            logger.error("toInnaData IOException:" , e);
            return Optional.empty();
        }
    }
    
    private class InnaData
    {
        private int repeats;
        private String data;
    }
}

/**
 * 将形如data形如：{"name":"bbb", "value":[[1, +2]]}，转换为：
 *     第一次执行：{"name":"bbb", "value":1}
 *     第二次执行：{"name":"bbb", "value":3}
 *     ......
 * @author A175703
 *
 */
class SmartDataParser2
{
    private String data;
    
    private int currNum;
    private IntUnaryOperator intOper;
    
    private List<Integer[]> startEndIndexList = Lists.newArrayList();

    public SmartDataParser2(String data)
    {
        this.data = data;
        
        Integer[] startEndIndexes = new Integer[] {data.indexOf("[["), data.indexOf("]]") + "]]".length()}; 
        startEndIndexList.add(startEndIndexes);
        
        this.currNum = parseStartNum(data);
        this.intOper = parseIntOper(data);
        
    }

    public String next()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(this.data.substring(0, startEndIndexList.get(0)[0]))
           .append(currNum)
           .append(this.data.substring(startEndIndexList.get(0)[1]));
           
        this.currNum = intOper.applyAsInt(currNum);
        return buf.toString();
    }
    
    private int parseStartNum(String data)
    {
        int startIndex = startEndIndexList.get(0)[0] + "[[".length();
        int endIndex = data.substring(startEndIndexList.get(0)[0]).indexOf(",") + startEndIndexList.get(0)[0];
        return Integer.parseInt(data.substring(startIndex, endIndex).trim());
    }
    
    private IntUnaryOperator parseIntOper(String data)
    {
        int startIndex = data.substring(startEndIndexList.get(0)[0]).indexOf(",") + startEndIndexList.get(0)[0] + 1;
        int endIndex = startEndIndexList.get(0)[1] - "]]".length();
        
        String operStr = data.substring(startIndex, endIndex).trim();
        
        return parseIntOperWithCleanData(operStr);
    }

    private IntUnaryOperator parseIntOperWithCleanData(String data)
    {
        if(data.startsWith("+"))
        {
            return x -> x + Integer.parseInt(data.substring(1));
        }
        else if(data.startsWith("-"))
        {
            return x -> x - Integer.parseInt(data.substring(1));
        }
        
        throw new RuntimeException("parseIntOperWithCleanData not supported data:" + data);
    }
}
