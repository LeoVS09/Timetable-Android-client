package edu.bonch.leovs09.timetable.REST;

import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
/**
 * Created by BoolenTF on 14.08.2016.
 */
public class RestRequest {
    final static String serverUrl = "http://timetable.cfapps.io";

    String prefix;
    String url;
    // statusCode - поле предназначеное для получения статуса запроса.
    String statusCode;
    RestTemplate restTemplate;

    public RestRequest() {
        try {
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            Log.i("rest","start RestRequest");
        }catch (Exception e){
            Log.e("RestRequest creating",e.getMessage(),e);
        }
    }
    public String getStatus(){
        return statusCode;
    }
    //В квчестве запроса отправляем объект, получает ответ- объект. Возвращает объект.
    public <T> T PostObjGetObj(Object request, Class<T> responseType) throws Exception{
        return restTemplate.postForObject(url,request,responseType);
    }
    //В квчестве запроса отправляет объект, получаем ответ- объект. Изменяет поле(statusCode). Возврашает объект.
    public <T> T PostObjGetObjAndStatus(Object request, Class<T> responseType) throws Exception{
        ResponseEntity <T> rEntity= restTemplate.postForEntity(url,request,responseType);
        statusCode = rEntity.getStatusCode().toString();
        return rEntity.getBody();
    }

    //В квчестве запроса отправляет объект и заголовок, получаем ответ- объект. Изменяет поле(statusCode). Возврашает объект.
    public <T> T PostObjGetObjAndStatus(Object request, Class<T> responseType, String token) throws Exception{
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization",token);
        //MultiValueMap<Object,HttpHeaders> EntityAndHeader= new LinkedMultiValueMap<Object,HttpHeaders>();
        //EntityAndHeader.set(request,requestHeaders);
        HttpEntity requestEntity = new HttpEntity<Object>(request,requestHeaders);
        ResponseEntity <T> rEntity = restTemplate.exchange(url, HttpMethod.POST,requestEntity,responseType);
        statusCode = rEntity.getStatusCode().toString();
        return rEntity.getBody();
    }

    //В квчестве запроса отправляет объект и заголовок, получаем ответ- объект. Изменяет поле(statusCode). Возврашает объект.
    public <T> T GetObjAndStatus(Class<T> responseType, String token) throws Exception{
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization",token);
        //MultiValueMap<Object,HttpHeaders> EntityAndHeader= new LinkedMultiValueMap<Object,HttpHeaders>();
        //EntityAndHeader.set(request,requestHeaders);
        HttpEntity requestEntity = new HttpEntity(requestHeaders);
        ResponseEntity <T> rEntity = restTemplate.exchange(url, HttpMethod.GET,requestEntity,responseType);
        statusCode=rEntity.getStatusCode().toString();
        return rEntity.getBody();
    }


    //Запрашивает некоторый объект. Изменяет статус код. Возвращает объект.
    public <T> T GetObjAndStatus(Class<T> responseType) throws Exception{
        Log.i("RestRequest","Start GetObjAndSatus");

        ResponseEntity<T> rEntity = restTemplate.getForEntity(url,responseType);

        Log.i("RestRequest","Finish GetObjAndSatus");
        statusCode = rEntity.getStatusCode().toString();
        return rEntity.getBody();
    }
    // Определяет каталог.
    public RestRequest in(String... prefixes){

        String prefix = "";
        for(String pr : prefixes)
            prefix += pr + "/";
        prefix = prefix.substring(0,prefix.length()-1);
       // Log.i("lol","lol");
        this.prefix = prefix;
        this.url = serverUrl + "/" + prefix;
        Log.i("info",url);
        return this;
    }

}