package name.cdd.product.kafka.pftest.messagesource;

import junit.framework.TestCase;
import name.cdd.product.kafka.pftest.messagesource.CddSmartDataParser;

public class CddSmartDataParserTest extends TestCase
{
    public void testSmartDataParser_add()
    {
        String data = "{\"name\":\"bbb\", \"value\":[[1, +2]]}";
        
        CddSmartDataParser sdp = new CddSmartDataParser(data);
        
        assertEquals("{\"name\":\"bbb\", \"value\":1}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":3}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":5}", sdp.next());
    }
    
    public void testSmartDataParser_minus()
    {
        String data = "{\"name\":\"bbb\", \"value\":[[11, -10]]}";
        
        CddSmartDataParser sdp = new CddSmartDataParser(data);
        
        assertEquals("{\"name\":\"bbb\", \"value\":11}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":1}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":-9}", sdp.next());
    }
    
    public void testSmartDataParser_limit1()
    {
        String data = "{\"name\":\"bbb\", \"value\":[[1, +1, 3]]}";
        
        CddSmartDataParser sdp = new CddSmartDataParser(data);
        
        assertEquals("{\"name\":\"bbb\", \"value\":1}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":2}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":3}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":1}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":2}", sdp.next());
    }
    
    public void testSmartDataParser_limit2()
    {
        String data = "{\"name\":\"bbb\", \"value\":[[1, +3, 5]]}";
        
        CddSmartDataParser sdp = new CddSmartDataParser(data);
        
        assertEquals("{\"name\":\"bbb\", \"value\":1}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":4}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":1}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\":4}", sdp.next());
    }
    
    
    public void testSmartDataParser_extra_spaces()
    {
        String data = "{\"name\":\"bbb\", \"value\": [[ 11,-10 ]]}";
        
        CddSmartDataParser sdp = new CddSmartDataParser(data);
        
        assertEquals("{\"name\":\"bbb\", \"value\": 11}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\": 1}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\": -9}", sdp.next());
    }
    
    public void testSmartDataParser_multi_extra_spaces1()
    {
        String data = "{\"name\":\"bbb\", \"value\": [[ 11,-10 ]], \"value2\": [[ 11,+10 ]]}";
        
        CddSmartDataParser sdp = new CddSmartDataParser(data);
        
        assertEquals("{\"name\":\"bbb\", \"value\": 11, \"value2\": 11}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\": 1, \"value2\": 21}", sdp.next());
        assertEquals("{\"name\":\"bbb\", \"value\": -9, \"value2\": 31}", sdp.next());
    }
        
    public void testSmartDataParser_mix1()
    {
        String data = "{\"total_in_quantity\":[[1, +2]],\"second_auth_percent\":\"[[1, +2]]\",\"second_in_quantity\":[[1, +1]],\"time\":\"2017-10-19 23:37:55\",\"event\":\"pdaRealtimeEveryCase\",\"emp_name\":\"A139980\",\"emp_id\":\"A139980\",\"event_id\":\"[[1000, +1]]\",\"type\":\"pdaRealtimeEveryCaseDetail\",\"group\":\"1D\",\"channel\":\"1\",\"city\":\"018\",\"event_id\":\"18\"}";
        
        CddSmartDataParser sdp = new CddSmartDataParser(data);
        
        assertEquals("{\"total_in_quantity\":1,\"second_auth_percent\":\"1\",\"second_in_quantity\":1,\"time\":\"2017-10-19 23:37:55\",\"event\":\"pdaRealtimeEveryCase\",\"emp_name\":\"A139980\",\"emp_id\":\"A139980\",\"event_id\":\"1000\",\"type\":\"pdaRealtimeEveryCaseDetail\",\"group\":\"1D\",\"channel\":\"1\",\"city\":\"018\",\"event_id\":\"18\"}", sdp.next());
        assertEquals("{\"total_in_quantity\":3,\"second_auth_percent\":\"3\",\"second_in_quantity\":2,\"time\":\"2017-10-19 23:37:55\",\"event\":\"pdaRealtimeEveryCase\",\"emp_name\":\"A139980\",\"emp_id\":\"A139980\",\"event_id\":\"1001\",\"type\":\"pdaRealtimeEveryCaseDetail\",\"group\":\"1D\",\"channel\":\"1\",\"city\":\"018\",\"event_id\":\"18\"}", sdp.next());
        assertEquals("{\"total_in_quantity\":5,\"second_auth_percent\":\"5\",\"second_in_quantity\":3,\"time\":\"2017-10-19 23:37:55\",\"event\":\"pdaRealtimeEveryCase\",\"emp_name\":\"A139980\",\"emp_id\":\"A139980\",\"event_id\":\"1002\",\"type\":\"pdaRealtimeEveryCaseDetail\",\"group\":\"1D\",\"channel\":\"1\",\"city\":\"018\",\"event_id\":\"18\"}", sdp.next());
    }
    
    public void testSmartDataParser_mix2()
    {
        String data = "{\"total_in_quantity\":[[1, +2]],\"second_auth_percent\":\"[[1, +50, 100]]\",\"second_in_quantity\":[[1, +1]],\"time\":\"2017-10-19 23:37:55\",\"event\":\"pdaRealtimeEveryCase\",\"emp_name\":\"A139980\",\"emp_id\":\"A139980\",\"event_id\":\"[[1000, +1]]\",\"type\":\"pdaRealtimeEveryCaseDetail\",\"group\":\"1D\",\"channel\":\"1\",\"city\":\"018\",\"event_id\":\"18\"}";
        
        CddSmartDataParser sdp = new CddSmartDataParser(data);
        
        assertEquals("{\"total_in_quantity\":1,\"second_auth_percent\":\"1\",\"second_in_quantity\":1,\"time\":\"2017-10-19 23:37:55\",\"event\":\"pdaRealtimeEveryCase\",\"emp_name\":\"A139980\",\"emp_id\":\"A139980\",\"event_id\":\"1000\",\"type\":\"pdaRealtimeEveryCaseDetail\",\"group\":\"1D\",\"channel\":\"1\",\"city\":\"018\",\"event_id\":\"18\"}", sdp.next());
        assertEquals("{\"total_in_quantity\":3,\"second_auth_percent\":\"51\",\"second_in_quantity\":2,\"time\":\"2017-10-19 23:37:55\",\"event\":\"pdaRealtimeEveryCase\",\"emp_name\":\"A139980\",\"emp_id\":\"A139980\",\"event_id\":\"1001\",\"type\":\"pdaRealtimeEveryCaseDetail\",\"group\":\"1D\",\"channel\":\"1\",\"city\":\"018\",\"event_id\":\"18\"}", sdp.next());
        assertEquals("{\"total_in_quantity\":5,\"second_auth_percent\":\"1\",\"second_in_quantity\":3,\"time\":\"2017-10-19 23:37:55\",\"event\":\"pdaRealtimeEveryCase\",\"emp_name\":\"A139980\",\"emp_id\":\"A139980\",\"event_id\":\"1002\",\"type\":\"pdaRealtimeEveryCaseDetail\",\"group\":\"1D\",\"channel\":\"1\",\"city\":\"018\",\"event_id\":\"18\"}", sdp.next());
    }
}
