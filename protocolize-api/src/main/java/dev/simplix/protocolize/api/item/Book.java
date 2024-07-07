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
    private String title;
    private String filteredTitle;
    private String author;
    private int generation;
    private List<Page> pages;
    private boolean resolved;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Page {
        private ChatElement<?> content;
        private ChatElement<?> filteredContent;
    }
}
