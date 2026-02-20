package com.sfedu.touragency.persistence.dao.factory;

import com.sfedu.touragency.persistence.dao.*;

public interface DaoFactory {
    PurchaseDao getPurchaseDao();
    ReviewDao getReviewDao();
    TourDao getTourDao();
    TourImageDao getTourImageDao();
    UserDao getUserDao();
}
