package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.chat.ChatElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    String title;
    String filteredTitle;
    String author;
    int generation;
    List<Page> pages;
    boolean resolved;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Page {
        ChatElement<?> content;
        ChatElement<?> filteredContent;
    }
}
