package cn.com.yitong.core.session.vo;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import cn.com.yitong.core.session.Session;
import cn.com.yitong.tools.vo.EnumerationWrapper;

/**
 * @author lc3@yitong.com.cn
 */
public class HttpSessionWrapper implements HttpSession {

    private Session session;

    public HttpSessionWrapper(Session session) {
        this.session = session;
    }

    @Override
    public long getCreationTime() {
        return session.getCreateTime().getTime();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessTime().getTime();
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        session.setTimeout(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return (int) session.getTimeout();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    @Override
    public Object getValue(String name) {
        return session.getAttribute(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return new EnumerationWrapper(session.getAttributeKeys().iterator());
    }

    @Override
    public String[] getValueNames() {
        return session.getAttributeKeys().toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        session.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {
        session.invalidate();
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
