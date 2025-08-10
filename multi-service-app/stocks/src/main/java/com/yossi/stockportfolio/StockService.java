package com.yossi.stockportfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private NinjaApiService ninjaApiService;

    // Get stock value by ID
    @Transactional
    public Map<String, Object> getStockValue(String id) {
        try {
            // Retrieve the stock by ID
            Stock stock = stockRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

            // Get stock price from NinjaApiService
            Map<String, Object> stockPriceData = ninjaApiService.getStockPrice(stock.getSymbol());

            // Calculate stock value based on price and number of shares
            double stockValue = (double) stockPriceData.get("price") * stock.getNumberOfShares();
            stockPriceData.put("stock value", stockValue);

            return stockPriceData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get stock value", e);
        }
    }

    // Get the total portfolio value
    @Transactional
    public Map<String, Object> getPortfolioValue() {
        double totalValue = 0;
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            try {
                // Get stock price for each stock
                Map<String, Object> stockPriceData = ninjaApiService.getStockPrice(stock.getSymbol());
                totalValue += (double) stockPriceData.get("price") * stock.getNumberOfShares();
            } catch (Exception e) {
                throw new RuntimeException("Failed to calculate portfolio value", e);
            }
        }

        // Return the total portfolio value along with the current date
        return Map.of(
            "date", java.time.LocalDate.now().toString(),
            "portfolio value", totalValue
        );
    }

    @Transactional
    public Stock addOrUpdateStock(Stock stock) {
        // Lock the stock if it exists to avoid conflicting updates
        if (stock.getId() != null && stockRepository.existsById(stock.getId())) {
            stockRepository.findByIdWithLock(stock.getId()).orElseThrow(() -> new RuntimeException("Stock not found"));
        }
        return stockRepository.save(stock);
    }

    @Transactional
    public void deleteStock(String id) {
        // Lock the stock before deletion to prevent conflicts
        stockRepository.findByIdWithLock(id).orElseThrow(() -> new RuntimeException("Stock not found"));
        stockRepository.deleteById(id);
    }
}
