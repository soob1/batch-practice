package com.soob1.batch.module.github;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GHPullRequest;

import java.io.IOException;
import java.io.Serializable;

@Getter
@NoArgsConstructor
public class PullRequest implements Serializable {

    private String title;
    private String url;
    private String author;
    private String createdAt;

    public PullRequest(GHPullRequest ghPullRequest) throws IOException {
        this.title = ghPullRequest.getTitle();
        this.url = ghPullRequest.getHtmlUrl().toString();
        this.author = ghPullRequest.getUser().getLogin();
        this.createdAt = ghPullRequest.getCreatedAt().toString();
    }
}
