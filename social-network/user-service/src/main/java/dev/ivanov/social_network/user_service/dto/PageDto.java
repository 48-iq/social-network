package dev.ivanov.social_network.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDto<T> {
    private List<T> data;
    private Integer size;
    private Integer page;
    private Integer totalPages;
    private Long totalElements;
}
