package com.chenqinhao.ant.shiro;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.chenqinhao.ant.domain.Permission;
import com.chenqinhao.ant.domain.Role;
import com.chenqinhao.ant.domain.User;
import com.chenqinhao.ant.service.UserService;

public class UserRealm extends AuthorizingRealm{
	
	@Autowired
	private UserService userService;
	
	//授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		String username = (String)principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		Set<Role> roles = userService.findRoles(username);
		Set<String> roleNames = new HashSet<>();
		for(Role role : roles) {
			roleNames.add(role.getRole());
		}
		authorizationInfo.setRoles(roleNames);
		Set<Permission> permissions = userService.findPermissions(username);
		Set<String> permissionNames = new HashSet<>();
		for(Permission permission : permissions) {
			permissionNames.add(permission.getPermission());
		}
		authorizationInfo.setStringPermissions(permissionNames);
		return authorizationInfo;
	}

	//认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		User user = userService.findByUsername(username);
		if(user == null) {
			throw new UnknownAccountException();
		}
		if(user.getLocked() == 0) {
			throw new LockedAccountException();
		}
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(),
				ByteSource.Util.bytes(user.getCredentialsSalt()), getName());		
		return authenticationInfo;
	}

}
