# Cryptocurrency Wallet Manager

A client-server Java application created as a final project for the 'Modern Java Technologies' course @ Faculty of Mathematics and Informatics, Sofia University. It simulates a personal cryptocurrency wallet, allowing users to manage their portfolio, buy and sell cryptocurrencies, and track their investments.

## Features

- **User Authentication**: Register and login securely with hashed passwords
- **Portfolio Management**: Deposit funds, buy and sell cryptocurrencies
- **Real-time Data**: Integration with CoinAPI for up-to-date cryptocurrency information
- **Investment Tracking**: Monitor your overall profits/losses and get detailed wallet summaries
- **Multi-user Support**: Server can handle multiple simultaneous client connections

## Technology Stack

- **Backend**: Java with NIO for non-blocking server implementation
- **API Integration**: java.net.HttpClient for REST CoinAPI requests
- **Data Persistence**: Binary file storage for user data with secure password hashing
- **Interface**: Console-based client application

## Architecture

The application follows a client-server architecture:

- **Client**: Console application that handles user input and displays formatted responses
- **Server**: NIO-based server that processes requests, manages user data, communicates with CoinAPI and logs unexpected errors to a file. It uses a ScheduledExecutorServices to update the prices of the cryptocurrencies
- **Data Storage**: Binary files to persist user information and wallet data
- **Unit Tests**: The project reaches 70% coverage with JUnit 5 and Mockito. 

## Commands

- `register` - Create a new user account
- `login` - Authenticate with your credentials
- `logout` - Log out of your account
- `deposit <amount>` - Add funds to your wallet
- `withdraw <amount>` - Withdraw funds from your wallet
- `list-offerings` - View available cryptocurrencies
- `buy <offering_code> <amount>` - Purchase cryptocurrency
- `sell <offering_code>` - Sells owned cryptocurrency
- `get-wallet-summary` - View current portfolio status
- `get-wallet-overall-summary` - Check total profit/loss from different currencies
- `help` - Get list of commands

## Future Improvements

- Graphical user interface
- Additional cryptocurrency data visualization
- Transaction history and reporting
- Support for additional investment types
