import pandas as pd
import numpy as np
from sklearn.metrics import mean_absolute_error
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import GradientBoostingRegressor
import matplotlib.pyplot as plt

def prepare_data(file_path):
    # Load and clean data
    data = pd.read_csv(file_path)
    data = data[['ReportYear', 'Total_All_Home_Types']].dropna()
    
    # Add growth rate feature
    data['PriceChange'] = data['Total_All_Home_Types'].pct_change()
    data = data.dropna()  # Remove first row with NaN price change
    
    return data

def train_model(data):
    # Prepare features
    X = data[['ReportYear', 'PriceChange']]
    y = data['Total_All_Home_Types']
    
    # Split data
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    # Scale features
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)
    
    # Train model with constraints
    model = GradientBoostingRegressor(
        n_estimators=100,
        learning_rate=0.1,
        max_depth=3,
        random_state=42
    )
    
    model.fit(X_train_scaled, y_train)
    
    # Calculate and print MAE
    y_pred = model.predict(X_test_scaled)
    mae = mean_absolute_error(y_test, y_pred)
    print(f"\nModel Performance:")
    print(f"Mean Absolute Error: ${mae:,.2f}")
    
    # Print average annual price change
    avg_price_change = data['PriceChange'].mean()
    print(f"Average Annual Price Change: {avg_price_change:.1%}")
    
    return model, scaler

def predict_price(model, scaler, data, year):
    # Get last known price and price change
    last_year = data['ReportYear'].max()
    last_price = data.loc[data['ReportYear'] == last_year, 'Total_All_Home_Types'].iloc[0]
    avg_price_change = data['PriceChange'].mean()
    
    # For predictions more than 1 year in the future, use moving average
    if year > last_year:
        # Use conservative growth rate (80% of historical average)
        conservative_growth = avg_price_change * 0.8
        years_ahead = year - last_year
        # Apply compound growth with decay factor
        decay_factor = 0.9 ** years_ahead  # Reduces growth rate over time
        growth_rate = conservative_growth * decay_factor
        predicted_price = last_price * (1 + growth_rate) ** years_ahead
        
        # Add sanity check limits
        max_annual_increase = 0.15  # 15% maximum annual increase
        min_price = last_price
        max_price = last_price * (1 + max_annual_increase) ** years_ahead
        
        predicted_price = min(max_price, max(min_price, predicted_price))
        
        return predicted_price
    else:
        # For historical years, use the model
        features = np.array([[year, avg_price_change]])
        features_scaled = scaler.transform(features)
        return model.predict(features_scaled)[0]

def plot_predictions(data, model, scaler, future_year):
    current_years = data['ReportYear']
    current_prices = data['Total_All_Home_Types']
    
    # Generate future predictions
    future_years = range(current_years.max() + 1, future_year + 1)
    future_prices = [predict_price(model, scaler, data, year) for year in future_years]
    
    plt.figure(figsize=(12, 8))
    
    # Plot historical data
    plt.plot(current_years, current_prices, 'bo-', label='Historical Prices', alpha=0.6)
    
    # Plot predictions
    if len(future_years) > 0:
        plt.plot(future_years, future_prices, 'rx-', label='Predicted Prices')
        
        # Add labels for predictions
        for year, price in zip(future_years, future_prices):
            plt.annotate(f'${price:,.0f}', (year, price), 
                        textcoords="offset points", 
                        xytext=(0,10), 
                        ha='center')
    
    plt.title('House Price Predictions (With Conservative Growth Model)')
    plt.xlabel('Year')
    plt.ylabel('Price ($)')
    plt.legend()
    plt.grid(True)
    
    # Add trendline
    plt.plot(current_years, current_prices.rolling(window=3).mean(), 
             'g--', alpha=0.5, label='Moving Average')
    
    plt.savefig('predictions.png')
    print("\nPlot saved as 'predictions.png'")

def main():
    file_path = "Average_Resale_Home_Prices.csv"
    data = prepare_data(file_path)
    model, scaler = train_model(data)
    
    while True:
        print("\n1. Predict price for a year")
        print("2. Show price prediction graph")
        print("3. Exit")
        
        choice = input("Choose an option (1-3): ")
        
        if choice == '1':
            try:
                year = int(input("Enter year to predict: "))
                last_year = data['ReportYear'].max()
                last_price = data.loc[data['ReportYear'] == last_year, 'Total_All_Home_Types'].iloc[0]
                
                price = predict_price(model, scaler, data, year)
                print(f"\nLast known price ({last_year}): ${last_price:,.2f}")
                print(f"Predicted price for {year}: ${price:,.2f}")
                
                if year > last_year:
                    total_increase = (price - last_price) / last_price * 100
                    years_ahead = year - last_year
                    annual_rate = (((price / last_price) ** (1/years_ahead)) - 1) * 100
                    print(f"Total predicted increase: {total_increase:.1f}%")
                    print(f"Implied annual growth rate: {annual_rate:.1f}%")
                
            except ValueError:
                print("Please enter a valid year")
                
        elif choice == '2':
            try:
                end_year = int(input("Enter last year to predict: "))
                plot_predictions(data, model, scaler, end_year)
            except ValueError:
                print("Please enter a valid year")
                
        elif choice == '3':
            print("Goodbye!")
            break
            
        else:
            print("Invalid choice. Please try again.")

if __name__ == "__main__":
    main()