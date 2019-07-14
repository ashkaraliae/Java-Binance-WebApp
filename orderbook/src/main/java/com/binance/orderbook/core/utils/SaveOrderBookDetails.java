package com.binance.orderbook.core.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.orderbook.core.models.OrderBookModel;
import com.binance.orderbook.core.repositories.OrderBookRepostitory;

public class SaveOrderBookDetails {

	OrderBookRepostitory orderBookRepository = null;
	OrderBookModel orderBookModel = null;

	private static final String BIDS = "BIDS";
	private static final String ASKS = "ASKS";

	private long lastUpdateId;

	private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;

	public SaveOrderBookDetails(String symbol, HttpSession session) {
		initializeDepthCache(symbol);
		startDepthEventStreaming(symbol,session);
	}

	/**
	 * Initializes the depth cache by using the REST API.
	 */
	private void initializeDepthCache(String symbol) {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
		BinanceApiRestClient client = factory.newRestClient();
		OrderBook orderBook = client.getOrderBook(symbol.toUpperCase(), 10);

		this.depthCache = new HashMap<>();
		this.lastUpdateId = orderBook.getLastUpdateId();

		NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(
				Comparator.reverseOrder());
		for (OrderBookEntry ask : orderBook.getAsks()) {
			asks.put(new BigDecimal(ask.getPrice()),
					new BigDecimal(ask.getQty()));
		}
		depthCache.put(ASKS, asks);

		NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(
				Comparator.reverseOrder());
		for (OrderBookEntry bid : orderBook.getBids()) {
			bids.put(new BigDecimal(bid.getPrice()),
					new BigDecimal(bid.getQty()));
		}
		depthCache.put(BIDS, bids);
	}

	/**
	 * Begins streaming of depth events.
	 */
	int i= 1;
	private void startDepthEventStreaming(String symbol, HttpSession session) {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
		BinanceApiWebSocketClient client = factory.newWebSocketClient();
		client.onDepthEvent(
				symbol.toLowerCase(),
				response -> {
					if (response.getUpdateId() > lastUpdateId) {
						orderBookModel = new OrderBookModel();
						//System.out.println(response);
						lastUpdateId = response.getUpdateId();
						updateOrderBook(getAsks(), response.getAsks());
						updateOrderBook(getBids(), response.getBids());
						orderBookModel.setSymbol(symbol);
						orderBookModel.setUpdateId(lastUpdateId);
						orderBookModel
								.setASK((NavigableMap<BigDecimal, BigDecimal>) depthCache
										.get("ASKS"));
						orderBookModel
								.setBID((NavigableMap<BigDecimal, BigDecimal>) depthCache
										.get("BIDS"));
						session.setAttribute("model", orderBookModel);
						System.out.println(orderBookModel);
						
					}

				});
	}
private void save(OrderBookModel orderBookModel){
	if(i==1){
		System.out.println("-----save +"+i);
		orderBookRepository.save(orderBookModel);
		orderBookRepository.flush();
	}
	i++;
}
	/**
	 * Updates an order book (bids or asks) with a delta received from the
	 * server.
	 *
	 * Whenever the qty specified is ZERO, it means the price should was removed
	 * from the order book.
	 */
	private void updateOrderBook(
			NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries,
			List<OrderBookEntry> orderBookDeltas) {
		for (OrderBookEntry orderBookDelta : orderBookDeltas) {
			BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
			BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
			if (qty.compareTo(BigDecimal.ZERO) == 0) {
				// qty=0 means remove this level
				lastOrderBookEntries.remove(price);
			} else {
				lastOrderBookEntries.put(price, qty);
			}
		}
	}

	public NavigableMap<BigDecimal, BigDecimal> getAsks() {
		return depthCache.get(ASKS);
	}

	public NavigableMap<BigDecimal, BigDecimal> getBids() {
		return depthCache.get(BIDS);
	}

	/**
	 * @return the best ask in the order book
	 */
	private Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
		return getAsks().lastEntry();
	}

	/**
	 * @return the best bid in the order book
	 */
	private Map.Entry<BigDecimal, BigDecimal> getBestBid() {
		return getBids().firstEntry();
	}

	/**
	 * @return a depth cache, containing two keys (ASKs and BIDs), and for each,
	 *         an ordered list of book entries.
	 */
	public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache() {
		return depthCache;
	}

	/**
	 * Prints the cached order book / depth of a symbol as well as the best ask
	 * and bid price in the book.
	 */
	/*
	 * private void printDepthCache() { System.out.println(depthCache);
	 * depthCache.forEach((k,v) -> { System.out.println(k+" : "+v); });
	 * //System.out.println("ASKS:"); //getAsks().entrySet().forEach(entry ->
	 * System.out.println(toDepthCacheEntryString(entry)));
	 * //System.out.println("BIDS:"); //getBids().entrySet().forEach(entry ->
	 * System.out.println(toDepthCacheEntryString(entry)));
	 * //System.out.println("BEST ASK: " +
	 * toDepthCacheEntryString(getBestAsk())); //System.out.println("BEST BID: "
	 * + toDepthCacheEntryString(getBestBid())); }
	 */

	/**
	 * Pretty prints an order book entry in the format "price / quantity".
	 */
	private static String toDepthCacheEntryString(
			Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
		return depthCacheEntry.getKey().toPlainString() + " / "
				+ depthCacheEntry.getValue();
	}

}
