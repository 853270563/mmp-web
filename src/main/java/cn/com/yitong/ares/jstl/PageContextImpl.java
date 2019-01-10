package cn.com.yitong.ares.jstl;

import java.util.Map;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 *
 * <p>
 * This is a "dummy" implementation of PageContext whose only purpose is to
 * serve the attribute getter/setter API's.
 * 
 * @author Nathan Abramson - Art Technology Group
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: kchung $
 **/

public class PageContextImpl implements PageContext {
	// -------------------------------------
	// Properties
	// -------------------------------------

	// -------------------------------------
	// Member variables
	// -------------------------------------

	Map mParam;
	Map mHeader;
	IBusinessContext ctx;

	// -------------------------------------
	/**
	 *
	 * Constructor
	 **/
	public PageContextImpl(IBusinessContext ctx) {
		this.ctx = ctx;
		this.mParam = ctx.getParamMap();
		//this.mHeader = ctx.getHeadMap();
	}

	// -------------------------------------
	public void release() {
	}

	// -------------------------------------
	public void setAttribute(String name, Object attribute) {
		mParam.put(name, attribute);
	}

	// -------------------------------------
	public void setAttribute(String name, Object attribute, int scope) {
		switch (scope) {
		case PARAM_SCOPE:
			mParam.put(name, attribute);
			break;
		case HEADER_SCOPE:
			mHeader.put(name, attribute);
			break;
		case SESSION_SCOPE:
			ctx.saveSessionObject(name, attribute);
			break;
		default:
			throw new IllegalArgumentException("Bad scope " + scope);
		}
	}

	// -------------------------------------
	public Object getAttribute(String name) {
		return mParam.get(name);
	}

	// -------------------------------------
	public Object getAttribute(String name, int scope) {
		switch (scope) {
		case PARAM_SCOPE:
			return mParam.get(name);
		case HEADER_SCOPE:
			return mHeader.get(name);
		case SESSION_SCOPE:
			return ctx.getSessionObject(name);
		default:
			throw new IllegalArgumentException("Bad scope " + scope);
		}
	}

	// -------------------------------------
	@Override
	public Object findAttribute(String name) {
		if (mParam.containsKey(name)) {
			return mParam.get(name);
		} else if (mHeader.containsKey(name)) {
			return mHeader.get(name);
		} else if (hasSession(name)) {
			return ctx.getSessionObject(name);
		} else {
			return null;
		}
	}

	// -------------------------------------
	public void removeAttribute(String name) {
		if (mParam.containsKey(name)) {
			mParam.remove(name);
		} else if (mHeader.containsKey(name)) {
			mHeader.remove(name);
		} else if (hasSession(name)) {
			ctx.removeSession(name);
		}
	}

	private boolean hasSession(String name) {
		return false && null != ctx.getSessionObject(name);
	}

	// -------------------------------------
	public void removeAttribute(String name, int scope) {
		switch (scope) {
		case PARAM_SCOPE:
			mParam.remove(name);
			break;
		case HEADER_SCOPE:
			mHeader.remove(name);
			break;
		case SESSION_SCOPE:
			ctx.removeSession(name);
			break;
		default:
			throw new IllegalArgumentException("Bad scope " + scope);
		}
	}

	// -------------------------------------
	public int getAttributesScope(String name) {
		if (mParam.containsKey(name)) {
			return PARAM_SCOPE;
		} else if (mHeader.containsKey(name)) {
			return HEADER_SCOPE;
		} else if (hasSession(name)) {
			return SESSION_SCOPE;
		} else {
			return 0;
		}
	}

}
