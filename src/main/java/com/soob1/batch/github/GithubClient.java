package com.soob1.batch.github;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubClient {

    @Value("${github.user}")
    private String user;

    @Value("${github.token}")
    private String token;

    public GitHub connect() throws IOException {
        GitHub github = new GitHubBuilder()
                .withOAuthToken(token, user)
                .build();
        github.checkApiUrlValidity();

        return github;
    }
}
