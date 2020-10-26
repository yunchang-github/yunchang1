package com.weiziplus.springboot.utils;

import java.util.*;

public class ListUtil {

    /**
     *
     * @param list 需要处理的list
     * @param target name值
     * @param date 分隔日期
     * @param initMap
     * @param type
     * @return
     */
    public static List mergeList(List list,String target,List<String> date,Map initMap,String type){

        List<List<Map<String,Object>>> resultList = new ArrayList();

        Iterator it = list.iterator();
        // 存储中间变量name值
        String temp = "";

        List<Map<String,Object>> temList = new ArrayList(4);
        flag: while (it.hasNext()){
            List<String> dateListB = new ArrayList<>();
            dateListB.addAll(date);
            Map map = (Map)(it.next());
            String res = (String) map.get(target);
            if (temp.equals("")){
                temp = res;
            }
            if(temp.equals(res)){
                temList.add(map);
                if(temList.size()==4){
                    resultList.add(temList);
                    continue flag;
                }
            }else {
                for (Map<String,Object> temMap: temList) {
                    if (dateListB.contains(temMap.get(type))) {
                        dateListB.remove(temMap.get(type));
                    }
                }
                for (int index =0; index<dateListB.size();index++) {
                    initMap.put(type, dateListB.get(index));
                    initMap.put(target,res);
                    Map<String,Object> newTemMap = new HashMap<>(1);
                    newTemMap.putAll(initMap);
                    temList.add(newTemMap);
                    if(temList.size()==4){
                        resultList.add(temList);
                        break;
                    }
                }
                temList = new ArrayList<>();
                temp = res;
                temList.add(map);
            }

            if(!it.hasNext()) {
                List<String> dateListC = new ArrayList<>();
                dateListC.addAll(date);
                for (Map<String,Object> temMap: temList) {
                    if (dateListC.contains(temMap.get(type))) {
                        dateListC.remove(temMap.get(type));
                    }
                }
                if (temList.size() < 4) {
                    for (int index = 0; index<dateListC.size();index++) {
                        initMap.put(type, dateListC.get(index));
                        initMap.put(target,res);
                        Map<String,Object> newTemMap = new HashMap<>(1);
                        newTemMap.putAll(initMap);
                        temList.add(newTemMap);
                        if(temList.size()==4){
                            resultList.add(temList);
                            break;
                        }
                    }
                }
            }
        }
        return resultList;
    }
}
