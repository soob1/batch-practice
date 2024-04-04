package com.soob1.batch.module.github.notifier.messenger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "messenger")
@AllArgsConstructor
public class ChannelInfo {

    // todo 값이 바인딩되지 않는 이슈
    private List<Channel> channels;

    @Getter
    @Setter
    public class Channel {
        private String user;
        private Integer id;
    }

    public Integer getChannelIdByUser(String user) {
        Channel userChannel = channels.stream()
                .filter(c -> c.getUser().equals(user))
                .findFirst()
                .orElse(null);
        return userChannel == null ? null : userChannel.getId();
    }
}
