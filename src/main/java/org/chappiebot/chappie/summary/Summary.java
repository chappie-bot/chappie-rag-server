package org.chappiebot.chappie.summary;

import java.util.List;

public record Summary(String uuid, String title, List<String> categories, String summary) {
}
