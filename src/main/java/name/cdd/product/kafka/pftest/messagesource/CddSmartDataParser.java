package name.cdd.product.kafka.pftest.messagesource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

/**
 * 将形如data形如：{"name":"bbb", "value":[[1, +2]]}，转换为：
 *     第一次执行：{"name":"bbb", "value":1}
 *     第二次执行：{"name":"bbb", "value":3}
 *     ......
 * 支持多个[[]]，具体见测试用例
 * @author A175703
 *
 */
public class CddSmartDataParser
{
    private String data;
    private List<Integer[]> startEndIndexList;
    private List<SinglePeriodData> singlePeriodDataTransformerList;
    
    public CddSmartDataParser(String data)
    {
        this.data = data;
        this.startEndIndexList = parseStartEndIndexList(data);
        this.singlePeriodDataTransformerList = parseSinglePeriodDataTransformer(startEndIndexList);
    }
    
    private List<SinglePeriodData> parseSinglePeriodDataTransformer(List<Integer[]> startEndIndexList)
    {
        return startEndIndexList.stream()
                         .map(startEndIndexes -> new SinglePeriodData(data.substring(startEndIndexes[0], startEndIndexes[1])))
                         .collect(Collectors.toList());
    }

    private List<Integer[]> parseStartEndIndexList(String data)
    {
        List<Integer[]> list = new ArrayList<>();
        
        int offset = 0;
        while(true)
        {
            String operatingData = data.substring(offset);
            
            int startIndex = operatingData.indexOf("[[");
            int endIndex = operatingData.indexOf("]]") + 2;
            
            if(startIndex < endIndex && startIndex != -1)
            {
                list.add( new Integer[] {startIndex + offset, endIndex + offset});
                operatingData = operatingData.substring(endIndex);
                offset += endIndex;
            }
            else
            {
                break;
            }
        }
        
        return list;
    }

   public String next()
   {
       StringBuffer buf = new StringBuffer();
       
       int lastIndex = 0;
       for(int i = 0; i < startEndIndexList.size(); i++)
       {
           int startIndex = startEndIndexList.get(i)[0];
           int endIndex = startEndIndexList.get(i)[1];
           
           buf.append(this.data.substring(lastIndex, startIndex))
              .append(singlePeriodDataTransformerList.get(i).nextInt());
           
           lastIndex = endIndex;
       }
       
       buf.append(this.data.substring(lastIndex));
          
       return buf.toString();
   }

  //格式形如：[[1, +1]] 或 [[1, +1, 3]]
    private class SinglePeriodData
    {
        private int currNum;
        private IntUnaryOperator intOper;
        
        private Optional<Integer> opMaxNum;
        private int initNum;//只有maxNum有效时，才有用

        public SinglePeriodData(String data)
        {            
            //1, +1  或 1, +1, 3
            String stripedData = data.substring(2, data.length() - 2);
            
            String[] splittedStriptedData = stripedData.split(",");
            this.currNum = parseNum(splittedStriptedData[0]);
            this.intOper = parseIntOper(splittedStriptedData[1]);
            
            this.opMaxNum = splittedStriptedData.length == 3 ? Optional.of(parseNum(splittedStriptedData[2])) : Optional.empty();
            this.initNum = currNum;
        }

        public int nextInt()
        {
            int rtnData = currNum;
            this.currNum = calcNextInt();
            
            return rtnData;
        }

        private int calcNextInt()
        {
            int num = intOper.applyAsInt(currNum);
            if(opMaxNum.isPresent() && num > opMaxNum.get())
            {
                num = initNum;
            }
            return num;
        }
        
        private int parseNum(String data)
        {
            return Integer.parseInt(data.trim());
        }
        
        private IntUnaryOperator parseIntOper(String data)
        {
            String operator_and_operand = data.trim();
            char oper = operator_and_operand.charAt(0);
            int operand = Integer.parseInt(operator_and_operand.substring(1));
            return parseIntOperWithCleanData(oper, operand);
        }

        private IntUnaryOperator parseIntOperWithCleanData(char oper, int operand)
        {
            if(oper == '+')
            {
                return x -> x + operand;
            }
            else if(oper == '-')
            {
                return x -> x - operand;
            }
            
            throw new RuntimeException("parseIntOperWithCleanData not supported: " + oper + ":" + operand);
        }
    }
}
