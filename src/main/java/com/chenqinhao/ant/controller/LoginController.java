package com.chenqinhao.ant.controller;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chenqinhao.ant.dto.LoginRequestDTO;
import com.chenqinhao.ant.dto.LoginResponseDTO;
import com.chenqinhao.ant.service.UserService;

@RestController
public class LoginController {
	private static final Logger logger = Logger.getLogger(LoginController.class);
	@Autowired
	private UserService userService;
	
	@Autowired
	private SecurityManager securityManager;
	
	@PostMapping("login")
	public LoginResponseDTO login(LoginRequestDTO loginRequest) {
		//获得主体
		Subject subject = SecurityUtils.getSubject();
		//检查主体是否认证成功
		if(!subject.isAuthenticated()){
			//创建令牌
			UsernamePasswordToken token = new UsernamePasswordToken(loginRequest.getUsername(), loginRequest.getPassword());
			//是否记住密码
			token.setRememberMe(loginRequest.isRememberMe());
			//工具
			SecurityUtils.setSecurityManager(securityManager);
			//登录
			try {
				subject.login(token);
			} catch (UnknownAccountException e) {
				logger.info("There is no user with username of " + token.getPrincipal());
				return LoginResponseDTO.validatedFail();
			} catch (IncorrectCredentialsException e) {
				logger.info("Password for account " + token.getPrincipal() + " was incorrect");
				return LoginResponseDTO.validatedFail();
			} catch (LockedAccountException e) {
				logger.info("The account for username " + token.getPrincipal() + " is locked.");
				return LoginResponseDTO.locked();
			} catch (Exception e) {
				logger.info("UnknownException with username of " + token.getPrincipal());
				return LoginResponseDTO.unknown();
			}
			Session session = subject.getSession();
			session.setAttribute("loginUser", subject.getPrincipal());
		}
		return LoginResponseDTO.success();
	}
	
}
