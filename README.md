# Team Project

Please keep this up-to-date with information about your project throughout the term.

The readme should include information such as:
- a summary of what your application is all about
- a list of the user stories, along with who is responsible for each one
- information about the API(s) that your project uses 
- screenshots or animations demonstrating current functionality

By keeping this README up-to-date,
your team will find it easier to prepare for the final presentation
at the end of the term.

# Agriberg Terminal
## Team Information
### Team Members
- Iraoui Abdourahmane
- William Darko
- Dianna Chen Zhang
- Serdar Ermis
---
## Project Description
### Finance/Agriculture
Bloomberg-inspired desktop app allowing users to explore agricultural commodity prices, compare global crop production, and monitor weather conditions through interactive charts and dashboards.
---
## User Stories
### As a user, I want to view and compare historical price of agricultural commodities so that I can see the changes in their prices over time.
### As a trader, I want to be able to backtest my strategies over the historical data, so that I can come up with a winning strategy.
### As a user, I want to compare historical crop production across different countries and years so I can understand where commodities have been produced.
### As a user, I want to view historical weather data for different regions, including rainfall and temperature in order to identify conditions that can affect crop production. 
### As a user, I want to search for commodities and add them to a personalized watchlist so that I can easily monitor their latest available prices and price trends in one place.
---
## Use Cases
### Search and View Commodity Watchlist
Primary Actor: User<br>
Precondition: Commodity price data is available in the system.<br>
Trigger: The user opens the watchlist tab or enters a commodity name into the search bar.<br>
Main Success Scenario:
1. System displays a list of commodities with their current prices, price changes, and small price trend charts.
2. User enters a commodity name into the search bar.
3. System filters the list to show matching commodities.
4. User selects a commodity from the list.
5. System displays more detailed price information and historical charts for the selected commodity.

Success Outcome: The user can search for commodities, monitor their price movements, and access detailed information about a selected commodity.

### View and Compare Commodity Prices
Primary Actor: User<br>
Precondition: Historical price data is available for the selected commodities.<br>
Trigger: The user selects a commodity in the prices tab.<br>
Main Success Scenario:
1. User searches for a commodity, such as wheat, corn, or soybeans.
2. System displays its historical prices on a chart.
3. User selects a date range or another commodity for comparison.
4. System updates the chart and calculates price changes.

Success Outcome: The user can view and compare commodity price trends over time.

### Backtest a Trading Strategy
Primary Actor: User<br>
Precondition: Historical price data is available for the selected commodity.<br>
Trigger: The user enters a strategy description in the backtest tab.<br>
Main Success Scenario:
1. User describes a strategy in plain language, such as "buy wheat when the 20-day average crosses above the 50-day average, sell when it crosses below".
2. System converts the description into entry and exit rules and displays them, along with any assumptions it made, for the user to confirm.
3. User confirms the rules and selects a date range and starting capital.
4. System runs the strategy over the historical prices and simulates each trade.
5. System displays the results: total return, maximum drawdown, win rate, a comparison against buy-and-hold, an equity curve, and the list of trades.
6. User adjusts the strategy, such as adding a stop loss, and the system reruns the backtest and shows the new results beside the previous ones.

Success Outcome: The user can evaluate and refine a strategy based on how it would have performed on historical data.

### Explore Global Crop Production
Primary Actor: User<br>
Precondition: Production data is available for the selected crop and countries.<br>
Trigger: The user selects a crop in the production tab.<br>
Main Success Scenario:
1. User selects a crop, such as wheat.
2. System displays production statistics for different countries.
3. User selects countries and years to compare.
4. System updates the charts and country rankings.

Success Outcome: The user can compare crop production across countries and years.

### View Agricultural Weather Conditions
Primary Actor: User<br>
Precondition: Weather data is available for the selected region.<br>
Trigger: The user selects a region in the weather tab.<br>
Main Success Scenario:
1. User selects an agricultural region.
2. System retrieves temperature and rainfall data.
3. User selects a date range.
4. System displays historical weather trends.

Success Outcome: The user can view weather conditions and trends for the selected agricultural region.

(Add additional use cases as needed.)
---
## Minimal Viable Product (MVP)
| Team Member | MVP Use Case |
|-------------|--------------|
| Abdou | Backtest Strategy |
| William | View Crop Data |
| Serdar | View Weather |
| Dianna | Search Commodity |
---
## Entities
### Commodity
(Add additional entities as needed.)
---
## API Usage
**API Name:**
**Documentation:** [Link]
**How it is used in the program:**
---
## Design Diagrams
### UML Class Diagram
### Sequence Diagrams
---
## How to Run
[Instructions for running the program.]
---
## Notes
[Any additional project information.]
