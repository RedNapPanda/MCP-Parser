package com.not2excel.lib.logging;

/**
 * Created by steelers on 2/26/14.
 */
public class PrefixedLogger implements ILogger<Object>
{
    private final String prefix;

    public PrefixedLogger()
    {
        this("");
    }

    public PrefixedLogger(final String prefix)
    {
       this.prefix = "[" + prefix + "] ";
    }

    public void log(String s, Object... objects)
    {
        this.log(String.format(s, objects));
    }

    @Override
    public void log(Object s)
    {
        System.out.println(this.prefix + s);
    }
}
