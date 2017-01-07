package com.chenqinhao.ant.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.Sha256CredentialsMatcher;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.util.Factory;

public class Secure {
	public void config() {
		/*Realm realm = new JdbcRealm();
		DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);
		DefaultSessionManager sessionManager = (DefaultSessionManager) securityManager.getSessionManager();
		SessionDAO sessionDAO = null;
		sessionManager.setSessionDAO(sessionDAO);*/
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro/shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		
	}
	public void ini() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro/shiro.ini");
		DefaultSecurityManager securityManager = (DefaultSecurityManager) factory.getInstance();
		CredentialsMatcher sha256Matcher = new Sha256CredentialsMatcher();
		JdbcRealm myRealm = new MysqlJdbcRealm();
		myRealm.setCredentialsMatcher(sha256Matcher);
		DefaultSecurityManager sessionManager = (DefaultSecurityManager) securityManager.getSessionManager();
		SecurityUtils.setSecurityManager(securityManager);
	}
}
