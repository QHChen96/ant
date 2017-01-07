package com.chenqinhao.ant.dao;

import java.util.List;
import java.util.Set;

import com.chenqinhao.ant.domain.Permission;
import com.chenqinhao.ant.domain.Role;
import com.chenqinhao.ant.domain.User;

public interface UserDao {
	//查找
	User findById(Integer id);
	//保存
	void saveUser(User user);
	//全部用户
	List<User> findUsers();
	//查角色
	List<Role> findRoles(String username);
	//查权限
	List<Permission> findPermissions(String username);
	User findByUsername(String username);
}
