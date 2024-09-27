package com.azvtech;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class GitHubUserActivity
{
    private static final String GITHUB_API_URL = "https://api.github.com/users/%s/events";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter GitHub username: ");
        String username = scanner.nextLine();

        String apiUrl = String.format(GITHUB_API_URL, username);

        try {
            String response = fetchGitHubActivity(apiUrl);
            System.out.println("Recent activity for user: " + username);
            System.out.println(response);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching activity: " + e.getMessage());
        }
    }

    private static String fetchGitHubActivity(String apiUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }

        return response.body();
    }
}
