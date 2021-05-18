package com.madai.mock.parse;

import java.util.Map;

public interface ReqParser {

    /**
     * 解析请求
     * mock-server目前提供了基本的三种解析方式 xml,json,query
     * 这三种方式根据request中的type来决定
     * 默认的方式对整个request body进行解析
     * 如果body比较复杂，可以自定义解析规则，实现这个接口
     *
     * 例：
     * a=1&b=2&c={"v":"3","x":"4"} body中不单单是某种形式，而是几种形式的嵌套
     * 此时自己实现解析方法
     * 最终解析成freemarker需要的参数
     * {
     *     "a":"1",
     *     "b":"2",
     *     "c":{
     *         "v":"3",
     *         "x":"4"
     *     }
     * }
     *
     * @param reqBody
     * @param map
     */
    public void parse(String reqBody, Map map);
}
