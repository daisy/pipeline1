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
     * @param a_logger the logger to add the handlers to.
     * @param a_loggingPropertyFileName filename of property file.
     */
    public static void addHandlers(MessageLogger a_logger, String a_loggingPropertyFileName) {
        // Check if specified
        if (a_loggingPropertyFileName == null) {
            System.err.println("No handlers added");
            return;
        }
        
        // Read logging properties file
        Properties _properties = new Properties();
        try {
            _properties.load(ClassLoader.getSystemResourceAsStream(a_loggingPropertyFileName));
        } catch (IOException e) {
            System.err.println("ioexception, no handlers added");
        }
        
        // Compile regex pattern
        Pattern _pattern = Pattern.compile("(.*)\\.formatter");
        
        // Find all formatters
        for (Iterator _iter = _properties.keySet().iterator(); _iter.hasNext(); ) {
            String _propertyName = (String)_iter.next();
            Matcher _matcher = _pattern.matcher(_propertyName);
            if (_matcher.matches()) {
                String _handlerName = _matcher.group(1);
                String _filePattern = _properties.getProperty(_handlerName + ".pattern");
                if (_filePattern == null) {
                    addConsoleHandler(a_logger, _handlerName, _properties);
                } else {
                    addFileHandler(a_logger, _handlerName, _properties);
                }
            }
        }
    }
    
    /**
     * Creates a formatter specified by class name.
     * @param a_className a class name
     * @return a <code>Formatter</code>
     * @see java.util.logging.Formatter
     */
    private static Formatter createFormatter(String a_className) {
        Formatter _formatter = null;
        try {
            Class _class = Class.forName(a_className);
            _formatter = (Formatter)_class.newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
        return _formatter;
    }
    
    /**
     * Adds a console handler to the logger.
     * @param a_logger
     * @param a_handlerName
     * @param a_properties
     */
    private static void addConsoleHandler(MessageLogger a_logger, String a_handlerName, Properties a_properties) {
        Formatter _formatter = createFormatter(a_properties.getProperty(a_handlerName + ".formatter"));
        if (_formatter == null) {
            return;
        }
        
        String _levelName = a_properties.getProperty(a_handlerName + ".level", "ALL");
        Level _level;
        try {
            _level = Level.parse(_levelName);
        } catch (IllegalArgumentException e) {
            _level = Level.ALL;
        }
        a_logger.addConsoleHandler(_formatter, _level);
    }
    
    private static void addFileHandler(MessageLogger a_logger, String a_handlerName, Properties a_properties) {
        Formatter _formatter = createFormatter(a_properties.getProperty(a_handlerName + ".formatter"));
        if (_formatter == null) {
            return;
        }
        
        String _levelName = a_properties.getProperty(a_handlerName + ".level", "ALL");
        Level _level;
        try {
            _level = Level.parse(_levelName);
        } catch (IllegalArgumentException e) {
            _level = Level.ALL;
        }
        
        String _filePattern = a_properties.getProperty(a_handlerName + ".pattern");
        
        int _limit;
        try {
        	_limit = Integer.parseInt(a_properties.getProperty(a_handlerName + ".limit", "0"));
        } catch (NumberFormatException e) {
            _limit = 0;
        }
        
        int _count;
        try {
        	_count = Integer.parseInt(a_properties.getProperty(a_handlerName + ".count", "0"));
        } catch (NumberFormatException e) {
            _count = 0;
        }
        
        boolean _append = Boolean.valueOf(a_properties.getProperty(a_handlerName + ".append", "false")).booleanValue();
        
        a_logger.addFileHandler(_formatter, _level, _filePattern, _limit, _count, _append);
    }
    
}
