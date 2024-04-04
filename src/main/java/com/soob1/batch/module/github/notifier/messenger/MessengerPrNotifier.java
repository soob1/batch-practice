package com.soob1.batch.module.github.notifier.messenger;

import com.soob1.batch.module.github.PullRequest;
import com.soob1.batch.module.github.notifier.PrNotifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class MessengerPrNotifier implements PrNotifier {

    private final ChannelInfo channelInfo;
    private final WebClient webClient;

    @Value("${messenger.token}")
    private String token;

    @Value("${messenger.url}")
    private String url;

    public MessengerPrNotifier(ChannelInfo channelInfo, WebClient webClient) {
        this.channelInfo = channelInfo;
        this.webClient = webClient.mutate()
                .baseUrl(url)
                .defaultHeader("Authorization", "Token " + token)
                .build();
    }

    public void send(String user, List<PullRequest> pullRequests) {

        Integer channelId = channelInfo.getChannelIdByUser(user);

        if (channelId == null) {
            return;
        }

        String body = String.format("{ \"channel\": \"%s\",\"content\": \"%s\" }", channelId, createMessage(pullRequests));

        String response = webClient.method(HttpMethod.POST)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("response = " + response);
    }

    private String createMessage(List<PullRequest> pullRequests) {
        StringBuilder content = new StringBuilder();
        content.append("오늘의 PR 목록입니다! 😀\\n\\n");
        pullRequests.forEach(pr -> content.append("- ")
                .append(pr.getTitle())
                .append("\\n  ")
                .append(pr.getUrl())
                .append("\\n"));
        return content.toString();
    }
}
