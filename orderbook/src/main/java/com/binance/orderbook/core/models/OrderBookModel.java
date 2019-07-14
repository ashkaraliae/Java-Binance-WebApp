package com.binance.orderbook.core.models;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Cacheable
@Table(name = "ORDER_BOOK")
public class OrderBookModel {
	private long id;
	private String symbol;
	private long updateId;
	private Map<BigDecimal, BigDecimal> ASK;
	private Map<BigDecimal, BigDecimal> BID;

	public OrderBookModel() {

	}

	public OrderBookModel(String symbol, long updateId,
			Map<BigDecimal, BigDecimal> ASK, Map<BigDecimal, BigDecimal> BID) {
		this.symbol = symbol;
		this.updateId = updateId;
		this.ASK = ASK;
		this.BID = BID;

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "SYMBOL",nullable = true)
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Column(name = "UPDATE_ID",nullable = true)
	public long getUpdateId() {
		return updateId;
	}

	public void setUpdateId(long updateId) {
		this.updateId = updateId;
	}

	@ElementCollection
	@Column(name = "ASK",nullable = true)
	public Map<BigDecimal, BigDecimal> getASK() {
		return ASK;
	}

	public void setASK(Map<BigDecimal, BigDecimal> aSK) {
		this.ASK = aSK;
	}

	@ElementCollection
	@Column(name = "BID",nullable = true)
	public Map<BigDecimal, BigDecimal> getBID() {
		return BID;
	}

	public void setBID(Map<BigDecimal, BigDecimal> bID) {
		this.BID = bID;
	}

	@Override
	public String toString() {
		return "OrderBook [id=" + id + ", symbol=" + symbol + ", updateId="
				+ updateId + ", ASK=" + ASK + ", BID=" + BID + "]";
	}

}
