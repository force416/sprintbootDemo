package org.eric.telegrambots.command.pttnotify.schedule;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.eric.telegrambots.model.pttnotify.Post;
import org.eric.telegrambots.service.pttnotify.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PTTNotifyScheduleTask {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    @Qualifier("pttNotifyBot")
    private TelegramBot telegramBot;

    @Scheduled(fixedDelay = 60 * 5000)
    public void sendNotify() {
        Map<Long, Map<String, List<Post>>> chatBoardPostsMap = notifyService.getChatsNotifyPosts();
        chatBoardPostsMap.entrySet().stream().forEach((entry) -> {
            long chatId = entry.getKey();

            Map<String, List<Post>> boardPostsMap = entry.getValue();

            boardPostsMap.entrySet().stream().forEach((boardEntry) -> {
                boardEntry.getValue().stream().forEach((post) -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("看板: " + boardEntry.getKey() + "\n");
                    builder.append("推文數: " + post.getLike() + "\n");
                    builder.append("標題: " + post.getTitle() + "\n");
                    builder.append(post.getUrl());
                    telegramBot.execute(new SendMessage(chatId, builder.toString()));
                    notifyService.updateChatBoardLastNotifyPostId(chatId, boardEntry.getKey(), post.getId());
                });

            });
        });
    }
}
