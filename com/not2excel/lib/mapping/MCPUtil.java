package com.not2excel.lib.mapping;

/**
 * @author Richmond Steele
 * @since 2/27/14
 * All rights Reserved
 * Please read included LICENSE file
 */
public class MCPUtil
{
    public static Mapped getMappedMap(MCPType type, String... unobf)
    {
        if (!MCPMappingParser.instance().isComplete() ||
            unobf.length < 1 ||
            unobf.length > 2)
        {
            return null;
        }
        switch (type)
        {
            case FIELD:
                if(unobf.length != 2)
                {
                    return null;
                }
                return retrieveField(unobf[0], unobf[1]);
            case METHOD:
                if(unobf.length != 2)
                {
                    return null;
                }
                return retrieveMethod(unobf[0], unobf[1]);
            case CLASS:
                return getMappedClass(unobf[0]);
            default:
                return getMappedClass(unobf[0]);
        }
    }
    
    private static MappedClass getMappedClass(String unobfClass)
    {
        return MCPMappingParser.instance().getMappedClassMap().get(unobfClass);
    }

    private static MappedField retrieveField(String unobfClass, String unobf)
    {
        MappedClass mappedClass = getMappedClass(unobfClass);
        if (mappedClass != null)
        {
            return mappedClass.mappedFields().get(unobf);
        }
        return null;
    }

    private static MappedMethod retrieveMethod(String unobfClass, String unobf)
    {
        MappedClass mappedClass = getMappedClass(unobfClass);
        if (mappedClass != null)
        {
            return mappedClass.mappedMethods().get(unobf);
        }
        return null;
    }
}
