package com.binance.orderbook.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.binance.orderbook.core.models.OrderBookModel;

@Repository
public interface OrderBookRepostitory extends
		JpaRepository<OrderBookModel, Long> {

}
