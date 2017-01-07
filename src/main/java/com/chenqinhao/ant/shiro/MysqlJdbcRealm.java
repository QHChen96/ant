package com.chenqinhao.ant.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.chenqinhao.ant.domain.User;
import com.chenqinhao.ant.service.UserService;
import com.google.common.base.Strings;

public class MysqlJdbcRealm extends JdbcRealm{
	@Autowired
	private UserService userService;

	//认证
	@SuppressWarnings("null")
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
		String username = String.valueOf(usernamePasswordToken.getUsername());
		User user = userService.findByUsername(username);
		AuthenticationInfo authenticationInfo = null;
		if(null == user){
			String password = new String(usernamePasswordToken.getPassword());
			if(password.equals(user.getPassword())){
				authenticationInfo = new SimpleAuthenticationInfo(username, password, getName());
			}
		}
		return authenticationInfo;
	}
	
	//授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		String username = (String)principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo authorizationInfo = null;
		if(!Strings.isNullOrEmpty(username)){
			authorizationInfo = new SimpleAuthorizationInfo();
			authorizationInfo.setRoles(userService.findRoleNames(username));
			authorizationInfo.setStringPermissions(userService.findPermissionNames(username));
		}
		return authorizationInfo;
	}
	
}
