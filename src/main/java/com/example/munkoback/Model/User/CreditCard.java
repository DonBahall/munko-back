package com.example.munkoback.Model.User;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private Integer userId;
    private String cardNumber;
    private String cardHolderName;
    private String expirationDate;
}
