package data_access;

import entity.Commodity;
import use_case.watchlist.WatchlistCommodityDataAccessInterface;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommodityDataAccessObject implements WatchlistCommodityDataAccessInterface {
    private static final URI CHART_ENDPOINT = URI.create("https://query1.finance.yahoo.com/v8/finance/chart/");
    private static final Pattern PRICE = Pattern.compile(
            "\"regularMarketPrice\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)(?=\\s*[,}])");
    private static final Pattern CURRENCY = Pattern.compile("\"currency\"\\s*:\\s*\"([A-Za-z]+)\"");

    private final HttpClient client;
    private final URI chartEndpoint;

    public CommodityDataAccessObject() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build(), CHART_ENDPOINT);
    }

    CommodityDataAccessObject(HttpClient client, URI chartEndpoint) {
        this.client = client;
        this.chartEndpoint = chartEndpoint;
    }

    @Override
    public Optional<Commodity> getCommodity(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            return Optional.empty();
        }
        String symbol = ticker.strip().toUpperCase(Locale.ROOT);
        String encoded = URLEncoder.encode(symbol, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(chartEndpoint.resolve(encoded + "?interval=1d&range=1d"))
                .header("User-Agent", "Mozilla/5.0")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                return Optional.empty();
            }
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Yahoo Finance returned HTTP " + response.statusCode());
            }
            Matcher price = PRICE.matcher(response.body());
            if (!price.find()) {
                return Optional.empty();
            }
            Matcher currency = CURRENCY.matcher(response.body());
            return Optional.of(new Commodity(symbol, Double.parseDouble(price.group(1)),
                    currency.find() ? currency.group(1) : ""));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not reach Yahoo Finance", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Yahoo Finance request interrupted", exception);
        }
    }
}
