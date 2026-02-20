package com.sfedu.touragency.domain;

import java.math.*;
import java.util.*;

public class Tour {

    private Long id;

    private String title;

    private String description;

    private String destination;

    private TourType type;

    boolean hot;

    private BigDecimal price;

    private boolean enabled;

    private Double avgRating;

    private int votesCount;

    private int discount;


    public Tour() {
    }

    public Tour(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TourType getType() {
        return type;
    }

    public void setType(TourType type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public int getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tour tour = (Tour) o;
        return Objects.equals(id, tour.id) &&
                hot == tour.hot &&
                enabled == tour.enabled &&
                votesCount == tour.votesCount &&
                discount == tour.discount &&
                Objects.equals(title, tour.title) &&
                Objects.equals(description, tour.description) &&
                Objects.equals(destination, tour.destination) &&
                type == tour.type &&
                price == null ? tour.price == null : price.compareTo(tour.getPrice()) == 0 &&
                Objects.equals(avgRating, tour.avgRating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, destination, type, hot,
                price, enabled, avgRating, votesCount, discount);
    }

    @Override
    public String toString() {
        return "Tour{" +
                "avgRating=" + avgRating +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", destination='" + destination + '\'' +
                ", type=" + type +
                ", hot=" + hot +
                ", price=" + price +
                ", enabled=" + enabled +
                ", votesCount=" + votesCount +
                ", discount=" + discount +
                '}';
    }
}
