package com.koolib.util;

public class DefineBase64Utils
{
    final static String encodingChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private static char[] getPaddedBytes(String source)
    {
        char[] converted = source.toCharArray();
        int requiredLength = 3 * ((converted.length + 2) / 3);
        char[] result = new char[requiredLength];
        System.arraycopy(converted, 0, result, 0, converted.length);
        return result;
    }

    /******Returns the base 64 encoded equivalent of a supplied string*****/
    /*******************@param source the string to encode*****************/
    public static String encode(String source)
    {
        char[] sourceBytes = getPaddedBytes(source);
        int numGroups = sourceBytes.length / 3;
        char[] targetBytes = new char[4];
        char[] target = new char[4 * numGroups];
        for (int group = 0; group < numGroups; group++)
        {
            convert3To4(sourceBytes, group * 3, targetBytes);
            for (int i = 0; i < targetBytes.length; i++)
            {
                target[i + 4 * group] = encodingChar.charAt(targetBytes[i]);
            }
        }

        int numPadBytes = sourceBytes.length - source.length();
        for (int i = target.length - numPadBytes; i < target.length; i++)
            target[i] = '=';
        return new String(target);
    }

    /*****Returns the plaintext equivalent of a base 64-encoded string.****/
    /**source base 64 string (which must have a multiple of 4 characters)**/
    public static String decode(String source)
    {
        if (source.length() % 4 != 0)
            throw new RuntimeException("valid Base64 codes have a multiple of 4 characters");
        int numGroups = source.length() / 4;
        int numExtraBytes = source.endsWith("==") ? 2 : (source.endsWith("=") ? 1 : 0);
        char[] targetBytes = new char[3 * numGroups];
        char[] sourceBytes = new char[4];
        for (int group = 0; group < numGroups; group++)
        {
            for (int i = 0; i < sourceBytes.length; i++)
            {
                sourceBytes[i] = (char) Math.max(0, encodingChar.indexOf(source.charAt(4 * group + i)));
            }
            convert4To3(sourceBytes, targetBytes, group * 3);
        }
        return new String(targetBytes, 0, targetBytes.length - numExtraBytes);
    }

    private static void convert4To3(char[] source, char[] target, int targetIndex)
    {
        target[targetIndex] = (char) ((source[0] << 2) | (source[1] >> 4));
        target[targetIndex + 1] = (char) (((source[1] & 0x0f) << 4) | (source[2] >> 2));
        target[targetIndex + 2] = (char) (((source[2] & 0x03) << 6) | (source[3]));
    }

    private static void convert3To4(char[] source, int sourceIndex, char[] target)
    {
        target[0] = (char) (source[sourceIndex] >>> 2);
        target[1] = (char) (((source[sourceIndex] & 0x03) << 4) | (source[sourceIndex + 1] >>> 4));
        target[2] = (char) (((source[sourceIndex + 1] & 0x0f) << 2) | (source[sourceIndex + 2] >>> 6));
        target[3] = (char) (source[sourceIndex + 2] & 0x3f);
    }
}