package cn.com.yitong.modules.system.note.service;

import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.CLOB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.service.ICrudService;

import com.alibaba.druid.proxy.jdbc.ClobProxyImpl;

/**
 * @author zhanglong
 * @date 17/9/25
 */
@Service
public class AresNoteService {

    @Autowired
    ICrudService service;

    public Map<String, Object> loadNoteInfo(Map<String, Object> params) throws Exception {
        Map<String, Object> rst = new HashMap<String, Object>();
        Map<String, Object> aresNote = service.load("ARES_NOTE.loadAresNote", params);
        if (aresNote == null || aresNote.isEmpty()) {
            rst.put(AppConstants.MSG, "资讯公告：" + params.get("NOTE_ID") + "不存在！");
            rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
            return rst;
        }
        Object object = aresNote.get("NOTE_CONTENT");
        rst.putAll(aresNote);
        String string = clobToString(object);
        rst.put("NOTE_CONTENT", string);
        rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
        return rst;
    }

    public Map<String, Object> queryNoteList(Map<String, Object> params) throws Exception {
        Map<String, Object> rst = new HashMap<String, Object>();
        int pageNo = Integer.parseInt((String)params.get("PAGE_NO"));
        int pageSize = Integer.parseInt((String)params.get("PAGE_SIZE"));

        int firstIndex = (pageNo - 1) * pageSize;
        int lastIndex = pageNo * pageSize;

        params.put("firstIndex", firstIndex);
        params.put("lastIndex", lastIndex);

        List<Map<String, Object>> aresNoteList = service.findList("ARES_NOTE.loadAresNoteList", params);
        List<Map<String, Object>> noteList = new ArrayList<Map<String, Object>>();
        if(null != aresNoteList && aresNoteList.size() > 0) {
            for(Map<String, Object> map : aresNoteList) {
                map.put("NOTE_CONTENT", clobToString(map.get("NOTE_CONTENT")));
                map.put("CRT_DATE", map.get("CREATE_TIME"));
                noteList.add(map);
            }
        }
        if (null != aresNoteList && aresNoteList.size() == pageSize) {
            rst.put("NEXT_PAGE", String.valueOf(pageNo + 1));
        } else {
            rst.put("NEXT_PAGE", params.get("PAGE_NO"));
        }
        rst.put("PAGE_SIZE", params.get("PAGE_SIZE"));
        rst.put("LIST", noteList);
        return rst;
    }

        /**
         * 将clob类型转成string
         */
    private String clobToString(Object clob) throws Exception {
        if (clob == null) {
            return "";
        }
        Reader characterStream = null;
        BufferedReader br = null;
        try {
            if(clob instanceof ClobProxyImpl) {
                ClobProxyImpl impl = (ClobProxyImpl)clob;
                Clob tempClob = impl.getRawClob(); // 获取原生的这个 Clob
                characterStream = tempClob.getCharacterStream();
            } else if (clob instanceof CLOB) {
                CLOB local = (CLOB)clob;
                characterStream = local.getCharacterStream();
            } else if ("weblogic.jdbc.wrapper.Clob_oracle_sql_CLOB".equals(clob.getClass().getName())) {
            	Method method = clob.getClass().getMethod("getVendorObj",new Class[]{}); 
            	CLOB t = (CLOB)method.invoke(clob); 
            	characterStream = t.getCharacterStream();
            } else {
                return clob.toString();
            }
            br = new BufferedReader(characterStream);
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (characterStream != null) {
                characterStream.close();
            }
            if (br != null) {
                br.close();
            }
        }
    }
}
