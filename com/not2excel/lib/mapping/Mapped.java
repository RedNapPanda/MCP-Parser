package com.not2excel.lib.mapping;

import com.not2excel.echo.Echo;

/**
 * @author Richmond Steele
 * @since 2/27/14
 * All rights Reserved
 * Please read included LICENSE file
 */
public class Mapped
{
    private final String notch;
    private final String searge;
    private final String fullMap;

    private String unobf;
    private String description;

    public Mapped(String notch, String searge, String fullMap)
    {
        this.notch = notch;
        this.searge = searge;
        this.fullMap = fullMap;
    }

    public String getNotch()
    {
        return notch;
    }

    public String getSearge()
    {
        return searge;
    }

    public String getFullMap()
    {
        return fullMap;
    }

    public String getUnobf()
    {
        return unobf;
    }

    public void setUnobf(String unobf)
    {
        this.unobf = unobf;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Normal mc clients would need Notch returned
     * Forge does Searge renaming on runtime, so need to return Searge
     */
    public String getReflectionName()
    {
        return Echo.isDebug() ? getUnobf() : getSearge();
    }
}
