package com.soob1.batch.module.github;

import com.soob1.batch.module.github.notifier.messenger.ChannelInfo;
import com.soob1.batch.module.github.notifier.messenger.MessengerPrNotifier;
import com.soob1.batch.module.github.notifier.PrNotifier;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.*;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class PrNotifyStepConfiguration {

    private static final String EXECUTION_CONTEXT_KEY = "requestedPrs";
    private final StepBuilderFactory stepBuilderFactory;
    private final GithubClient githubClient;
    private final ChannelInfo channelInfo;
    private final WebClient webClient;

    @Bean
    public PrNotifier notifier() {
        return new MessengerPrNotifier(channelInfo, webClient);
    }

    @Bean
    public Step getPrListStep() {
        return stepBuilderFactory.get("getPrList")
                .tasklet((stepContribution, chunkContext) -> {
                    GitHub github = githubClient.connect();

                    GHOrganization organization = github.getOrganization("mailplug-inc");
                    GHTeam team = organization.getTeamByName("GW2");
                    Set<GHUser> teamMembers = team.getMembers();
                    Map<String, GHRepository> repositories = team.getRepositories();

                    // 각각의 repo 별로 pr 을 가져온다.
                    Map<String, List<PullRequest>> requestedPrs = new HashMap<>();

                    for (Map.Entry<String, GHRepository> entry : repositories.entrySet()) {
                        GHRepository repository = entry.getValue();
                        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);
                        for (GHPullRequest pr : pullRequests) {
                            // 아직 승인 안한 리뷰어
                            for (GHUser requestedReviewer : pr.getRequestedReviewers()) {
                                // team member 가 아니면 continue
                                if (!teamMembers.contains(requestedReviewer)) {
                                    continue;
                                }
                                if (!requestedPrs.containsKey(requestedReviewer.getLogin())) {
                                    requestedPrs.put(requestedReviewer.getLogin(), new ArrayList<>());
                                }

                                requestedPrs.get(requestedReviewer.getLogin()).add(new PullRequest(pr));
                            }
                        }
                    }

                    ExecutionContext executionContext = stepContribution.getStepExecution().getJobExecution().getExecutionContext();
                    executionContext.put(EXECUTION_CONTEXT_KEY, requestedPrs);

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step sendNotificationStep() {
        return stepBuilderFactory.get("sendNotification")
                .tasklet((stepContribution, chunkContext) -> {
                    ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                    Map<String, List<PullRequest>> requestedPrs = (Map<String, List<PullRequest>>) executionContext.get(EXECUTION_CONTEXT_KEY);

                    for (Map.Entry<String, List<PullRequest>> entry : requestedPrs.entrySet()) {
                        PrNotifier prNotifier = notifier();
                        prNotifier.send(entry.getKey(), entry.getValue());
                    }

                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
