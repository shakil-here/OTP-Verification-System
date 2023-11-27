# Awesome OTP Generator and Verifier

🔐 Secure and simple OTP (One-Time Password) generator and verifier with SMS integration.

![GitHub stars](https://img.shields.io/github/stars/shakil-here/OTP-System)
![GitHub forks](https://img.shields.io/github/forks/shakil-here/OTP-System)
![GitHub issues](https://img.shields.io/github/issues/shakil-here/OTP-System)
![GitHub license](https://img.shields.io/github/license/shakil-here/OTP-System)

## Overview

This repository contains a Java application that generates and verifies OTPs using a MySQL database and sends the OTP to a mobile number via an SMS API.

🌟 **Key Features:**
- Generate random 6-digit OTPs.
- Verify OTPs with time and date constraints.
- Send OTPs via SMS using an external API.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [SMS API Integration](#sms-api-integration)
- [Demo: Database Table](#demo-database-table)
- [Contributing](#contributing)
- [License](#license)

## Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/your-username/your-repository.git
    cd your-repository
    ```

2. Download the MySQL Connector/J JDBC driver JAR file and add it to your project's classpath.

    - MySQL Connector/J: [Download Link](https://dev.mysql.com/downloads/connector/j/)

3. Compile and run the Java application:

    ```bash
    javac Main.java
    java Main
    ```

4. Follow the instructions in the console to input mobile numbers, OTPs, and verify the OTPs.

## Usage

- Input your mobile number to receive an OTP via SMS.
- Enter the received OTP to verify.

## Configuration

Before running the application, you need to set up the following configurations:

- **JDBC Server Host Link:**
  - Replace `YOUR_JDBC_SERVER_HOST_LINK` in `Main.java` with your MySQL server link.

- **Database Credentials:**
  - Replace `YOUR_USER_NAME` and `YOUR_PASSWORD` in `Main.java` with your MySQL username and password.

- **SMS API Credentials:**
  - Replace `YOUR_SMS_API_URL/ENDPOINT` and `YOUR_SMS_API_KEY` in `Main.java` with your API details.

## Database Setup

1. Create a MySQL database named `otp`.

2. Execute the following SQL script to create the `otptable` table:

    ```sql
    CREATE TABLE otptable (
        id INT AUTO_INCREMENT PRIMARY KEY,
        mobile VARCHAR(11) NOT NULL,
        code VARCHAR(6) NOT NULL,
        date DATE NOT NULL,
        time TIME NOT NULL
    );
    ```

## SMS API Integration

1. Sign up for an SMS API service and obtain your API key.

2. Replace `YOUR_SMS_API_URL/ENDPOINT` and `YOUR_SMS_API_KEY` in `Main.java` with your API details.

## Demo: Database Table

Here's a demo of the `otptable` database table:

| id  | Mobile        | Code     | Date       | Time      |
| --- | ------------- | -------- | ---------- | --------- |
| 1   | 1234567890     | 123456   | 2023-11-27 | 12:34:56  |
| 2   | 9876543210     | 654321   | 2023-11-26 | 18:45:30  |

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

## License

This project is licensed under the [MIT License](LICENSE).
