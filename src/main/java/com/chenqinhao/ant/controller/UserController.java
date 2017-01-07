package com.chenqinhao.ant.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chenqinhao.ant.domain.User;
import com.chenqinhao.ant.dto.ResultDTO;
import com.chenqinhao.ant.service.UserService;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/user/{id}")
	public User findUser(@PathVariable Integer id){
		return userService.findUser(id);
	}

	@RequiresRoles("admin")
	@GetMapping("/users")
	public List<User> findUsers(){
		return userService.findUsers();
	}
	@RequiresPermissions("user:create")
	@PostMapping("/user")
	public ResultDTO saveUser(User user){
		int status = userService.saveUser(user);
		if(status == 1){
			return ResultDTO.OK();
		}
		return ResultDTO.NO();
	}
}
