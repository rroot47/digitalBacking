package bk.services;

import bk.dao.AccountOperationRepository;
import bk.dao.BankAccountRepository;
import bk.dao.ClientRepository;
import bk.entities.*;
import bk.enums.OperationType;
import bk.exceptions.BalanceNotSufficentException;
import bk.exceptions.BankAccountNotFoundException;
import bk.exceptions.ClientNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
//@AllArgsConstructor
@Slf4j
public class BankAccountServiceImp implements BankAccountService{

    private ClientRepository clientRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;

   // Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public BankAccountServiceImp(ClientRepository clientRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository) {
        this.clientRepository = clientRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
    }


    @Override
    public Client saveClient(Client client) {
        log.info("Saving new Client");
        Client saveClient = clientRepository.save(client);
        return saveClient;
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialSolde, double overDraft, Long clientId) throws ClientNotFoundException {
        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null)
            throw  new ClientNotFoundException("Cleint not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialSolde);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setClient(client);
        CurrentAccount saveBankAccount = bankAccountRepository.save(currentAccount);
        return saveBankAccount;
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialSolde, double rate, Long clientId) throws ClientNotFoundException {
        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null)
            throw  new ClientNotFoundException("Cleint not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialSolde);
        savingAccount.setInterestRate(rate);
        savingAccount.setClient(client);
        SavingAccount saveBankAccount = bankAccountRepository.save(savingAccount);
        return saveBankAccount;
    }


    @Override
    public List<Client> listClient() {
        return clientRepository.findAll();
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount  bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        return bankAccount;
    }

    @Override
    public void debitAccount(String accountId, double amount, String desc) throws BankAccountNotFoundException, BalanceNotSufficentException {
        BankAccount  bankAccount = getBankAccount(accountId);
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDesc(desc);
        accountOperation.setOpDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void retraitAccount(String accountId, double amount, String desc) throws BankAccountNotFoundException, BalanceNotSufficentException {
        BankAccount  bankAccount = getBankAccount(accountId);
        if(bankAccount.getBalance() < amount)
            throw  new BalanceNotSufficentException("Balance not sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDesc(desc);
        accountOperation.setOpDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDest, double amount) throws BankAccountNotFoundException, BalanceNotSufficentException {
        debitAccount(accountIdSource, amount,"Transfer from "+accountIdDest);
        retraitAccount(accountIdDest, amount, "Transfer from "+ accountIdSource);
    }
}
