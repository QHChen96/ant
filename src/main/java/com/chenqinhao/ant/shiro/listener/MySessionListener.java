package com.chenqinhao.ant.shiro.listener;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

public class MySessionListener implements SessionListener{
	private static Logger logger = Logger.getLogger(MySessionListener.class);
	/**
	 * 启动
	 */
	@Override
	public void onStart(Session session) {
		logger.info("Session onStart ...");
	}

	/**
	 * 停止
	 */
	@Override
	public void onStop(Session session) {
		logger.info("Session onStop ...");
	}

	/**
	 * 超时
	 */
	@Override
	public void onExpiration(Session session) {
		logger.info("Session onExpiration ...");
	}

}
