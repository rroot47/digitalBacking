package bk;

import bk.dao.AccountOperationRepository;
import bk.dao.BankAccountRepository;
import bk.dao.ClientRepository;
import bk.entities.AccountOperation;
import bk.entities.Client;
import bk.entities.CurrentAccount;
import bk.entities.SavingAccount;
import bk.enums.AccountStatus;
import bk.enums.OperationType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(DigitalBackingApplication.class, args);
    }

    @Bean
    CommandLineRunner start(ClientRepository clientRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){
        return  args -> {
            Stream.of("Boubou", "Fodie", "Samba").forEach(name->{
                Client client = new Client();
                client.setNameClient(name);
                client.setEmailClient(name+"@gmail.com");
                clientRepository.save(client);
            });
            clientRepository.findAll().forEach(client -> {
                CurrentAccount currentAccount= new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*9000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setAccountStatus(AccountStatus.CREATED);
                currentAccount.setClient(client);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount= new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*9000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setAccountStatus(AccountStatus.CREATED);
                savingAccount.setClient(client);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);
            });

            bankAccountRepository.findAll().forEach(account ->{
                for(int i=0; i<5; i++){
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOpDate(new Date());
                    accountOperation.setAmount(Math.random()*130);
                    accountOperation.setOperationType(Math.random()>0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    accountOperation.setBankAccount(account);
                    accountOperationRepository.save(accountOperation);
                }
            });
        };
    }
}
