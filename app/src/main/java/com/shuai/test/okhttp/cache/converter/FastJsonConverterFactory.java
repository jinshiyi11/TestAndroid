package com.shuai.test.okhttp.cache.converter;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shuai.test.okhttp.cache.retrofit.Code;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * A {@linkplain Converter.Factory converter} which uses FastJson for JSON.
 * <p>
 * Because FastJson is so flexible in the types it supports, this converter assumes that it can
 * handle all types. If you are mixing JSON serialization with something else (such as protocol
 * buffers), you must {@linkplain Retrofit.Builder#addConverterFactory(Converter.Factory) add
 * this instance} last to allow the other converters a chance to see their types.
 */
public class FastJsonConverterFactory extends Converter.Factory {

  //private ParserConfig mParserConfig = ParserConfig.getGlobalInstance();
  //private int featureValues = JSON.DEFAULT_PARSER_FEATURE;
  private Feature[] features;

  private SerializeConfig serializeConfig;
  private SerializerFeature[] serializerFeatures;

  /**
   * Create an default instance for conversion. Encoding to JSON and
   * decoding from JSON (when no charset is specified by a header) will use UTF-8.
   * @return The instance of FastJsonConverterFactory
   */
  public static FastJsonConverterFactory create() {
    return new FastJsonConverterFactory();
  }

  private FastJsonConverterFactory() {
  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                          Retrofit retrofit) {
    return new FastJsonResponseBodyConverter<>(type,getCodeKey(annotations),features);
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                        Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    return new FastJsonRequestBodyConverter<>(serializeConfig, serializerFeatures);
  }

  private String getCodeKey(Annotation[] annotations){
    String codeKey = "error";
    if (annotations == null || annotations.length == 0) {
      return codeKey;
    }
    for (Annotation annotation :annotations){
      if (annotation instanceof Code){
        return codeKey = ((Code)annotation).value();
      }
    }
    return codeKey;
  }

  public Feature[] getParserFeatures() {
    return features;
  }

  public FastJsonConverterFactory setParserFeatures(Feature[] features) {
    this.features = features;
    return this;
  }

  public SerializeConfig getSerializeConfig() {
    return serializeConfig;
  }

  public FastJsonConverterFactory setSerializeConfig(SerializeConfig serializeConfig) {
    this.serializeConfig = serializeConfig;
    return this;
  }

  public SerializerFeature[] getSerializerFeatures() {
    return serializerFeatures;
  }

  public FastJsonConverterFactory setSerializerFeatures(SerializerFeature[] features) {
    this.serializerFeatures = features;
    return this;
  }
}
