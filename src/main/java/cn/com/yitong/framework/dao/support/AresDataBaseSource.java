package cn.com.yitong.framework.dao.support;

import cn.com.yitong.tools.codec.Hex;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.sm.PncDigest;
import com.alibaba.druid.pool.DruidDataSource;
//import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by winer on 2017-11-20.
 */
public class AresDataBaseSource extends DruidDataSource {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private final byte[] keys = { 1, 80, 120, 11, -11, 30, 89, 109, -103, 17, 67, 87, 46, 27, 68, 57 };
    @Override
    public void setUsername(String username) {
        if (StringUtil.isNotEmpty(username) && username.length() > 16) {
            logger.info("----ares data source setUsername start-------");
            try {
//				username = username.substring(0, 64);
                byte[] usernames = Hex.decode(username);
                byte[] out = PncDigest.getInstance().reqDecode(keys, usernames);
                username = new String(out);
            } catch (Exception e) {
                logger.warn("ares data source mm {} ,init error", username, e);
            }
        }
        super.setUsername(username);
    }

    @Override
    public void setPassword(String password) {
        if (StringUtil.isNotEmpty(password) && password.length() > 16) {
            logger.info("----ares data source setPassword start-------");
            try {
//				password = password.substring(0, 64);
                byte[] pwds = Hex.decode(password);
                byte[] out = PncDigest.getInstance().reqDecode(keys, pwds);
                password = new String(out);
            } catch (Exception e) {
                logger.warn("ares data source mm {} ,init error", password, e);
            }
        }
        super.setPassword(password);
    }
}
