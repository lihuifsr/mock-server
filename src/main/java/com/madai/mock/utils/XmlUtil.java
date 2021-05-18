package com.madai.mock.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

public class XmlUtil {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getResult(String xml) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Document document = DocumentHelper.parseText(xml);
            Element root = document.getRootElement();
            Iterator<Element> it = root.elementIterator();
            while (it.hasNext()) {
                Element element = it.next();
                map.put(element.getName(), element.getTextTrim());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void element2map(String body, Map<String, Object> map) {
        Element element = null;
        try {
            element = DocumentHelper.parseText(body).getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        element2map(element, map);
    }

    public static void element2map(Element elmt, Map<String, Object> map) {
        if (null == elmt) {
            return;
        }
        String name = convert(elmt.getName());
        if (elmt.isTextOnly()) {
            map.put(name, elmt.getText());
        } else {
            Map<String, Object> mapSub = new HashMap<>();
            List<Element> elements = (List<Element>) elmt.elements();
            for (Element elmtSub : elements) {
                element2map(elmtSub, mapSub);
            }
            Object first = map.get(name);
            if (null == first) {
                map.put(name, mapSub);
            } else {
                if (first instanceof List<?>) {
                    ((List) first).add(mapSub);
                } else {
                    List<Object> listSub = new ArrayList<Object>();
                    listSub.add(first);
                    listSub.add(mapSub);
                    map.put(name, listSub);
                }
            }
        }
    }

    private static String convert(String str) {
        if (str == null) {
            return str;
        }
        return str.replaceAll("-", "")
                .replaceAll(":", "");
    }
}
