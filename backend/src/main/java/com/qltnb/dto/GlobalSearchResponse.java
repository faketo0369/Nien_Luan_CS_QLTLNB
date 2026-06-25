package com.qltnb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchResponse {
    private SearchResultWrapper<DocumentResponse> documents;
    private SearchResultWrapper<ClientResponse> clients;
    private SearchResultWrapper<CaseResponse> cases;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultWrapper<T> {
        private List<T> content;
        private long total;
    }
}
