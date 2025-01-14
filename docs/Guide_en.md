# Penny - Evaluation Guide

Welcome to Penny! This guide outlines the recommended evaluation process to help you explore and
assess the core features of this personal finance management app.


> Note: The OpenAI API key configured in the project has been automatically deactivated due to the
> project's migration to a public repository.
> For evaluators, please use the backup key provided in the submission form essay. Thank you for
> your
> understanding!(2025.1.14)

## Recommended Evaluation Flow

1. [Initial Setup](#initial-setup)
    - Account Creation
    - Understanding Default Ledger

2. [Core Features Exploration](#core-features-exploration)
    - Basic Transaction Operations
    - Generating Sample Data
    - Analyzing Financial Data
    - Experiencing AI Assistant

3. [Advanced Features Review](#advanced-features-review)
    - Customization Options
    - Financial Tools
    - Additional Management Features

## Initial Setup

When you first launch Penny, you'll be presented with several options:

- Create a new account (recommended for evaluation)
- Sign in with existing account
- Continue without account

For the best evaluation experience, I strongly recommend creating an account as it provides access
to all features including cloud sync and AI capabilities.

### Quick Start Steps

1. Click "Register" on the onboarding page
2. Enter username, email, and password
3. After automatic login, you'll be redirected to the home page
4. Set up your default ledger in the OnBoarding screen

## Core Features Exploration

### Step 1: Basic Transaction Management

Start by understanding the basic transaction flow:

1. Locate the "Add a Transaction" button on the card in Dashboard
2. Try adding a transaction:
    - Select transaction type (Expense/Income)
    - Choose categories (Primary and Secondary)
    - Pop up the Number Pad by clicking the floating action button
    - Enter amount and other details
    - Save and observe it appear in your ledger(The Screen will automatically redirect to the
      Transaction Screen)

### Step 2: Generating Sample Data

To better evaluate the analysis capabilities:

1. Access the Debug Screen:
    - Go to Profile Screen
    - Long-press the gear icon (Settings)
2. Use InsertRandomTransaction feature:
    - Recommended combination: (50,30) for 50 transactions over 30 days
    - This provides enough data to explore analysis features

### Step 3: Exploring Data Views

After generating sample data, explore different visualization options:

1. **Transaction Screen**
    - List View:
        - Try different grouping options (Time/Category)
        - Expand/collapse groups
        - View transaction details
    - Calendar View:
        - Switch using top-right icon
        - Browse transactions by date

2. **Analytics Screen**
   Explore various visualizations:
    - Income/Expense Trend Chart
    - Category Distribution Pie Chart
    - Asset Daily Change Table

   Try different time ranges:
    - Recent (7 days)
    - Month
    - Year
    - Custom range

### Step 4: AI Assistant Features

Access through the Spark icon in bottom navigation:

1. **Text Commands to Try:**
    - "I spent $10 at Starbucks"
    - "Show me this month's statement"
    - "Create a ledger for daily expenses"

2. **Voice Commands:**
    - Click microphone icon
    - Try the same commands using voice input
      Note: First use requires microphone permission

### Step 5:Cloud Sync & Storage

- Enable Auto Cloud Sync in Settings
- try to add a transaction in one device
- launch the app in another platform and login with the same account , the transaction should be
  synced to the new device

## Advanced Features Review

### Customization Options

Access through Settings:

- Language: Switch between English and Chinese
- Theme Color: Try both static and dynamic themes
- Display Mode: Test different modes (Dark/Light/System)
- Contrast Levels (for static themes)

### Financial Tools (Penny's Box)

- Currency Converter
- Loan Calculator

### Additional Management Features

- MyLedger: Create and manage multiple ledgers
- Profile Management

Note: If AI features don't respond immediately, please retry as there might be initialization or API
delays.