Mock-server使用说明

1.将配置文件放入测试机指定目录 /opt/mock-server/config/ ，配置文件必须以.json结尾，具体参考/opt/mock-server/config/mo.json
2.配置文件格式：
例：[{ "request" : { "uri" : "/gzdsf/ProcessServlet", "type":"xml" ,"crypt":"baofoo","parse":"baofoo"}, "response" : { "body" : "Hello  World !!! ${req.uri}" ,"crypt":"baofoo"} }]

request:
1.uri 必填
2.type 不必填  ，xml 表示请求体会以xml形式解析，josn 表示请求体会以json形式解析， 不填表示以query形式解析(即a=1&b=2&c=3)
3.crypt 不必填 ，若需要加解密和签名验签操作，可以实现CryptHandler接口，具体可参考BaoFooCryptHandler, 然后在Config.java中指定key 
cryptMap.put("baofoo", new BaoFooCryptHandler());
之后在配置文件中配上key ，"crypt":"baofoo" 即可
4.parse 不必填 ，当type中的json，xml，query不能满足需要时，可以自己实现ReqParser接口，具体参考BaofooReqParser.java, 然后同样配置key 
parseMap.put("baofoo", new BaofooReqParser());
"parse":"baofoo"

response:
1.body 必填 ，支持freemarker模版，具体语法请参考freemarker文档
2.crypt 不必填，和request中的crypt一样的配置


## 参考文档：http://www.mock-server.com/
## FTL参考手册：http://freemarker.foofun.cn/ref_builtins_string.html#ref_builtin_ends_with

## 项目启动：MockApplication类，运行MAIN函数即可
## 1、开发指南
 ### 在src/main/resources下的mock-config目录，建立一个.JSON文件
  格式参考：
  [
      {
          "request": {
              "uri": “/hello”
          },
          "response": {
              "body": "Hello  World"
          }
      }
  ]
  
响应的正文，支持FTL语法
请求里面的数据，在响应里面都可以用FTL语法来引用
EG 第三方请求的入参为{
    "a":{
        "b":""c"
        }
    },则在响应里面可以通过${a.b}来访问 得到值c

## 2、基本组件
#### 2.1、request：定义请求相关信息
uri:请求地址 ;实际请求地址为http://ip:9875/+uri
type:相当于content-type，可选值为json,xml

REQUEST的配置如下
    "request": {
      "uri": "/bqs",
      "type":"json"
    }

#### 2.2、response：定义响应相关信息
响应配置如下，其中body里面就是想要返回的内容
"response": {
      "body": ""
}


#### 加解密
当入参或者响应需要加解密的时候，这个时候需要应用加解密组件
1、加解密组件
实现CryptHandler，其中接口里面的decode方法代表对入参的解密，encode方法代表对响应的加密，然后在类上加注解@MockResource(name = "加密组件名字")，再在REQUEST,RESPONSE中 通过属性crypt来配置

#### 缓存
当你需要缓存某个请求的入参时，在后面的请求中作为响应时，可以使用缓存
2、缓存组件
分为读缓存和写缓存

读缓存的配置
"cache":{
    	"r":"缓存KEY"
   }
   
写缓存的配置
   "cache":{
       	"w":"缓存KEY"
      }

自定义缓存
 实现CacheHandler，且在缓存实现类上加@MockResource(name = "your_cache_key") 
 

#### 3、回调
 通过callback来配置
 
 配置示例
 callback:{
   "url": "回调地址",
   "type": "POST",
   "parse": "json",
   "params": {
   }
 }

### 4、部署
登录Jenkis

###MOCK常见实例
1、GET请求，请参考/get
2、POST CONTENT-TYPE 为application/json 请参考postwithjson
3、POST CONTENT-TYPE 为application/x-www-form-urlencoded 请参考postwithform


