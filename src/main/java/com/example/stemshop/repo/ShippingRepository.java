package com.example.stemshop.repo;

import com.example.stemshop.domain.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<Shipping, Long> { }
