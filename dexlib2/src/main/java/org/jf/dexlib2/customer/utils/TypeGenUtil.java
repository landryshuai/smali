package org.jf.dexlib2.customer.utils;

/**
 * Created by shuaijiman on 2016/8/9.
 */
public class TypeGenUtil
{
    public static String newType(String type)
    {
        return type.substring(0, type.length() - 1) + "_CF;";
    }
}
