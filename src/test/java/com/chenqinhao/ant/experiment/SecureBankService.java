package com.chenqinhao.ant.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenqinhao.ant.experiment.AccountTransaction.TransactionType;

public class SecureBankService implements BankService{
	private static final Logger log = LoggerFactory.getLogger(SecureBankService.class);
    private volatile boolean _isRunning;
    private final List<Account> _accounts;
    private Map<Long, Account> _accountsById;

    /**
     * Creates a new {@link SecureBankService} instance.
     */
    public SecureBankService() {
        _accounts = new ArrayList<Account>();
        _accountsById = new HashMap<Long, Account>();
    }

    /**
     * Starts this service
     */
    public void start() throws Exception {
        _isRunning = true;
        log.info("银行服务开始...");
    }

    /**
     * Stop this service
     */
    public void dispose() {
        log.info("银行服务停止...");
        _isRunning = false;

        synchronized (_accounts) {
            _accountsById.clear();
            _accounts.clear();
        }

        log.info("银行服务停止！");
    }

    /**
     * Internal utility method that validate the internal state of this service.
     */
    protected void assertServiceState() {
        if (!_isRunning) {
            throw new IllegalStateException("银行的服务没有开始");
        }
    }

    public int getAccountCount() {
        return _accounts.size();
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#createNewAccount(java.lang.String)
    */

    @RequiresPermissions("bankAccount:create")
    public long createNewAccount(String anOwnerName) {
        assertServiceState();
        log.info("创建新的账户给 " + anOwnerName);

        synchronized (_accounts) {
            Account account = new Account(anOwnerName);
            account.setCreatedBy(getCurrentUsername());
            _accounts.add(account);
            _accountsById.put(account.getId(), account);

            log.debug("创建新的账户: " + account);
            return account.getId();
        }
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#searchAccountIdsByOwner(java.lang.String)
    */

    public long[] searchAccountIdsByOwner(String anOwnerName) {
        assertServiceState();
        log.info("查找已经存在的银行账户为 " + anOwnerName);

        ArrayList<Account> matchAccounts = new ArrayList<Account>();
        synchronized (_accounts) {
            for (Account a : _accounts) {
                if (a.getOwnerName().toLowerCase().contains(anOwnerName.toLowerCase())) {
                    matchAccounts.add(a);
                }
            }
        }

        long[] accountIds = new long[matchAccounts.size()];
        int index = 0;
        for (Account a : matchAccounts) {
            accountIds[index++] = a.getId();
        }

        log.debug("找到 " + accountIds.length + " 相匹配的账户的名称 " + anOwnerName);
        return accountIds;
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#getOwnerOf(long)
    */

    @RequiresPermissions("bankAccount:read")
    public String getOwnerOf(long anAccountId) throws AccountNotFoundException {
        assertServiceState();
        log.info("获得银行账户的所有者 " + anAccountId);

        Account a = safellyRetrieveAccountForId(anAccountId);
        return a.getOwnerName();
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#getBalanceOf(long)
    */

    @RequiresPermissions("bankAccount:read")
    public double getBalanceOf(long anAccountId) throws AccountNotFoundException {
        assertServiceState();
        log.info("得到账户的余额 " + anAccountId);

        Account a = safellyRetrieveAccountForId(anAccountId);
        return a.getBalance();
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#depositInto(long, double)
    */

    @RequiresPermissions("bankAccount:operate")
    public double depositInto(long anAccountId, double anAmount) throws AccountNotFoundException, InactiveAccountException {
        assertServiceState();
        log.info("存钱到 " + anAmount + " 这个账户 " + anAccountId);

        try {
            Account a = safellyRetrieveAccountForId(anAccountId);
            AccountTransaction tx = AccountTransaction.createDepositTx(anAccountId, anAmount);
            tx.setCreatedBy(getCurrentUsername());
            log.debug("创建一个新的交易 " + tx);

            a.applyTransaction(tx);
            log.debug("新的账户余额 " + a.getId() + " 存款后 " + a.getBalance());

            return a.getBalance();

        } catch (NotEnoughFundsException nefe) {
            throw new IllegalStateException("应该从未发生过", nefe);
        }
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#withdrawFrom(long, double)
    */

    @RequiresPermissions("bankAccount:operate")
    public double withdrawFrom(long anAccountId, double anAmount) throws AccountNotFoundException, NotEnoughFundsException, InactiveAccountException {
        assertServiceState();
        log.info("取款 " + anAmount + " 从账户 " + anAccountId);

        Account a = safellyRetrieveAccountForId(anAccountId);
        AccountTransaction tx = AccountTransaction.createWithdrawalTx(anAccountId, anAmount);
        tx.setCreatedBy(getCurrentUsername());
        log.debug("创建一个新的交易 " + tx);

        a.applyTransaction(tx);
        log.debug("新的账户余额 " + a.getId() + " 取款后 " + a.getBalance());

        return a.getBalance();
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#getTxHistoryFor(long)
    */

    @RequiresPermissions("bankAccount:read")
    public TxLog[] getTxHistoryFor(long anAccountId) throws AccountNotFoundException {
        assertServiceState();
        log.info("获取账户交易 " + anAccountId);

        Account a = safellyRetrieveAccountForId(anAccountId);

        TxLog[] txs = new TxLog[a.getTransactions().size()];
        int index = 0;
        for (AccountTransaction tx : a.getTransactions()) {
            log.debug("查过交易 " + tx);

            if (TransactionType.DEPOSIT == tx.getType()) {
                txs[index++] = new TxLog(tx.getCreationDate(), tx.getAmount(), tx.getCreatedBy());
            } else {
                txs[index++] = new TxLog(tx.getCreationDate(), -1.0d * tx.getAmount(), tx.getCreatedBy());
            }
        }

        return txs;
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#closeAccount(long)
    */

    @RequiresPermissions("bankAccount:close")
    public double closeAccount(long anAccountId) throws AccountNotFoundException, InactiveAccountException {
        assertServiceState();
        log.info("截止账户 " + anAccountId);

        Account a = safellyRetrieveAccountForId(anAccountId);
        if (!a.isActive()) {
            throw new InactiveAccountException("这个账户 " + anAccountId + " 已经关闭");
        }

        try {
            AccountTransaction tx = AccountTransaction.createWithdrawalTx(a.getId(), a.getBalance());
            tx.setCreatedBy(getCurrentUsername());
            log.debug("创建一个新的交易  " + tx);
            a.applyTransaction(tx);
            a.setActive(false);

            log.debug("账户 " + a.getId() + " 现在是关闭的 " + tx.getAmount() + " 针对这个业主");
            return tx.getAmount();

        } catch (NotEnoughFundsException nefe) {
            throw new IllegalStateException("应该从来不发生", nefe);
        }
    }

    /* (non-Javadoc)
    * @see com.connectif.trilogy.root.security.BankService#isAccountActive(long)
    */

    @RequiresPermissions("bankAccount:read")
    public boolean isAccountActive(long anAccountId) throws AccountNotFoundException {
        assertServiceState();
        log.info("获取账户的活动状态 " + anAccountId);

        Account a = safellyRetrieveAccountForId(anAccountId);
        return a.isActive();
    }


    /**
     * Internal method that safelly (concurrency-wise) retrieves an account from the id passed in.
     *
     * @param anAccountId The identifier of the account to retrieve.
     * @return The account instance retrieved.
     * @throws AccountNotFoundException If no account is found for the provided identifier.
     */
    protected Account safellyRetrieveAccountForId(long anAccountId) throws AccountNotFoundException {
        Account account = null;
        synchronized (_accounts) {
            account = _accountsById.get(anAccountId);
        }

        if (account == null) {
            throw new AccountNotFoundException("没有找到ID为 " + anAccountId + " 的账户");
        }

        log.info("检查账户 " + account);
        return account;
    }

    /**
     * Internal utility method to retrieve the username of the current authenticated user.
     *
     * @return The name.
     */
    protected String getCurrentUsername() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null || subject.getPrincipal() == null || !subject.isAuthenticated()) {
            throw new IllegalStateException("无法检索当前验证的主题");
        }
        return SecurityUtils.getSubject().getPrincipal().toString();
    }
}
