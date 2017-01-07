package com.chenqinhao.ant.experiment;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.shiro.mgt.SecurityManager;

public class AccountTests {
	private static Logger logger = null;
	private static SecureBankService service;
	private Subject _subject;

	@BeforeClass
	public static void setUpClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		logger = Logger.getLogger(AccountTests.class.getSimpleName());
		Factory<SecurityManager> factory = new IniSecurityManagerFactory(
				"classpath:shiro/shiro.ini");
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		service = new SecureBankService();
		service.start();
	}

	// 作为用户登录，不能关闭账户
	protected void loginAsUser() {
		if (_subject == null) {
			_subject = SecurityUtils.getSubject();
		}
		// use dan to run as a normal user (which cannot close an account)
		_subject.login(new UsernamePasswordToken("dan", "123"));
	}

	// 作为超级用户登录，不能操作账户
	protected void loginAsSuperviser() {
		if (_subject == null) {
			_subject = SecurityUtils.getSubject();
		}
		// use sally to run as a superviser (which cannot operate an account)
		_subject.login(new UsernamePasswordToken("sally", "1234"));
	}
	@Test
	public void testCreateAccount() throws Exception {
		loginAsUser();
		createAndValidateAccountFor("张三");
	}

	protected long createAndValidateAccountFor(String anOwner) throws Exception {
		long createdId = service.createNewAccount(anOwner);
		assertAccount(anOwner, true, 0.0d, 0, createdId);
		return createdId;
	}

	public static void assertAccount(String eOwnerName, boolean eIsActive,
			double eBalance, int eTxLogCount, long actualAccountId)
			throws Exception {
		Assert.assertEquals(eOwnerName, service.getOwnerOf(actualAccountId));
		Assert.assertEquals(eIsActive, service.isAccountActive(actualAccountId));
		Assert.assertEquals(eBalance, service.getBalanceOf(actualAccountId));
		Assert.assertEquals(eTxLogCount,
				service.getTxHistoryFor(actualAccountId).length);
	}
	@Test
	public void testDepositInto_singleTx() throws Exception {
		loginAsUser();
		long accountId = createAndValidateAccountFor("李四");
		makeDepositAndValidateAccount(accountId, 250.00d, "李四");
	}
protected double makeDepositAndValidateAccount(long anAccountId,
   double anAmount, String eOwnerName) throws Exception {
  double previousBalance = service.getBalanceOf(anAccountId);
  int previousTxCount = service.getTxHistoryFor(anAccountId).length;
  double newBalance = service.depositInto(anAccountId, anAmount);
  Assert.assertEquals(previousBalance + anAmount, newBalance);
  assertAccount(eOwnerName, true, newBalance, 1 + previousTxCount,
    anAccountId);
  return newBalance;
 }
@Test
public void testDepositInto_multiTxs() throws Exception {
	loginAsUser();
	long accountId = createAndValidateAccountFor("王五");
	makeDepositAndValidateAccount(accountId, 50.00d, "王五");
	makeDepositAndValidateAccount(accountId, 300.00d, "王五");
	makeDepositAndValidateAccount(accountId, 85.00d, "王五");
	assertAccount("王五", true, 435.00d, 3, accountId);
}
@Test(expected = NotEnoughFundsException.class)
public void testWithdrawFrom_emptyAccount() throws Exception {
	loginAsUser();
	long accountId = createAndValidateAccountFor("贾六");
	service.withdrawFrom(accountId, 100.00d);
}
@Test(expected = NotEnoughFundsException.class)
public void testWithdrawFrom_notEnoughFunds() throws Exception {
	loginAsUser();
	long accountId = createAndValidateAccountFor("周七");
	makeDepositAndValidateAccount(accountId, 50.00d, "周七");
	service.withdrawFrom(accountId, 100.00d);
}
@Test
public void testWithdrawFrom_singleTx() throws Exception {
	loginAsUser();
	long accountId = createAndValidateAccountFor("国八");
	makeDepositAndValidateAccount(accountId, 500.00d, "国八");
	makeWithdrawalAndValidateAccount(accountId, 100.00d, "国八");
	assertAccount("国八", true, 400.00d, 2, accountId);
}
private void makeWithdrawalAndValidateAccount(long accountId, double d,
		String string) {
	// TODO Auto-generated method stub
	
}

@Test
public void testWithdrawFrom_manyTxs() throws Exception {
	loginAsUser();
	long accountId = createAndValidateAccountFor("Zoe Smith");
	makeDepositAndValidateAccount(accountId, 500.00d, "Zoe Smith");
	makeWithdrawalAndValidateAccount(accountId, 100.00d, "Zoe Smith");
	makeWithdrawalAndValidateAccount(accountId, 75.00d, "Zoe Smith");
	makeWithdrawalAndValidateAccount(accountId, 125.00d, "Zoe Smith");
	assertAccount("Zoe Smith", true, 200.00d, 4, accountId);
}
@Test
public void testWithdrawFrom_upToZero() throws Exception {
	loginAsUser();
	long accountId = createAndValidateAccountFor("Zoe Smith");
	makeDepositAndValidateAccount(accountId, 500.00d, "Zoe Smith");
	makeWithdrawalAndValidateAccount(accountId, 500.00d, "Zoe Smith");
	assertAccount("Zoe Smith", true, 0.00d, 2, accountId);
}
}
