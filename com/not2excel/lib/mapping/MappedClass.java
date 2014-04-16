package com.not2excel.lib.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Richmond Steele on 2/26/14 at 2:52 PM.
 * All Rights Reserved unless alternate license provided.
 */
public class MappedClass extends Mapped
{
    private final String                    classLoc;
    private final String                    fullMap;
    private       Map<String, MappedField>  mappedFields;
    private       Map<String, MappedMethod> mappedMethods;

    public MappedClass(String notch, String unobf, String fullMap)
    {
        super(notch, unobf, fullMap);
        setUnobf(unobf);
        int begin = fullMap.indexOf(notch) + notch.length() + 1;
        this.classLoc = fullMap.substring(begin, fullMap.lastIndexOf(unobf)).replace("/", ".");
        this.fullMap = fullMap;
        this.mappedFields = new HashMap<>();
        this.mappedMethods = new HashMap<>();
    }

    public String getClassLoc()
    {
        return classLoc;
    }

    public String getFullMap()
    {
        return fullMap;
    }

    public Map<String, MappedField> mappedFields()
    {
        return this.mappedFields;
    }

    public Map<String, MappedMethod> mappedMethods()
    {
        return this.mappedMethods;
    }

    public String getFullClassLoc()
    {
        return classLoc + getUnobf();
    }
}
