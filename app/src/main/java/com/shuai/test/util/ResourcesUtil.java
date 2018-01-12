package com.shuai.test.util;

import android.content.res.Resources;

/**
 *
 */
public class ResourcesUtil {

    public static int getSystemResourceId(String name, String defType, String defPackage){
        return Resources.getSystem().getIdentifier(name,defType,defPackage);
    }
}
