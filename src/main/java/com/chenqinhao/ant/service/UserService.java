package com.chenqinhao.ant.service;

import java.util.List;
import java.util.Set;

import com.chenqinhao.ant.domain.Permission;
import com.chenqinhao.ant.domain.Role;
import com.chenqinhao.ant.domain.User;

public interface UserService {
	User findUser(Integer id);

	int saveUser(User user);
	//查询角色集合
	Set<Role> findRoles(String username);
	
	List<User> findUsers();

	Set<Permission> findPermissions(String username);

	User findByUsername(String username);
	
	Set<String> findRoleNames(String username);
	
	Set<String> findPermissionNames(String username);
}
