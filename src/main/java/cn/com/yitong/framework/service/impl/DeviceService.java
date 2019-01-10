package cn.com.yitong.framework.service.impl;

import cn.com.yitong.framework.dao.IbatisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备管理服务
 * Created by QianJH on 2015/1/4.
 */
@Service
public class DeviceService {
    @Autowired
    @Qualifier("ibatisDao")
    private IbatisDao dao;

    private static final String DEVICE_ID = "DEVICE_ID";
    private static final String UDID = "UDID";
    private static final String APP_ID = "APP_ID";
    private static final String USER_ID = "USER_ID";
    private static final String APP_NAME = "APP_NAME";

    /**
     * 查询udid设备上appId应用是否绑定用户userId
     * @param <T>
     * @param udid 设备唯一标识
     * @param appId 应用编号
     * @param userId 用户编号
     * @return
     */
    public <T> Map<String, Object> bindQuery(String udid, String appId, String userId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(UDID, udid);

        //设备信息
        List<Map<String, Object>> devices = dao.findList("DEVICE.query", param);
        if(devices == null || devices.size() != 1) {
            return null;
        }
        Map<String, Object> device = devices.get(0);
        param.put(DEVICE_ID, device.get(DEVICE_ID));

        //设备绑定记录
        List<Map<String, Object>> rels = dao.findList("DEVICE_BIND.query", param);

        //从所有绑定记录中取出指定应用与用户的记录
        Map<String, Object> bind = null;
        for(Map<String, Object> rel : rels) {
            if(rel.get(APP_ID).equals(appId) && rel.get(USER_ID).equals(userId)) {
                bind = rel;
            }
        }
        //如果没有绑定指定应用与用户，返回
        if(bind == null) {
            return null;
        }

        //设备绑定的应用
        List<Map<String, Object>> apps = distinctByAppId(rels);
        device.put("APP_LIST", apps);

        return device;
    }

    /**
     * 根据应用编号将设备绑定记录去重
     * @param rels
     * @return
     */
    private List<Map<String, Object>> distinctByAppId(List<Map<String, Object>> rels) {
        //根据应用编号去重
        Map<String, Object> map = new HashMap<String, Object>();
        for(Map<String, Object> rel : rels) {
            map.put(String.valueOf(rel.get(APP_ID)), rel.get(APP_NAME));
        }

        List<Map<String, Object>> binds = new ArrayList<Map<String, Object>>();
        for(String key : map.keySet()) {
            Map<String, Object> bind = new HashMap<String, Object>();
            bind.put(APP_ID, key);
            bind.put(APP_NAME, map.get(key));
            binds.add(bind);
        }
        return binds;
    }

    /**
     * 设备绑定
     * @param param
     */
    public void bind(Map<String, Object> param) throws Exception {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        //如果不存在设备，新增设备信息
        queryParam.put(UDID, param.get(UDID));
        Map<String, Object>  device = dao.load("DEVICE.query", queryParam);
        if(device == null || device.isEmpty()) {
            param.put(DEVICE_ID, dao.load("DEVICE.seq", null));
            dao.insert("DEVICE.insert", param);
        } else {
            param.put(DEVICE_ID, device.get(DEVICE_ID));
        }

        //如果不存在绑定记录，新增绑定
        List<Map<String, Object>> deviceBinds = dao.findList("DEVICE_BIND.query", param);
        if(deviceBinds != null && !deviceBinds.isEmpty()) {
            throw new Exception("重复绑定.");
        }
        param.put("BIND_ID", dao.load("DEVICE_BIND.seq", null));
        dao.insert("DEVICE_BIND.insert", param);
    }

    /**
     * 添加黑/白名单
     * @param param
     * @throws Exception
     */
    public void addMark(Map<String, Object> param) throws Exception{
        Map<String, Object> device = dao.load("DEVICE.query", param);
        if(device == null || device.isEmpty()) {
            throw new Exception("设备不存在");
        }
        param.put(DEVICE_ID, device.get(DEVICE_ID));
        Map<String, Object> mark = dao.load("DEVICE_MARK.query", param);
        if(mark != null && !mark.isEmpty()) {
            throw  new Exception("已存在名单中");
        }
        param.put("MARK_ID", dao.load("DEVICE_MARK.seq", null));
        dao.insert("DEVICE_MARK.insert", param);
    }

    /**
     * 查询设备标记
     * @param param
     * @return
     */
    public List<Map<String, Object>> markQuery(Map<String, Object> param) throws Exception {
        Map<String, Object> device = dao.load("DEVICE.query", param);
        if(device == null || device.isEmpty()) {
            throw new Exception("设备不存在");
        }
        param.put(DEVICE_ID, device.get(DEVICE_ID));
        return dao.findList("DEVICE_MARK.query", param);
    }

    /**
     * 设备访问
     * @param param
     */
    public void access(Map<String, Object> param) throws Exception {
        Map<String, Object> device = dao.load("DEVICE.query", param);
        if(device == null || device.isEmpty()) {
            throw new Exception("设备尚未绑定");
        }
        param.put(DEVICE_ID, device.get(DEVICE_ID));
        param.put("LOG_ID", dao.load("DEVICE_LOG.seq", null));
        dao.insert("DEVICE_LOG.insert", param);
    }

    /**
     * 更新设备信息
     * @param param
     */
    public void update(Map<String, Object> param) throws Exception {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put(UDID, param.get(UDID));
        Map<String, Object> device = dao.load("DEVICE.query", queryParam);
        if(device == null || device.isEmpty()) {
            throw new Exception("设备尚未绑定");
        }
        param.put(DEVICE_ID, device.get(DEVICE_ID));

        dao.update("DEVICE.updateById", param);
    }

}
