package bk.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CE")
@Data @NoArgsConstructor @AllArgsConstructor
public class SavingAccount extends  BankAccount{
    private  double interestRate; //taux d'interet
}
