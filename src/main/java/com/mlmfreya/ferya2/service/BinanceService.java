package com.mlmfreya.ferya2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlmfreya.ferya2.model.CryptoSymbol;
import com.mlmfreya.ferya2.model.PriceData;
import com.mlmfreya.ferya2.repository.CryptoSymbolRepository;
import com.mlmfreya.ferya2.repository.PriceDataRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BinanceService {

    private final PriceDataRepository priceDataRepository;
    private final RestTemplate restTemplate;

    @Autowired
    private CryptoSymbolRepository cryptoSymbolRepository;

    public BinanceService(PriceDataRepository priceDataRepository, RestTemplate restTemplate) {
        this.priceDataRepository = priceDataRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void start() {
        fetchAndStorePrices();
    }

    @Scheduled(fixedRate = 20000)
    public void fetchAndStorePrices() {
        String url = "https://api.binance.us/api/v3/ticker/price";
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        List<Map<String, String>> prices = response.getBody();

        for(Map<String, String> priceInfo : prices) {
            String symbol = priceInfo.get("symbol");
            String price = priceInfo.get("price");

            // Save the price in the database
            PriceData priceData = new PriceData();
            priceData.setPrice(price);
            priceData.setSymbol(symbol);
            priceDataRepository.save(priceData);
        }
    }

    private void handleNewPrice(String event) {
        // Parse the event to get the symbol and price
        // You will need to implement this part
        String symbol = parseSymbol(event);
        String price = parsePrice(event);

        // Save the price in the database
        PriceData priceData = new PriceData();
        priceData.setPrice(price);
        priceData.setSymbol(symbol);
        priceDataRepository.save(priceData);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldData() {
        // Delete data older than 10 days
        priceDataRepository.deleteDataOlderThan(LocalDate.now().minusDays(10).atStartOfDay());
    }


    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String parseSymbol(String event) {
        try {
            JsonNode jsonNode = objectMapper.readTree(event);
            return jsonNode.get("s").asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse symbol", e);
        }
    }

    private String parsePrice(String event) {
        try {
            JsonNode jsonNode = objectMapper.readTree(event);
            return jsonNode.get("p").asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse price", e);
        }
    }


    public List<String> getTop100Cryptos() {
        List<CryptoSymbol> cryptoSymbols = cryptoSymbolRepository.findAll();

        if (cryptoSymbols.isEmpty()) {
            final String uri = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=1&sparkline=false";

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List> response = restTemplate.getForEntity(uri, List.class);

            List<Map<String, Object>> coins = response.getBody();


            for (Map<String, Object> coin : coins) {
                CryptoSymbol cryptoSymbol = new CryptoSymbol();
                cryptoSymbol.setSymbol((String) coin.get("symbol"));
                cryptoSymbol.setName((String) coin.get("name"));
                cryptoSymbol.setImage((String) coin.get("image"));
                cryptoSymbol.setMarket_cap(convertToDouble(coin.get("market_cap")));
                cryptoSymbol.setMarket_cap_rank((Integer) coin.get("market_cap_rank"));
                cryptoSymbol.setFully_diluted_valuation(convertToDouble(coin.get("fully_diluted_valuation")));
                cryptoSymbol.setTotal_volume(convertToDouble(coin.get("total_volume")));
                cryptoSymbol.setMarket_cap_change_24h(convertToDouble(coin.get("market_cap_change_24h")));
                cryptoSymbol.setMarket_cap_change_percentage_24h(convertToDouble(coin.get("market_cap_change_percentage_24h")));
                cryptoSymbol.setCirculating_supply(convertToDouble(coin.get("circulating_supply")));
                cryptoSymbol.setTotal_supply(convertToDouble(coin.get("total_supply")));
                cryptoSymbol.setMax_supply(convertToDouble(coin.get("max_supply")));

                Instant instant = Instant.parse((String) coin.get("ath_date"));
                cryptoSymbol.setAth_date(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));

                cryptoSymbol.setAtl(convertToDouble(coin.get("atl")));
                cryptoSymbol.setAtl_change_percentage(convertToDouble(coin.get("atl_change_percentage")));

                Instant instantAtl = Instant.parse((String) coin.get("atl_date"));
                cryptoSymbol.setAtl_date(LocalDateTime.ofInstant(instantAtl, ZoneId.systemDefault()));

                Instant instantUpdated = Instant.parse((String) coin.get("last_updated"));
                cryptoSymbol.setLast_updated(LocalDateTime.ofInstant(instantUpdated, ZoneId.systemDefault()));

                cryptoSymbolRepository.save(cryptoSymbol);
            }



            cryptoSymbols = cryptoSymbolRepository.findAll();
        }

        List<String> top100Cryptos = new ArrayList<>();
        for (CryptoSymbol cryptoSymbol : cryptoSymbols) {
            top100Cryptos.add(cryptoSymbol.getSymbol());
        }

        return top100Cryptos;
    }
    public Double convertToDouble(Object obj) {
        if (obj == null) {
            return 0.0;  // return a default value instead of null
        } else if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).doubleValue();
        } else if (obj instanceof Double) {
            return (Double) obj;
        } else {
            throw new IllegalArgumentException("Object cannot be converted to double");  // throw an exception if conversion is not possible
        }
    }


    public PriceData CryptoPrice(String symbol){
        return priceDataRepository.findTopBySymbolOrderByTimestampDesc(symbol);
    }


}
