package com.madai.mock.crypt;

public interface CryptHandler {

    /**
     * 解密，解签
     * 由于接收到的请求加密方式及签名方式千奇百怪，无法统一使用一种或几种标准的方式，
     * 因此提供标准解密，解签接口，各个不同的service需要具体实现自己的解密方法。
     * 例 ：oridata = "a=1&b=2&c=YDSFHISFNSKMFN"
     *      先拆分字符串 获取 YDSFHISFNSKMFN
     *      再根据具体解密方法如RSA 解密出明文 {"s":"1","g":"3"}返回，具体返回结果根据service自身的要求决定
     * @param oriData
     * @return
     */
    String decode(String oriData);

    /**
     * 加密，加签
     * 每个服务加密以及签名的方式有各有不同，
     * 这里提供标准接口，
     * 需要用到此功能的服务需要具体实现这个方法
     * 例：{"data":"123","code":"0"}
     *     根据自己的规则生成签名 sf5sdf7s88f
     *     再插入到返回结果中{"data":"123","code":"0","sign":"sf5sdf7s88f"}返回
     * @param oriData
     * @return
     */
    String encode(String oriData);

}
