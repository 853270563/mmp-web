package cn.com.yitong.modules.common.thirdServer.utils;

import cn.com.yitong.util.StringUtil;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供使用dom4j 解析XML 结构，返回map格式，中间支持集合 使用递归解析
 * @author zhanglong
 * @date 17/8/31
 */
public class Dom4jParserXmlUtils {

    /**
     *  使用dom4j解析XML 中elemet元素节点 支持集合和递归
     */
    public static Map<String, Object> parserElment(Element element, String nodeName) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Element> childrenList = element.elements();
        if(null != childrenList && childrenList.size() > 0) {
            int elementSize = 0;
            for(Element ele : childrenList) {
                elementSize = ele.elements().size();
                String nodeKey = getNodeKey(ele, nodeName);
                if(elementSize > 0) {
                    //list node
                    if(resultMap.containsKey(nodeKey)) {
                        Map<String, Object> tempMap = (Map<String, Object>)resultMap.get(nodeKey);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        list.add(tempMap);
                        Map<String, Object> secMap = new HashMap<String, Object>();
                        eleListMap(ele, secMap, nodeName);
                        list.add(secMap);
                        resultMap.put(nodeKey, list);
                    }else {
                        Map<String, Object> tempMap = new HashMap<String, Object>();
                        eleListMap(ele, tempMap, nodeName);
                        resultMap.put(nodeKey, tempMap);
                    }
                }else {
                    // single node
                    resultMap.put(nodeKey, getValue(ele.getText()));
                }
            }
        }
        return resultMap;
    }

    private static void eleListMap(Element element, Map<String, Object> eleMap, String nodeName) {
        List<Element> elementList = element.elements();
        if(null != elementList && elementList.size() > 0) {
            int elementSize = 0;
            for(Element ele : elementList) {
                elementSize = ele.elements().size();
                String nodeKey = getNodeKey(ele, nodeName);
                if(elementSize > 0) {
                    if(eleMap.containsKey(nodeKey)) {
                        Map<String, Object> map = (Map<String, Object>)eleMap.get(nodeKey);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        list.add(map);
                        Map<String, Object> secMap = new HashMap<String, Object>();
                        eleListMap(ele, secMap, nodeName);
                        list.add(secMap);
                        eleMap.put(nodeKey, list);
                    }else {
                        Map<String, Object> map = new HashMap<String, Object>();
                        eleListMap(ele, map, nodeName);
                        eleMap.put(nodeKey, map);
                    }
                }else {
                    eleMap.put(nodeKey, getValue(ele.getText()));
                }
            }
        }
    }

    /**
     * 获取dom节点的元素的名称或者key值 可能是当前节点 也可能是节点的某个属性
     */
    private static String getNodeKey(Element ele, String nodeName) {
        if(StringUtil.isEmpty(nodeName)) {
            return getValue(ele.getName());
        }else {
            return getValue(ele.attribute(nodeName).getValue());
        }
    }

    private static String getValue(String value) {
        if(StringUtil.isNotEmpty(value)) {
            value = value.trim();
        }else {
            value = "";
        }
        return value;
    }
}
