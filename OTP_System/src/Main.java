import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException {
        final String url = "YOUR_JDBC_SERVER_HOST_LINK";
        final String userName = "YOUR_USER_NAME";
        final String password = "YOUR_PASSWORD";

        String mobile;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Mobile Number:");
        mobile = scanner.next();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        try (Connection connection = DriverManager.getConnection(url, userName, password)) {
            String generatedCode;
            do {
                generatedCode = generateRandomCode();
                // System.out.println("Genarated Code: " + generatedCode);
            } while (codeExists(connection, generatedCode));

            int smsStatus= sendOTPSMS(generatedCode,mobile);
            if (smsStatus==1) {
                currentDate = LocalDate.now();
                currentTime = LocalTime.now();

                final String insertQuery = "INSERT INTO otptable (mobile, code, date, time) VALUES (?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, mobile);
                    preparedStatement.setString(2, generatedCode);
                    preparedStatement.setDate(3, Date.valueOf(currentDate));
                    preparedStatement.setTime(4, Time.valueOf(currentTime));

                    preparedStatement.executeUpdate();

                    // if (rowsAffected > 0) {
                    //System.out.println("Data inserted successfully.");
                    //} else {
                    // System.out.println("No rows affected. Data insertion failed.");
                    //}
                }

                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Enter Your OTP:\n");

            String inputedOTP = scanner.next();
            LocalDate currentDate2 = LocalDate.now();
            LocalTime currentTime2 = LocalTime.now();

            Connection connectionq = DriverManager.getConnection(url, userName, password);
            final String query = "SELECT id, code, date, time FROM otptable WHERE mobile ="+mobile+" ORDER BY date DESC, time DESC LIMIT 1;";
            Statement statement = connectionq.createStatement();
            ResultSet userResultSet = statement.executeQuery(query);


            if (userResultSet.next()) {
                int id = userResultSet.getInt("id");
                String code = userResultSet.getString("code");
                LocalDate date = userResultSet.getDate("date").toLocalDate();
                LocalTime time = userResultSet.getTime("time").toLocalTime();

                int verificationStatus = verifyOTP(mobile, code, inputedOTP, date, time,currentDate2,currentTime2);
                while (verificationStatus == -1) {
                    System.out.println("Wrong OTP. Please enter again:");
                    inputedOTP = scanner.next();
                    verificationStatus =  verifyOTP(mobile, code, inputedOTP, date, time,currentDate2,currentTime2);
                }
                if (verificationStatus == 1) {


                    Connection connectionin = DriverManager.getConnection(url, userName, password);
                    final String queryin = "SELECT id FROM otptable WHERE mobile ="+mobile+";";
                    Statement statementin = connectionin.createStatement();
                    ResultSet userResultSetin = statementin.executeQuery(queryin);
                    while (userResultSetin.next()){
                        int dltId=userResultSetin.getInt("id");
                        // System.out.println("ID :"+dltId);
                        deleteOTP(dltId);

                    }
                    System.out.println("Verified");
                } else if (verificationStatus == -1) {
                    System.out.println("Wrong OTP");
                } else {
                    System.out.println("Time out !! Try Again!!");
                    System.out.println("Generate new OTP!");
                }
            } else {
                System.out.println("No OTP found for the given mobile number.");
            }
        }
    }



    private static int verifyOTP(String mobile, String code, String inputedOTP, LocalDate date, LocalTime currentTime, LocalDate currentDate2, LocalTime currentTime2) {
        if (code.equals(inputedOTP)) {
            if (LocalDate.now().equals(date)) {
                Duration d = getTimeDifference(currentTime, LocalTime.now());
                int sec = calculateDurationInSeconds(d);

                if (sec < 60) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    private static void deleteOTP(int id) {
        String url="jdbc:mysql://avnadmin:AVNS_8f65KgDkLWAY6bVplK7@mysql-4e2a7f2-mealmanagement.a.aivencloud.com:25728/otp?ssl-mode=REQUIRED";
        String userName="avnadmin";
        String password="AVNS_EQuO0Vy0a6fLcSD1DTU";
        try (Connection connection = DriverManager.getConnection(url, userName, password)) {
            String deleteQuery = "DELETE FROM otptable WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                statement.setInt(1, id);
                statement.executeUpdate();
                // System.out.println(rowsAffected + " row(s) deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static Duration getTimeDifference(LocalTime startTime, LocalTime endTime) {
        return Duration.between(startTime, endTime);
    }

    private static int calculateDurationInSeconds(Duration duration) {
        return (int) duration.getSeconds();
    }


    private static String generateRandomCode() {
        int codeLength = 6;

        String characters = "0123456789";

        StringBuilder codeBuilder = new StringBuilder(codeLength);
        Random random = new Random();

        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            codeBuilder.append(randomChar);
        }

        return codeBuilder.toString();
    }

    private static boolean codeExists(Connection connection, String code) throws SQLException {
        final String selectQuery = "SELECT COUNT(*) FROM otptable WHERE code = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, code);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }
    public static int sendOTPSMS(String otp, String mobile) throws SQLException {
        String message="Your OTP Code is : "+otp;
        String to= "88"+mobile;

        final String API_URL = "YOUR_SMS_API_URL/ENDPOINT";
        final String API_KEY = "YOUR_SMS_API_KEY";




        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            // System.out.println(encodedMessage);
            String urlString = String.format("%s?api_key=%s&msg=%s&to=%s", API_URL, API_KEY, encodedMessage, to);
            URL url;
            url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("OTP sent successfully to " + to);
                return 1;
            } else {
                System.err.println("Failed to send OTP to " + to);
                return -1;
            }
        } catch (Exception e) {
            System.err.println("Error sending SMS to " + to + ": " + e.getMessage());
            return -1;
        }
    }
}



















