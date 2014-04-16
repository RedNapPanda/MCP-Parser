package com.not2excel.lib.mapping;

/**
 * Created by Richmond Steele on 2/26/14 at 2:58 PM.
 * All Rights Reserved unless alternate license provided.
 */
public class MappedMethod extends Mapped
{
    private final String notchSig;
    private final String seargeSig;

    private       String unobf;

    public MappedMethod(String notch, String notchSig, String searge, String seargeSig, String fullMap)
    {
        super(notch, searge, fullMap);
        this.notchSig = notchSig;
        this.seargeSig = seargeSig;
    }

    public String getNotchSig()
    {
        return notchSig;
    }

    public String getSeargeSig()
    {
        return seargeSig;
    }
}