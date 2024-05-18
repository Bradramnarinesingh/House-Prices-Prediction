import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_absolute_error, mean_squared_error
import matplotlib
matplotlib.use('TkAgg')  # Set the backend for matplotlib
import matplotlib.pyplot as plt

plt.ion()  # Ensure interactive mode is on

# Load the data
csv_file_path = "/mnt/data/Average_Resale_Home_Prices.csv"
data = pd.read_csv(csv_file_path)

# Filter necessary columns and handle missing values if any
data = data[['ReportYear', 'Total_All_Home_Types']].dropna()

# Prepare the features (X) and target (y)
X = data[['ReportYear']]
y = data['Total_All_Home_Types']

# Split the data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train the regression model
model = LinearRegression()
model.fit(X_train, y_train)

# Evaluate the model
y_pred = model.predict(X_test)
mae = mean_absolute_error(y_test, y_pred)
mse = mean_squared_error(y_test, y_pred)
rmse = mean_squared_error(y_test, y_pred, squared=False)

print(f"Model Evaluation:\nMean Absolute Error: {mae}\nMean Squared Error: {mse}\nRoot Mean Squared Error: {rmse}")

# Function to predict future house prices
def predict_future_price(year):
    # Ensure the input is a DataFrame with the correct column name
    future_price = model.predict(pd.DataFrame({'ReportYear': [year]}))
    return future_price[0]

# Function to visualize historical data and future predictions
def visualize_predictions(years):
    historical_years = data['ReportYear']
    historical_prices = data['Total_All_Home_Types']
    
    future_years = list(range(historical_years.max() + 1, years + 1))
    future_prices = [predict_future_price(year) for year in future_years]
    
    plt.figure(figsize=(10, 6))
    plt.plot(historical_years, historical_prices, label='Historical Prices', marker='o')
    plt.plot(future_years, future_prices, label='Predicted Prices', marker='x')
    plt.xlabel('Year')
    plt.ylabel('Total All Home Types Price')
    plt.title('House Price Predictions')
    plt.legend()
    plt.grid(True)
    plt.show()

# Menu to input a year and exit the program
def menu():
    while True:
        print("\nMenu:")
        print("1. Predict the price of a home in a future year")
        print("2. Visualize predictions")
        print("0. Exit")
        choice = input("Enter your choice: ")

        if choice == '1':
            try:
                year = int(input("Enter the year: "))
                predicted_price = predict_future_price(year)
                print(f"Predicted house price for the year {year}: ${predicted_price:.2f}")
            except ValueError:
                print("Invalid input. Please enter a valid year.")
            except Exception as e:
                print(f"An error occurred: {e}")
        elif choice == '2':
            try:
                year = int(input("Enter the last year for prediction visualization: "))
                visualize_predictions(year)
            except ValueError:
                print("Invalid input. Please enter a valid year.")
            except Exception as e:
                print(f"An error occurred: {e}")
        elif choice == '0':
            print("Exiting the program.")
            break
        else:
            print("Invalid choice. Please try again.")

# Run the menu
if __name__ == "__main__":
    menu()
