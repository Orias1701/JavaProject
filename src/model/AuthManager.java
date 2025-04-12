// package model;

// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.util.Base64;
// import util.Logger;

// public class AuthManager {

//     private static String authHeader;

//     public static boolean login(String username, String password) {
//         HttpClient client = HttpClient.newHttpClient();
//         String credentials = username + ":" + password;
//         String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
//         authHeader = "Basic " + encodedCredentials;

//         HttpRequest request = HttpRequest.newBuilder()
//                 .uri(URI.create("http://localhost:8080/api/login"))
//                 .header("Authorization", authHeader)
//                 .POST(HttpRequest.BodyPublishers.noBody())
//                 .build();

//         try {
//             HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//             Logger.log("Login response status: " + response.statusCode());
//             Logger.log("Login response body: " + response.body());
//             return response.statusCode() == 200;
//         } catch (Exception e) {
//             Logger.logError("Login error: " + e.getMessage());
//             return false;
//         }
//     }

//     public static String getAuthHeader() {
//         return authHeader;
//     }
// }