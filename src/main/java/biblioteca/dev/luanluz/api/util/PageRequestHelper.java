package biblioteca.dev.luanluz.api.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestHelper {
    public static PageRequest getPageRequest(Integer page, Integer size, Sort sort) {
        int defaultPage = page == null || page < 0 ? 0 : page;
        int defaultSize = size == null || size < 1 ? 20 : size;
        Sort defaultSort = sort != null ? sort : Sort.by("id").ascending();

        return PageRequest.of(defaultPage, defaultSize, defaultSort);
    }
}
