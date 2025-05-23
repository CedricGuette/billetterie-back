package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
