package com.example.munkoback.Service;

import com.example.munkoback.Model.FunkoPop.FunkoPop;
import com.example.munkoback.Model.FunkoPop.FunkoPops;
import com.example.munkoback.Model.FunkoPop.FunkoSearchCriteria;
import com.example.munkoback.Model.Paging_Sorting.OrderBy;
import com.example.munkoback.Model.Paging_Sorting.Paging;
import com.example.munkoback.Model.Paging_Sorting.SearchPaging;
import com.example.munkoback.Repository.FunkoPopRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
public class FunkoPopService {
    private final FunkoPopRepository repository;
    public FunkoPopService(FunkoPopRepository repository) {
        this.repository = repository;
    }

    public FunkoPops getAllItems(SearchPaging paging, OrderBy orderBy, FunkoSearchCriteria searchCriteria) {
        Sort sort = getSort(orderBy);
        if(paging == null){
            paging = new SearchPaging();
            paging.setPage(0);
            paging.setPerPage(15);
        }
        Pageable pageRequest = PageRequest.of(paging.getPage(), paging.getPerPage(), sort);
        Page<FunkoPop> page;
        if (searchCriteria != null && (searchCriteria.getName() != null ||  searchCriteria.getSeries() != null ||
                searchCriteria.getCategory() != null || searchCriteria.getPrice() != null)) {
            page = repository.findAll(constructSpecification(searchCriteria), pageRequest);
        }else {
            page = repository.findAll(pageRequest);
        }
        return new FunkoPops(
                page.getContent(),
                new Paging(
                        paging.getPage(),
                        paging.getPerPage(),
                        page.getTotalPages(),
                        (int) page.getTotalElements()
                ));
    }
    private Specification<FunkoPop> constructSpecification(FunkoSearchCriteria searchCriteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchCriteria.getName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchCriteria.getName().toLowerCase() + "%"));
            }
            if (searchCriteria.getPrice() != null) {
                predicates.add(criteriaBuilder.equal(root.get("price"), searchCriteria.getPrice()));
            }
            if (searchCriteria.getSeries() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("series")), "%" + searchCriteria.getSeries().toLowerCase() + "%"));
            }
            if (searchCriteria.getCategory() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + searchCriteria.getCategory().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    private Sort getSort(OrderBy orderBy) {
        if (orderBy == null) {
            return Sort.unsorted();
        }
        return switch (orderBy) {
            case IdAsc -> Sort.by("id").ascending();
            case IdDesc -> Sort.by("id").descending();
            case NameAsc -> Sort.by("name").ascending();
            case NameDesc -> Sort.by("name").descending();
            case PriceAsc -> Sort.by("price").ascending();
            case PriceDesc -> Sort.by("price").descending();
            case DateAsc -> Sort.by("date").ascending();
            case DateDesc -> Sort.by("date").descending();
            case CategoryAsk -> Sort.by("category").ascending();
            case CategoryDesc -> Sort.by("category").descending();
            case SeriesAsk -> Sort.by("series").ascending();
            case SeriesDesc -> Sort.by("series").descending();
            default -> Sort.unsorted();
        };
    }

    public FunkoPop getItem(Integer id) {
        return repository.findById(id).orElse(null);
    }
}
