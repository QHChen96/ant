package com.chenqinhao.ant.dto;

public class GenericRequestDTO {
	//加密类型
	private Integer encryptioType;
	//内容
	private String content;
	public Integer getEncryptioType() {
		return encryptioType;
	}
	public void setEncryptioType(Integer encryptioType) {
		this.encryptioType = encryptioType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
