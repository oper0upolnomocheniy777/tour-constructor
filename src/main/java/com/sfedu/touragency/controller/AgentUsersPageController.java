package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.service.UserService;
import com.sfedu.touragency.util.ServiceLocator;

import java.util.*;
import java.util.stream.*;

public final class AgentUsersPageController extends Controller {
    private UserService userService = ServiceLocator.INSTANCE.get(UserService.class);

    @Override
    public void get(RequestService reqService) {
        List<User> users = userService.findAllOrderByRegularity(true);
        List<UserPurchaseSummaryDto> summaries = users.stream()
                     .map(u -> new UserPurchaseSummaryDto(u, userService.countPurchases(u.getId()),
                                userService.computePurchasesTotalPrice(u.getId()).intValue(),
                                u.getRoles().contains(Role.TOUR_AGENT)))
                     .collect(Collectors.toList());
        reqService.setPageAttribute("summaries", summaries);
    }

    public static class UserPurchaseSummaryDto {
        private User user;
        private int purchaseCount;
        private int totalPrice;
        private boolean tourAgent;

        public UserPurchaseSummaryDto(User user, int purchaseCount, int totalPrice,
                                      boolean isTourAgent) {
            this.user = user;
            this.purchaseCount = purchaseCount;
            this.totalPrice = totalPrice;
            this.tourAgent = isTourAgent;
        }

        public int getPurchaseCount() {
            return purchaseCount;
        }

        public int getTotalPrice() {
            return totalPrice;
        }

        public User getUser() {
            return user;
        }

        public boolean isTourAgent() {
            return tourAgent;
        }
    }
}
