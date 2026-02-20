package com.sfedu.touragency.controller;

import com.sfedu.touragency.controller.support.RequestExtractors;
import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.TourType;
import com.sfedu.touragency.persistence.dao.ToursDynamicFilter;
import com.sfedu.touragency.service.TourService;
import com.sfedu.touragency.util.*;

import java.util.*;
import java.util.stream.*;

public final class ToursController extends Controller {
    private TourService tourService = ServiceLocator.INSTANCE.get(TourService.class);

    private static final int MAX_DESC_LENGTH = 300;

    private static final int FILTER_MAX_PRICE = 1999;

    private static final int PAGE_SIZE = 8;

    @Override
    public void get(RequestService reqService) {
        ToursDynamicFilter filter = prepareFilter(reqService);

        TourType[] tourTypesArr  = Stream.of(reqService.getString("type").split(","))
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(TourType::valueOf)
                .collect(Collectors.toList())
                .toArray(new TourType[]{});
        filter.setTourTypes(tourTypesArr);

        List<Tour> tours = tourService.executeDynamicFilter(filter);

        reqService.setPageAttribute("tours", tours);
        reqService.setPageAttribute("data", filter);
        preparePaging(reqService, tours.size());

        tours.forEach(t -> {
            t.setDescription(TextShrinker.shrink(t.getDescription(), MAX_DESC_LENGTH));
        });

        for(TourType t: tourTypesArr) {
            reqService.setPageAttribute(t.name(), true);
        }
    }

    private void preparePaging(RequestService reqService, int tourCount) {
        int offset = reqService.getInt("offset").orElse(0);
        if (tourCount == PAGE_SIZE) {
            reqService.setPageAttribute("next", offset + PAGE_SIZE);
        }
        if (offset > 0) {
            reqService.setPageAttribute("prev", offset - PAGE_SIZE);
        }
    }


    @Override
    public void post(RequestService reqService) {
        WithStatus<Tour> tourWithStatus =
                RequestExtractors.extractTourWithStatus(reqService);

        if (tourWithStatus.isOk()) {
            tourService.create(tourWithStatus.getPayload());
            reqService.redirect("/agent/tours.html");
        } else {
            reqService.redirect("/agent/new-tour.html?failed=true");
            reqService.putFlashParameter("tour", tourWithStatus.getPayload());
        }
    }

    @Override
    public void put(RequestService reqService) {
        WithStatus<Tour> tourWithStatus =
                RequestExtractors.extractTourWithStatus(reqService);

        Long id = tourWithStatus.getPayload().getId();

        if (tourWithStatus.isOk()) {
            tourService.update(tourWithStatus.getPayload());
            reqService.redirect("/agent/tours.html");
        } else {
            reqService.redirect("/agent/edit-tour.html?failed=true&id=" + id);
        }
    }


    private ToursDynamicFilter prepareFilter(RequestService reqService) {
        ToursDynamicFilter filter = new ToursDynamicFilter();

        SortDir ratingOrd = getSortOrder(reqService, "rating");

        SortDir votesOrd = reqService.getParameter("votes")
                .map(String::toUpperCase)
                .flatMap(s -> TryOptionalUtil.of(() -> SortDir.valueOf(s)))
                .orElse(null);

        SortDir priceOrd = reqService.getParameter("price")
                .map(String::toUpperCase)
                .flatMap(s -> TryOptionalUtil.of(() -> SortDir.valueOf(s)))
                .orElse(null);

        String searchStr = UrlParamDecoder.decode(reqService.getParameter("search").orElse(null))
                .orElse(null);

        Integer priceLow = reqService.getInt("priceLow").filter(p -> p != 0)
                .orElse(null);
        Integer priceHigh = reqService.getInt("priceHigh").filter(p -> p != FILTER_MAX_PRICE)
                .orElse(null);
        boolean hot = !reqService.getString("hotFirst").equals("0");

        Integer offset = reqService.getInt("offset").orElse(null);

        filter.setRatingSort(ratingOrd).setVotesSort(votesOrd).setPriceSort(priceOrd)
              .setSearchQuery(searchStr).setPriceLow(priceLow).setPriceHigh(priceHigh)
              .setHotFirst(hot).setLimit(PAGE_SIZE).setOffset(offset);

        return filter;
    }

    private SortDir getSortOrder(RequestService reqService, String param) {
        return reqService.getParameter(param)
                .map(String::toUpperCase)
                .flatMap(s -> TryOptionalUtil.of(() -> SortDir.valueOf(s)))
                .orElse(null);
    }
}
