package com.chenqinhao.ant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class LoginResponseDTO {
	private static final String VALIDATED_FAIL = "用户名或密码错误";
	private static final String VALIDATED_SUCCESS = "认证成功";
	private static final String ACCOUNT_IS_LOCKED = "账户被锁定";
	private static final String UNKNOWN_EXCEPTION = "未知异常";
	private boolean success;
	private String msg;
	@JsonInclude(Include.NON_NULL)
	private Object data;
	
	public LoginResponseDTO() {}
	public LoginResponseDTO(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
		this.data = null;
	}
	public LoginResponseDTO(boolean success, String msg, Object data) {
		super();
		this.success = success;
		this.msg = msg;
		this.data = data;
	}
	/**
	 * 认证失败
	 * @return
	 */
	public static LoginResponseDTO validatedFail() {
		return new LoginResponseDTO(false, VALIDATED_FAIL);
	}
	/**
	 * 登录成功
	 * @return
	 */
	public static LoginResponseDTO success() {
		return new LoginResponseDTO(true, VALIDATED_SUCCESS);
	}
	/**
	 * 账户锁定
	 * @return
	 */
	public static LoginResponseDTO locked() {
		return new LoginResponseDTO(true, ACCOUNT_IS_LOCKED);
	}
	/**
	 * 未知异常
	 * @return
	 */
	public static LoginResponseDTO unknown() {
		return new LoginResponseDTO(true, UNKNOWN_EXCEPTION);
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
