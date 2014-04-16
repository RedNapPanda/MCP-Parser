package com.not2excel.lib.mapping;

import com.not2excel.lib.logging.PrefixedLogger;

import java.util.Map.Entry;

/**
 * @author Richmond Steele
 * @since 2/28/14
 * All rights Reserved
 * Please read included LICENSE file
 */
public class TransformerMappings
{
    private static PrefixedLogger logger = new PrefixedLogger("MCP Dump");

    public static void dump(MCPType type, String... strings)
    {
        if (strings.length <= 2 &&
            strings.length >= 1)
        {
            switch (type)
            {
                case CLASS:
                    dumpClass(strings[0]);
                    break;
                case METHOD:
                    if (strings.length == 2)
                    {
                        dumpMethod(MCPMappingParser.instance().getMappedClassMap().get(strings[0]), strings[1]);
                    }
                    break;
                case FIELD:
                    if (strings.length == 2)
                    {
                        dumpMethod(MCPMappingParser.instance().getMappedClassMap().get(strings[0]), strings[1]);
                    }
                    break;
            }
        }
    }

    public static void dumpClass(String label)
    {
        for (Entry<String, MappedClass> entry : MCPMappingParser.instance().getMappedClassMap().entrySet())
        {
            if (entry.getKey().contains("$") &&
                entry.getKey().substring(0, entry.getKey().indexOf('$')).equalsIgnoreCase(label) ||
                entry.getKey().equalsIgnoreCase(label))
            {
                MappedClass mappedClass = entry.getValue();
                if (mappedClass != null)
                {
                    logger.log("CL: %s, %s, %s", mappedClass.getUnobf(), mappedClass.getNotch(),
                               mappedClass.getFullClassLoc());
                    for (MappedField mappedField : mappedClass.mappedFields().values())
                    {
                        logger.log("Dumping: %s", mappedField.getFullMap());
                        dumpField(mappedClass, mappedField.getUnobf());
                    }
                    for (MappedMethod mappedMethod : mappedClass.mappedMethods().values())
                    {
                        logger.log("Dumping: %s", mappedMethod.getFullMap());
                        dumpMethod(mappedClass, mappedMethod.getUnobf());
                    }
                }
            }
        }
    }

    public static void dumpMethod(MappedClass mappedClass, String label)
    {
        if (mappedClass != null)
        {
            MappedMethod mappedMethod = mappedClass.mappedMethods().get(label);
            if (mappedMethod != null)
            {
                logger.log("MD: %s, %s, %s, %s, %s, %s, %s",
                           mappedClass.getFullClassLoc(),
                           mappedClass.getNotch(),
                           mappedMethod.getUnobf(),
                           mappedMethod.getSearge(),
                           mappedMethod.getNotch(),
                           mappedMethod.getNotchSig(),
                           mappedMethod.getDescription());
            }
            else
            {
                logger.log("METHOD NOT FOUND");
            }
        }
    }

    public static void dumpField(MappedClass mappedClass, String label)
    {
        if (mappedClass != null)
        {
            MappedField mappedField = mappedClass.mappedFields().get(label);
            if (mappedField != null)
            {
                logger.log("FD: %s, %s, %s, %s, %s, %s",
                           mappedClass.getFullClassLoc(),
                           mappedClass.getNotch(),
                           mappedField.getUnobf(),
                           mappedField.getSearge(),
                           mappedField.getNotch(),
                           mappedField.getDescription());
            }
            else
            {
                logger.log("FIELD NOT FOUND");
            }
        }
    }
}
