package com.soob1.batch.github;

import lombok.RequiredArgsConstructor;
import org.kohsuke.github.*;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PrNotificationStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final GithubClient githubClient;

    @Bean
    public Step getRepoStep() {
        return stepBuilderFactory.get("getRepoStep")
                .tasklet((stepContribution, chunkContext) -> {
                    GitHub github = githubClient.connect();

                    GHOrganization organization = github.getOrganization("honeyosori");

                    Map<String, GHRepository> repositories = organization.getRepositories();

                    // 각각의 repo 별로 pr 을 가져온다.
                    // todo chunk 적용

                    for (Map.Entry<String, GHRepository> entry : repositories.entrySet()) {
                        GHRepository repository = entry.getValue();
                        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);
                        for (GHPullRequest pullRequest : pullRequests) {

                        }
                    }

                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
