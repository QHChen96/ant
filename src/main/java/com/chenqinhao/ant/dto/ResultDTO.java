package com.chenqinhao.ant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ResultDTO {
	public final static int SUCCESS = 1;
	public final static int FAIL = 0;
	private int status;
	private String msg;
	@JsonInclude(Include.NON_NULL)
	private Object data;
	
	public ResultDTO() {
		super();
	}
	public ResultDTO(int status, String msg, Object data) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
	public static ResultDTO OK(){
		return new ResultDTO(SUCCESS, "操作成功", null);
	}
	public static ResultDTO NO(){
		return new ResultDTO(FAIL, "操作失败", null);
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
