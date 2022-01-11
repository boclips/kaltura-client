package com.boclips.kalturaclient.baseentry;

import com.boclips.kalturaclient.captionsProvider.CaptionProvider;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.boclips.kalturaclient.captionsProvider.CaptionProviderCaptionStatus.CANCELLED;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Getter
@Builder(toBuilder = true)
public class BaseEntry {
    private final String id;
    private final List<String> tags;
    private final List<String> categories;
    private final String thumbnailUrl;
    private final String name;

    public boolean isTagged() {
        return isNotEmpty(tags);
    }

    public boolean hasCategories() {
        return isNotEmpty(categories);
    }

    public boolean isCaptionRequested(String requestStatus) {
        return isTaggedWith(requestStatus);
    }

    public boolean isCaptionRequestCancelled(CaptionProvider captionProvider){
        return hasCategory(captionProvider.uploadedToProviderTag())
            && CANCELLED.equals(captionProvider.getCaptionStatus(name, id));
        }

    public boolean isCaptionRequestProcessing(String requestStatus){
        return hasCategory(requestStatus);
    }

    private boolean isTaggedWith(String tag) {
        return isTagged() && tags.contains(tag);
    }

    private boolean hasCategory(String category) {
        return hasCategories() && categories.contains(category);
    }

}
