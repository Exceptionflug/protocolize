package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BannerLayer;

import java.util.List;

public interface BannerPatternsComponent extends StructuredComponent {

    List<BannerLayer> getLayers();

    void setLayers(List<BannerLayer> layers);

    void addLayer(BannerLayer layer);

    void removeLayer(BannerLayer layer);

    void removeAllLayers();

    static BannerPatternsComponent create(List<BannerLayer> layers) {
        return Protocolize.getService(BannerPatternsComponent.Factory.class).create(layers);
    }

    interface Factory {

        BannerPatternsComponent create(List<BannerLayer> layers);

    }

}
