package com.soob1.batch.module.github.notifier;

import com.soob1.batch.module.github.PullRequest;

import java.util.List;

public interface PrNotifier {

    void send(String user, List<PullRequest> pullRequests);
}
