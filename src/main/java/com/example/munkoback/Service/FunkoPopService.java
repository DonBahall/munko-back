package com.example.munkoback.Service;

import com.example.munkoback.Model.FunkoPop.FunkoPop;
import com.example.munkoback.Model.FunkoPop.FunkoPops;
import com.example.munkoback.Model.FunkoPop.FunkoSearchCriteria;
import com.example.munkoback.Model.FunkoPop.PriceSearch;
import com.example.munkoback.Model.Paging_Sorting.OrderBy;
import com.example.munkoback.Model.Paging_Sorting.Paging;
import com.example.munkoback.Model.Paging_Sorting.SearchPaging;
import com.example.munkoback.Repository.FunkoPopRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.Predicate;

@Service
public class FunkoPopService {
    private final FunkoPopRepository repository;

    public FunkoPopService(FunkoPopRepository repository) {
        this.repository = repository;
    }

    public FunkoPops getAllItems(SearchPaging paging, OrderBy orderBy, FunkoSearchCriteria searchCriteria) {
        Sort sort = getSort(orderBy);
        if (paging == null) {
            paging = new SearchPaging();
            paging.setPage(0);
            paging.setPerPage(15);
        }
        Pageable pageRequest = PageRequest.of(paging.getPage(), paging.getPerPage(), sort);
        Page<FunkoPop> page;
        if (searchCriteria != null) {
            page = repository.findAll(constructSpecification(searchCriteria), pageRequest);
        } else {
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
                PriceSearch priceRange = searchCriteria.getPrice();
                if (priceRange.getFrom() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceRange.getFrom()));
                }
                if (priceRange.getTo() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceRange.getTo()));
                }
            }
            if (searchCriteria.getCollection() != null && !searchCriteria.getCollection().isEmpty()) {
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get("collection"));
                for (String collection : searchCriteria.getCollection()) {
                    inClause.value(collection.toLowerCase());
                }
                predicates.add(inClause);
            }

            if (searchCriteria.getSeries() != null && !searchCriteria.getSeries().isEmpty()) {
                CriteriaBuilder.In<String> seriesInClause = criteriaBuilder.in(root.get("series"));
                for (String series : searchCriteria.getSeries()) {
                    seriesInClause.value(series.toLowerCase());
                }
                predicates.add(seriesInClause);
            }

            if (searchCriteria.getCategory() != null && !searchCriteria.getCategory().isEmpty()) {
                CriteriaBuilder.In<String> categoryInClause = criteriaBuilder.in(root.get("category"));
                for (String category : searchCriteria.getCategory()) {
                    categoryInClause.value(category.toLowerCase());
                }
                predicates.add(categoryInClause);
            }

            if (searchCriteria.getInStock() != null) {
                if (searchCriteria.getInStock().equals(true)) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), 1));
                } else {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), 0));
                }
            }
            if (searchCriteria.getSale() != null) {
                Expression<Boolean> saleExpression = root.get("sale");
                predicates.add(criteriaBuilder.equal(saleExpression, searchCriteria.getSale()));
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
            case CollectionAsk -> Sort.by("collection").ascending();
            case CollectionDesc -> Sort.by("collection").descending();
            default -> Sort.unsorted();
        };
    }

    public FunkoPop getItem(Integer id) {
        return repository.findById(id).orElse(null);
    }
    public AttributeCollection getAllAttributes() {
        List<FunkoPop> popList = repository.findAll();
        return convertToAttributeCollection(popList);
    }

    private AttributeCollection convertToAttributeCollection(List<FunkoPop> popList) {
        AttributeCollection attributeCollection = new AttributeCollection();

        Set<String> categories = new HashSet<>();
        Set<String> collections = new HashSet<>();
        Set<String> series = new HashSet<>();

        for (FunkoPop pop : popList) {
            categories.add(pop.getCategory());
            collections.add(pop.getCollection());
            series.add(pop.getSeries());
        }

        attributeCollection.setCategories(new ArrayList<>(categories));
        attributeCollection.setCollections(new ArrayList<>(collections));
        attributeCollection.setSeries(new ArrayList<>(series));

        return attributeCollection;
    }

   @Setter
   @Getter
   @AllArgsConstructor
   @NoArgsConstructor
   public static class AttributeCollection {
        private List<String> categories;
        private List<String> collections;
        private List<String> series;

    }

}
