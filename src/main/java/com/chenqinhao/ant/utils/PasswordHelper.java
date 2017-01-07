package com.chenqinhao.ant.utils;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import com.chenqinhao.ant.domain.User;

public class PasswordHelper {
	//随机数生成
	private RandomNumberGenerator randomNumberGenerator;
	//算法
	private String algorithmName = "md5";
	//迭代次数
	private final int hashIterations = 2;
	
	public void encryptPassword(User user){
		user.setSalt(randomNumberGenerator.nextBytes().toHex());
		String newPassword = new SimpleHash(algorithmName, user.getPassword(), ByteSource.Util.bytes(user.getCredentialsSalt()), hashIterations).toHex();
		user.setPassword(newPassword);
	}
}
