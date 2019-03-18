package com.shuai.test.okhttp.cache.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.shuai.test.okhttp.cache.CacheResult;
import com.shuai.test.okhttp.cache.NetConstants;
import com.shuai.test.okhttp.cache.exceptions.ServerException;
import com.shuai.test.okhttp.cache.exceptions.TransformException;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

  private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];
  private static final String KEY_JSON_MESSAGE = "msg";
  private static final String KEY_JSON_DATA = "data";
  private Type mType;
  private Feature[] features;
  private String codeKey;


  FastJsonResponseBodyConverter(Type type, String codeKey,
                                Feature... features) {
      mType = type;
      this.features = features;
      this.codeKey  = codeKey;
  }

  private boolean isCacheResultType(Type type){
    return (type instanceof ParameterizedType)
            &&((ParameterizedType)type).getRawType() == CacheResult.class;
  }

  @Override
  public T convert(ResponseBody value) throws IOException {
      if (value == null) {
        return null;
      }
      String response = value.string();
      try {
        //无需解析code,直接返回
        if (NetConstants.NO_CODE.equalsIgnoreCase(codeKey)) {
          if (mType == String.class) {
            return (T) response;
          } else {
            //如果返回类型是CacheResult<Data>，Data才是真正的数据类型
            if (!isCacheResultType(mType)) {
              return JSON.parseObject(response, mType, FastJsonConfigProvider.getParseConfig(), JSON.DEFAULT_PARSER_FEATURE,
                      features != null ? features : EMPTY_SERIALIZER_FEATURES);
            } else {
              //拿到CacheResult<Data>的真正的数据类型
              Type realType = ((ParameterizedType) mType).getActualTypeArguments()[0];
              Object data = JSON.parseObject(response, realType, FastJsonConfigProvider.getParseConfig(), JSON.DEFAULT_PARSER_FEATURE,
                      features != null ? features : EMPTY_SERIALIZER_FEATURES);
              //isFromCache参数始终设为false，由RetrofitProxy去设置来自缓存还是网络，因为判断不了来源
              return (T) new CacheResult<>(false, data);
            }
          }
          //需要解析
        }else {
          JSONObject jsonObject = new JSONObject(response);
          int code = jsonObject.getInt(codeKey);
          if (code == 0) { //业务正常返回
            String json = jsonObject.getString(KEY_JSON_DATA);
            if (mType == String.class){
              return (T) json;
            }else {
              //如果返回类型是CacheResult<Data>，Data才是真正的数据类型
              if (!isCacheResultType(mType)) {
                return JSON.parseObject(json, mType, FastJsonConfigProvider.getParseConfig(), JSON.DEFAULT_PARSER_FEATURE,
                        features != null ? features : EMPTY_SERIALIZER_FEATURES);
              } else {
                //拿到CacheResult<Data>的真正的数据类型
                Type realType = ((ParameterizedType) mType).getActualTypeArguments()[0];
                Object data = JSON.parseObject(json, realType, FastJsonConfigProvider.getParseConfig(), JSON.DEFAULT_PARSER_FEATURE,
                        features != null ? features : EMPTY_SERIALIZER_FEATURES);
                //isFromCache参数始终设为false，由RetrofitProxy去设置来自缓存还是网络，因为判断不了来源
                return (T) new CacheResult<>(false, data);
              }
            }
          } else {  //业务出错
            String msg = null;
            if (jsonObject.has(KEY_JSON_MESSAGE)){
              msg = jsonObject.getString(KEY_JSON_MESSAGE);//兼容后台返回带 "message" 的 json
            }else {
              msg = jsonObject.getString(KEY_JSON_DATA);
            }
            throw new ServerException(code, msg);
          }
        }
      } catch (Throwable e) {
        if (e instanceof ServerException){
          throw (ServerException)e;//让订阅者拿到错误code和message
        }else {//其他异常
          throw new TransformException(e.getMessage(),e.getCause());
        }
      } finally {
        value.close();
      }
  }
}
