package name.cdd.product.kafka.pftest.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class TimeNowSetting
{
    private SimpleDateFormat sdf_str1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
    
    @SuppressWarnings ("serial")
    private Map<String, Supplier<String>> strDateMap = new HashMap<String, Supplier<String>>(){{
        put("$$NOW-STR1$$", () -> getTimeNow_STR1());
    }};

    @SuppressWarnings ("serial")
    private Map<String, Supplier<Long>> longDateMap = new HashMap<String, Supplier<Long>>(){{
        put("$$NOW-LONG$$", () -> getTimeNow());
    }};

    private List<String> REGISTERED_STR_KEYS = Lists.newArrayList();
    private List<String> REGISTERED_LONG_KEYS = Lists.newArrayList();

    public void registerMatching(String pattern)
    {
        REGISTERED_STR_KEYS.addAll(strDateMap.keySet().stream().filter(key -> pattern.contains(key)).collect(Collectors.toList()));
        REGISTERED_LONG_KEYS.addAll(longDateMap.keySet().stream().filter(key -> pattern.contains(key)).collect(Collectors.toList()));
    }
    
    public boolean isNeedFormatByRegistering()
    {
         return REGISTERED_STR_KEYS.size() + REGISTERED_LONG_KEYS.size() != 0;
    }
               
    public String format(String data)
    {
        for(String key : REGISTERED_STR_KEYS)
        {
            data = data.replace(key, strDateMap.get(key).get());
        }
        
        for(String key : REGISTERED_LONG_KEYS)
        {
            data = data.replace(key, longDateMap.get(key).get()+ "");
        }

        return data;
    }
    
    protected long getTimeNow()
    {
        return new Date().getTime();
    }
    
    protected String getTimeNow_STR1()
    {
        return sdf_str1.format(new Date());
    }
}
