package com.shuai.test.okhttp.cache.converter;

import com.alibaba.fastjson.parser.ParserConfig;

/**
 * 将fastJson的转换配置抽取出来
 */
public class FastJsonConfigProvider {

    private static ParserConfig config;

    public static ParserConfig getParseConfig(){
        if (config == null){
            synchronized (FastJsonConfigProvider.class){
                if (config == null){
                    config = loadParseConfig();
                }
            }
        }
        return config;
    }

    private static ParserConfig loadParseConfig() {
        return ParserConfig.getGlobalInstance();
    }

}
