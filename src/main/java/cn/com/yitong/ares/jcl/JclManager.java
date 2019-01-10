package cn.com.yitong.ares.jcl;

public interface JclManager {
	

	public void registerBeanWithXmlAndAnnotion(String jarPath,String jarSpringXmlPackage,String... basePackages);
	
	public void destoryBeanWithXmlAndAnnotion(String jarPath,String jarSpringXmlPackage,String... basePackages);

}
