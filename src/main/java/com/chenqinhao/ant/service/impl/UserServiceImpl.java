package com.chenqinhao.ant.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chenqinhao.ant.dao.UserDao;
import com.chenqinhao.ant.domain.Permission;
import com.chenqinhao.ant.domain.Role;
import com.chenqinhao.ant.domain.User;
import com.chenqinhao.ant.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService{
	@Autowired
	private UserDao userDao;
	
	@Override
	public User findUser(Integer id) {
		return userDao.findById(id);
	}

	@Override
	public int saveUser(User user) {
		userDao.saveUser(user);
		return 1;
	}


	@Override
	public List<User> findUsers() {
		return userDao.findUsers();
	}
	
	@Override
	public Set<Role> findRoles(String username) {
		Set<Role> roles = new HashSet<>(userDao.findRoles(username));
		return roles;
	}

	@Override
	public Set<Permission> findPermissions(String username) {
		Set<Permission> permissions = new HashSet<>(userDao.findPermissions(username));
		return permissions;
	}

	@Override
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public Set<String> findRoleNames(String username) {
		Set<Role> roles = findRoles(username);
		Set<String> roleNames = new HashSet<>();
		for(Role role : roles) {
			roleNames.add(role.getRole());
		}
		return roleNames;
	}
	
	@Override
	public Set<String> findPermissionNames(String username) {
		Set<Permission> permissions = findPermissions(username);
		Set<String> permissionNames = new HashSet<>();
		for(Permission permission : permissions) {
			permissionNames.add(permission.getPermission());
		}
		return permissionNames;
	}
}
