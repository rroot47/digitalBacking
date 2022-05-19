package bk.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long di;
    private String nameClient;
    private  String emailClient;
    @OneToMany(mappedBy = "client")
    private List<BankAccount> bankAccounts;
}
