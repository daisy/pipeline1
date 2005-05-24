/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.dmfc.logging;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for adding handlers to a logger. The handlers are described
 * in a properties file.
 * @author Linus Ericson
 */
public class LoggingPropertiesReader {    
    
    /**
     * Adds a set of handlers descripbed in the specified file to the specified logger.
     * @param logger the logger to add the handlers to.
     * @param loggingPropertyFileName filename of property file.
     */
    public static void addHandlers(MessageLogger logger, String loggingPropertyFileName) {
        // Check if specified
        if (loggingPropertyFileName == null) {
            System.err.println("No handlers added");
            return;
        }
        
        // Read logging properties file
        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(loggingPropertyFileName));
        } catch (IOException e) {
            System.err.println("ioexception, no handlers added");
        }
        
        // Compile regex pattern
        Pattern pattern = Pattern.compile("(.*)\\.formatter");
        
        // Find all formatters
        for (Iterator it = properties.keySet().iterator(); it.hasNext(); ) {
            String propertyName = (String)it.next();
            Matcher matcher = pattern.matcher(propertyName);
            if (matcher.matches()) {
                String handlerName = matcher.group(1);
                String filePattern = properties.getProperty(handlerName + ".pattern");
                if (filePattern == null) {
                    addConsoleHandler(logger, handlerName, properties);
                } else {
                    addFileHandler(logger, handlerName, properties);
                }
            }
        }
    }
    
    /**
     * Creates a formatter specified by class name.
     * @param className a class name
     * @return a <code>Formatter</code>
     * @see java.util.logging.Formatter
     */
    private static Formatter createFormatter(String className) {
        Formatter formatter = null;
        try {
            Class cls = Class.forName(className);
            formatter = (Formatter)cls.newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
        return formatter;
    }
    
    /**
     * Adds a console handler to the logger.
     * @param logger
     * @param handlerName
     * @param properties
     */
    private static void addConsoleHandler(MessageLogger logger, String handlerName, Properties properties) {
        Formatter formatter = createFormatter(properties.getProperty(handlerName + ".formatter"));
        if (formatter == null) {
            return;
        }
        
        String levelName = properties.getProperty(handlerName + ".level", "ALL");
        Level level;
        try {
            level = Level.parse(levelName);
        } catch (IllegalArgumentException e) {
            level = Level.ALL;
        }
        logger.addConsoleHandler(formatter, level);
    }
    
    private static void addFileHandler(MessageLogger logger, String handlerName, Properties properties) {
        Formatter formatter = createFormatter(properties.getProperty(handlerName + ".formatter"));
        if (formatter == null) {
            return;
        }
        
        String levelName = properties.getProperty(handlerName + ".level", "ALL");
        Level level;
        try {
            level = Level.parse(levelName);
        } catch (IllegalArgumentException e) {
            level = Level.ALL;
        }
        
        String filePattern = properties.getProperty(handlerName + ".pattern");
        
        int limit;
        try {
        	limit = Integer.parseInt(properties.getProperty(handlerName + ".limit", "0"));
        } catch (NumberFormatException e) {
            limit = 0;
        }
        
        int count;
        try {
        	count = Integer.parseInt(properties.getProperty(handlerName + ".count", "0"));
        } catch (NumberFormatException e) {
            count = 0;
        }
        
        boolean append = Boolean.valueOf(properties.getProperty(handlerName + ".append", "false")).booleanValue();
        
        logger.addFileHandler(formatter, level, filePattern, limit, count, append);
    }
    
}
