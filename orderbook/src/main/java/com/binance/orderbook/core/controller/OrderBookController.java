package com.binance.orderbook.core.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binance.orderbook.core.models.OrderBookModel;
import com.binance.orderbook.core.repositories.OrderBookRepostitory;
import com.binance.orderbook.core.utils.SaveOrderBookDetails;

/**
 * Illustrates how to use the depth event stream to create a local cache of bids/asks for a symbol.
 */

@RestController
@RequestMapping("/OrderBook/")
public class OrderBookController {
	@Autowired
	OrderBookRepostitory orderBookRepository;
	OrderBookModel orderBookModel = null;
	
	@GetMapping("/start/{symbol}")
	public List<OrderBookModel> startCachingAndSave(@PathVariable String symbol,HttpSession session){
		new SaveOrderBookDetails(symbol,session);
//		orderBookModel.setSymbol(symbol);
//		orderBookModel.setUpdateId(123456879);
//		
//		orderBookModel.setASK();
//		orderBookModel.setBID((ArrayList) depthCache.get("BIDS"));
		return null;
		
	}
	@GetMapping("/getOrderBook/{symbol}")
	public List<OrderBookModel>getOrderBook(@PathVariable String symbol){
		return orderBookRepository.findAll();
		
	}
}
