package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface WritableBookContentComponent extends StructuredComponent {

    List<WriteablePage> getPages();

    void setPages(List<WriteablePage> pages);

    void addPage(WriteablePage page);

    void removePage(WriteablePage page);

    void removeAllPages();

    static WritableBookContentComponent create(List<WriteablePage> pages) {
        return Protocolize.getService(Factory.class).create(pages);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class WriteablePage {
        String content;
        String filteredContent;
    }

    interface Factory {

        WritableBookContentComponent create(List<WriteablePage> pages);

    }

}
