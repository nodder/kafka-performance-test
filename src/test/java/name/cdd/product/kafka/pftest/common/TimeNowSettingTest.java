package name.cdd.product.kafka.pftest.common;

import junit.framework.TestCase;
import name.cdd.product.kafka.pftest.common.TimeNowSetting;

public class TimeNowSettingTest extends TestCase
{
    private static final long TIMENOW = 12345L;
    private static final String TimeNow_STR1 ="time_now_str1";
    
    private TimeNowSetting tns;
    
    @Override
    protected void setUp() throws Exception
    {
        tns = new TimeNowSetting() {
            
        @Override
        protected long getTimeNow()
        {
            return TIMENOW;
        }
        
        @Override
        protected String getTimeNow_STR1()
        {
            return TimeNow_STR1;
        }};
    }
    
    public void test_registerMatching_once_and_noMathching()
    {
        tns.registerMatching("abcd");
        
        assertFalse(tns.isNeedFormatByRegistering());
    }
    
    public void test_registerMatching_once_and_mathched_now_str1()
    {
        String line = "time:$$NOW-STR1$$";
        tns.registerMatching(line);
        assertTrue(tns.isNeedFormatByRegistering());
        
        assertEquals("time:" + TimeNow_STR1, tns.format(line));
    }
    
    public void test_registerMatching_once_and_mathched_now_long()
    {
        tns.registerMatching("time:$$NOW-LONG$$");
        assertTrue(tns.isNeedFormatByRegistering());
    }
    
    
    public void test_registerMatching_muliti_and_noMatching()
    {
        tns.registerMatching("abcd");
        tns.registerMatching("efgh");
        
        assertFalse(tns.isNeedFormatByRegistering());
    }
    
    public void test_registerMatching_muliti_timenowlong_and_matched()
    {
        String line1 = "abcd";
        String line2 = "time:$$NOW-LONG$$";
        String line3 = "time:\"$$NOW-LONG$$\"";
        
        tns.registerMatching(line1);
        tns.registerMatching(line2);
        tns.registerMatching(line3);
        
        assertTrue(tns.isNeedFormatByRegistering());
        
        assertEquals(line1, tns.format(line1));
        assertEquals("time:" + TIMENOW, tns.format(line2));
        assertEquals("time:\"" + TIMENOW + "\"", tns.format(line3));
    }
    
    
    public void test_registerMatching_mixed_time_longAndStr1()
    {
        String line1 = "abcd";
        String line2 = "time:$$NOW-LONG$$";
        String line3 = "time:$$NOW-STR1$$";
        String line4 = "efgh";
        String line5 = "time:\"$$NOW-LONG$$\"";
        
        tns.registerMatching(line1);
        tns.registerMatching(line2);
        tns.registerMatching(line3);
        tns.registerMatching(line4);
        tns.registerMatching(line5);
        
        assertTrue(tns.isNeedFormatByRegistering());
        
        assertEquals(line1, tns.format(line1));
        assertEquals("time:" + TIMENOW, tns.format(line2));
        assertEquals("time:" + TimeNow_STR1, tns.format(line3));
        assertEquals(line4, tns.format(line4));
        assertEquals("time:\"" + TIMENOW + "\"", tns.format(line5));
    }
}
