package com.sui.haedal.common;

import com.sui.haedal.model.enums.DecimalType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    /**
     *
     * Decimal 计算
     * @param type 1加 2减 3乘 4除
     * @param bigDecimal
     * @param bigDecimal2
     * @param decimals 保留小数位
     * @param roundingMode RoundingMode.DOWN 丢弃多余小数 RoundingMode.UP:四舍五入
     * @return
     */
    public static BigDecimal calculate(Integer type, BigDecimal bigDecimal, BigDecimal bigDecimal2, int decimals, RoundingMode roundingMode){
        BigDecimal result = new BigDecimal(0);
        if(DecimalType.ADD.getValue().equals(type)){
            result = bigDecimal.add(bigDecimal2);
        }else if(DecimalType.SUBTRACT.getValue().equals(type)){
            result = bigDecimal.subtract(bigDecimal2);
        }else if(DecimalType.MULTIPLY.getValue().equals(type)){
            result = bigDecimal.multiply(bigDecimal2);
        }else if(DecimalType.DIVIDE.getValue().equals(type)){
            result = bigDecimal.divide(bigDecimal2,decimals,roundingMode);
        }
        return result.setScale(decimals,roundingMode);
    }

}
