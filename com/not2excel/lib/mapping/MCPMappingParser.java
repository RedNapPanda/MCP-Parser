package com.not2excel.lib.mapping;

import com.not2excel.lib.io.FileUtil;
import com.not2excel.lib.io.InputStreamManager;
import com.not2excel.lib.logging.PrefixedLogger;
import com.not2excel.lib.objects.TimeManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Richmond Steele on 2/26/14 at 3:33 PM.
 * All Rights Reserved unless alternate license provided.
 */
public class MCPMappingParser
{
	private final InputStream               joinedSrg;
	private final InputStream               methodsCsv;
	private final InputStream               fieldsCsv;
	private       InputStreamManager        inputStreamManager;
	private       Map<String, MappedClass>  mappedClassMap;
	private       Map<String, List<String>> tempFieldMap;
	private       Map<String, List<String>> tempMethodMap;
	private       PrefixedLogger            logger;
	private       TimeManager               time;
	private boolean isComplete = false;

	private static volatile MCPMappingParser instance;

	public static MCPMappingParser instance()
	{
		if (instance == null)
		{
			synchronized (MCPMappingParser.class)
			{
				if (instance == null)
				{
					instance = new MCPMappingParser();
				}
			}
		}
		return instance;
	}

	public MCPMappingParser()
	{
		this.mappedClassMap = new HashMap<>();
		this.tempFieldMap = new HashMap<>();
		this.tempMethodMap = new HashMap<>();
		this.logger = new PrefixedLogger("MCPMP");
		this.time = new TimeManager();
		this.joinedSrg = FileUtil.getResourceInputStream("/assets/mappings/joined.srg");
		this.methodsCsv = FileUtil.getResourceInputStream("/assets/mappings/methods.csv");
		this.fieldsCsv = FileUtil.getResourceInputStream("/assets/mappings/fields.csv");
		this.inputStreamManager = new InputStreamManager();
	}

	public void parseMappings()
	{
	    /*
	    runs the parsing in another thread since the line count is that high
        most computers should be able to handle it on the main thread, but you know...toasters
        */
		if (isComplete)
		{
			return;
		}
		logger.log("Parsing started");
		time.reset();
		inputStreamManager.manageNewInputStream(joinedSrg);
		parseJoinedMappings();
		inputStreamManager.manageNewInputStream(fieldsCsv);
		parseFieldMappings();
		tempFieldMap.clear();
		inputStreamManager.manageNewInputStream(methodsCsv);
		parseMethodMappings();
		tempMethodMap.clear();
		isComplete = true;
		logger.log("Parsing completed");
		logger.log(time.currentTimePassed(TimeUnit.MILLISECONDS) + "ms");
	}

	private void parseJoinedMappings()
	{
		String s;
		try
		{
			while ((s = this.inputStreamManager.reader().readLine()) != null)
			{
				if (s.startsWith("CL"))
				{
					handleClassJoined(s, s.split(" "));
				}
				else if (s.startsWith("FD"))
				{
					handleFieldJoined(s, s.split(" "));
				}
				else if (s.startsWith("MD"))
				{
					handleMethodJoined(s, s.split(" "));
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void handleClassJoined(String fullMap, String... mapping)
	{
        /*
        ../ => path
        **Numbers [x] are based on array length so last index == [-1]
        Strings are mapped like this:
        <CL:> <notch> <../unobf[-1]>
        Indices: 3 {4 if has #C/#S}
         */
		if (mapping.length < 3 ||
		    mapping.length > 4)
		{
			logger.log("Class mapping, %s, has too many indices", fullMap);
		}
//		if (mapping.length == 4)
//		{
//			if (mapping[3].equalsIgnoreCase("#S"))
//			{
//                logger.log("Class is server-sided only");
//				return;
//			}
//		}
		String notch = mapping[1];
		String unobf = mapping[2].split("/")[mapping[2].split("/").length - 1];
		mappedClassMap.put(unobf, new MappedClass(notch, unobf, fullMap));
//        logger.log("Class: %s, %s", unobf, notch);
	}

	private void handleFieldJoined(String fullMap, String... mapping)
	{
        /*
        ./ => path
        **Numbers [x] are based on array length so last index == [-1]
        Strings are mapped like this:
        <FD:> <notch-class/notch> <../unobf-class[-2]/searge[-1]> < /#C/#S>
        Indices: 3 {4 if has #C/#S}
         */
		if (mapping.length < 3 ||
		    mapping.length > 4)
		{
			logger.log("Field mapping, %s, has too many indices", fullMap);
		}
//		if (mapping.length == 4)
//		{
//			if (mapping[3].equalsIgnoreCase("#S"))
//			{
//                logger.log("Class is server-sided only");
//				return;
//			}
//		}
		String notch = mapping[1].split("/")[mapping[1].split("/").length - 1];
		String unobfClass = mapping[2].split("/")[mapping[2].split("/").length - 2];
		String searge = mapping[2].split("/")[mapping[2].split("/").length - 1];
		MappedClass mappedClass = this.mappedClassMap.get(unobfClass);
		if (mappedClass != null)
		{
			mappedClass.mappedFields().put(searge, new MappedField(notch, searge, fullMap));
//            logger.log("Field: %s, %s; Class: %s", searge, notch, unobfClass);
			List<String> temp = new ArrayList<>();
			if (tempFieldMap.containsKey(searge))
			{
				temp = tempFieldMap.get(searge);
			}
			temp.add(unobfClass);
			tempFieldMap.put(searge, temp);
		}
	}

	private void handleMethodJoined(String fullMap, String... mapping)
	{
        /*
        ./ => path
        **Numbers [x] are based on array length so last index == [-1]
        Strings are mapped like this:
        <MD:> <notch-class/notch> <notch-sig> <../unobf-class[-2]/searge[-1]> <searge-sig> < /#C/#S>
        Indices: 5 {6 if has #C/#S}
         */
		if (mapping.length < 5 ||
		    mapping.length > 6)
		{
			logger.log("Method mapping, %s, has too many indices", fullMap);
		}
//        if (mapping.length == 6)
//        {
//            if (mapping[5].equalsIgnoreCase("#S"))
//            {
//                logger.log("Class is server-sided only");
//                return;
//            }
//        }
		String notch = mapping[1].split("/")[1];
		String notchSig = mapping[2];
		String unobfClass = mapping[3].split("/")[mapping[3].split("/").length - 2];
		String searge = mapping[3].split("/")[mapping[3].split("/").length - 1];
		String seargeSig = mapping[4];
		MappedClass mappedClass = this.mappedClassMap.get(unobfClass);
		if (mappedClass != null)
		{
			mappedClass.mappedMethods().put(searge, new MappedMethod(notch, notchSig, searge, seargeSig, fullMap));
//            logger.log("Method: %s, %s, %s, %s; Class: %s", searge, seargeSig, notch, notchSig, unobfClass);
			List<String> temp = new ArrayList<>();
			if (tempMethodMap.containsKey(searge))
			{
				temp = tempMethodMap.get(searge);
			}
			temp.add(unobfClass);
			tempMethodMap.put(searge, temp);
		}
	}

	private void parseFieldMappings()
	{
		String s;
		try
		{
			while ((s = this.inputStreamManager.reader().readLine()) != null)
			{
				String[] split = s.split(",");
//                if(split[2].equalsIgnoreCase("1"))
//                {
//                    continue;
//                }
				String searge = split[0];
				if(!this.tempFieldMap.containsKey(searge))
				{
					continue;
				}
				for (String uClass : this.tempFieldMap.get(searge))
				{
					MappedClass mappedClass = this.mappedClassMap.get(uClass);
					if (mappedClass != null)
					{
						if (mappedClass.mappedFields().containsKey(searge))
						{
							MappedField mappedField = mappedClass.mappedFields().get(searge);
							mappedField.setUnobf(split[1]);
							mappedField.setDescription(split.length == 4 ? split[3] : "");
							mappedClass.mappedFields().remove(searge);
							mappedClass.mappedFields().put(split[1], mappedField);
						}
					}
				}
				tempFieldMap.remove(searge);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void parseMethodMappings()
	{
		String s;
		try
		{
			while ((s = this.inputStreamManager.reader().readLine()) != null)
			{
				String[] split = s.split(",");
//                if(split[2].equalsIgnoreCase("1"))
//                {
//                    continue;
//                }
				String searge = split[0];
				if(!this.tempMethodMap.containsKey(searge))
				{
					continue;
				}
				for (String uClass : this.tempMethodMap.get(searge))
				{
					MappedClass mappedClass = this.mappedClassMap.get(uClass);
					if (mappedClass != null)
					{
						if (mappedClass.mappedMethods().containsKey(searge))
						{
							MappedMethod mappedMethod = mappedClass.mappedMethods().get(searge);
							mappedMethod.setUnobf(split[1]);
							mappedMethod.setDescription(split.length == 4 ? split[3] : "");
							mappedClass.mappedMethods().remove(searge);
							mappedClass.mappedMethods().put(split[1], mappedMethod);
						}
					}
				}
				tempMethodMap.remove(searge);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public boolean isComplete()
	{
		return isComplete;
	}

	public Map<String, MappedClass> getMappedClassMap()
	{
		return mappedClassMap;
	}
}
