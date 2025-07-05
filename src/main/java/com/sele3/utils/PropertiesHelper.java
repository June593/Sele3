package com.sele3.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {
    /*
     * Properties mode
     */
    private static Properties profile;

    private static Properties initPropsForName(String propFileName) {
        try (InputStream inputStream = PropertiesHelper.class.getClassLoader()
                .getResourceAsStream(propFileName)) {

            if (inputStream != null) {
                Properties prop = new Properties();
                prop.load(inputStream);
                return prop;
            } else {
                System.err.println(propFileName + " not found !");
                return null;
            }
        } catch (Exception e) {
            logger.info("Exception: " + e);
        }
        return null;
    }

    private static void initProps() {
        profile = initPropsForName("profiles/" + InputParameters.ENV + ".properties");
    }

    public static String getPropValue(String key) {
        return getPropValue(key, null);
    }

    public static String getPropValue(String key, String defaultValue) {
        initProps();

        if (System.getProperty(key) != null) {
            return System.getProperty(key);
        }

        if (profile != null && profile.containsKey(key)) {
            return profile.getProperty(key);
        }
        return defaultValue;
    }

    private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);
}
