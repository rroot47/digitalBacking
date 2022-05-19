package bk.services;

import bk.entities.BankAccount;
import bk.entities.Client;
import bk.entities.CurrentAccount;
import bk.entities.SavingAccount;
import bk.exceptions.BalanceNotSufficentException;
import bk.exceptions.BankAccountNotFoundException;
import bk.exceptions.ClientNotFoundException;

import java.util.List;

public interface BankAccountService {
    Client saveClient(Client client);
    CurrentAccount saveCurrentBankAccount (double initialSolde, double overDraft, Long clientId) throws ClientNotFoundException;
    SavingAccount saveSavingBankAccount (double initialSolde, double rate, Long clientId) throws ClientNotFoundException;
    List<Client>  listClient();
    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debitAccount(String accountId, double amount, String desc) throws BankAccountNotFoundException, BalanceNotSufficentException;
    void retraitAccount(String accountId, double amount, String desc) throws BankAccountNotFoundException, BalanceNotSufficentException;
    void transfer(String accountIdSource, String accountIdDest, double amount) throws BankAccountNotFoundException, BalanceNotSufficentException;
}
