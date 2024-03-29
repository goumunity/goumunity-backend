package com.ssafy.goumunity.domain.feed.domain;

import com.ssafy.goumunity.domain.user.domain.User;
import com.ssafy.goumunity.domain.user.domain.UserCategory;
import com.ssafy.goumunity.domain.user.domain.UserSavingCategory;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class FeedWeight implements Comparable<FeedWeight> {
    private FeedRecommendResource feedRecommendResource;
    private Double weight;

    public static FeedWeight from(FeedRecommendResource feedRecommendResource, User user) {
        return FeedWeight.builder()
                .feedRecommendResource(feedRecommendResource)
                .weight(getWeight(feedRecommendResource, user))
                .build();
    }

    private static Double getWeight(FeedRecommendResource feedRecommendResource, User user) {

        // 카테고리체크
        if (feedRecommendResource.getFeedCategory() == null
                || feedRecommendResource.getFeedCategory() == FeedCategory.FUN) {
            return Math.random() * 0.15;
        }

        // Feed SavingCategory nullcheck
        // User SavingCategory nullcheck
        // User MonthBudget check

        FeedSavingCategory feedSavingCategory = feedRecommendResource.getSavingCategory();
        UserSavingCategory userSavingCategory = user.getSavingCategory();

        if (feedSavingCategory == null
                || userSavingCategory == null
                || user.getMonthBudget() == null
                || user.getMonthBudget() == 0) {
            return 0.0;
        }

        // 1. feed savingcategory - user savingcategory간 가중치계산
        // 2. feed savingcategory - user monthbudget간 가중치계산

        double weight = 1.0;

        if (userSavingCategory == UserSavingCategory.HOUSING) {
            weight *=
                    switch (feedSavingCategory) {
                        case HOUSING -> 0.45;
                        case TRANSPORTATION_COMMUNICATION -> 0.15;
                        case FOOD -> 0.21;
                        case ENTERTAINMENT -> 0.1;
                        default -> 0.05;
                    };
        } else if (userSavingCategory == UserSavingCategory.TRANSPORTATION_COMMUNICATION) {
            weight *=
                    switch (feedSavingCategory) {
                        case HOUSING -> 0.1;
                        case TRANSPORTATION_COMMUNICATION -> 0.55;
                        case FOOD -> -0.22;
                        case ENTERTAINMENT -> 0.05;
                        default -> 0.04;
                    };
        } else if (userSavingCategory == UserSavingCategory.FOOD) {
            weight *=
                    switch (feedSavingCategory) {
                        case HOUSING -> 0.13;
                        case TRANSPORTATION_COMMUNICATION -> 0.2;
                        case FOOD -> 0.4;
                        case ENTERTAINMENT -> 0.08;
                        default -> 0.07;
                    };
        } else if (userSavingCategory == UserSavingCategory.ENTERTAINMENT) {
            weight *=
                    switch (feedSavingCategory) {
                        case HOUSING -> 0.12;
                        case TRANSPORTATION_COMMUNICATION -> 0.19;
                        case FOOD -> 0.1;
                        case ENTERTAINMENT -> 0.3;
                        default -> 0.1;
                    };
        } else {
            weight *= 0.2;
        }

        // 소비구간별 가중치조절
        if (user.getMonthBudget() <= 1000000) {
            weight *=
                    switch (feedSavingCategory) {
                        case HOUSING -> 0.17;
                        case TRANSPORTATION_COMMUNICATION -> 0.09;
                        case FOOD -> 0.28;
                        case ENTERTAINMENT -> 0.15;
                        default -> 0.05;
                    };
        } else if (user.getMonthBudget() <= 2000000) {
            weight *=
                    switch (feedSavingCategory) {
                        case HOUSING -> 0.3;
                        case TRANSPORTATION_COMMUNICATION -> 0.2;
                        case FOOD -> 0.5;
                        case ENTERTAINMENT -> 0.36;
                        default -> 0.12;
                    };
        } else if (user.getMonthBudget() <= 3000000) {
            weight *=
                    switch (feedSavingCategory) {
                        case HOUSING -> 0.41;
                        case TRANSPORTATION_COMMUNICATION -> 0.35;
                        case FOOD -> 0.8;
                        case ENTERTAINMENT -> 0.5;
                        default -> 0.2;
                    };
        } else {
            weight *= 0.2;
        }

        return weight;
    }

    @Deprecated
    private static Double calcWeight(FeedRecommendResource feedRecommendResource, User user) {
        // Feed Category null check

        if (feedRecommendResource.getFeedCategory() == null
                || feedRecommendResource.getFeedCategory() == FeedCategory.FUN) {
            return Math.random() * 3.75;
        }

        // Feed Price, AfterPrice nullcheck
        // User Usercategory, age nullcheck
        // Price, AfterPrice 유효성 검증

        if (feedRecommendResource.getPrice() == null
                || feedRecommendResource.getPrice() == 0
                || feedRecommendResource.getAfterPrice() == null
                || feedRecommendResource.getAfterPrice() == 0
                || user.getUserCategory() == null
                || user.getAge() == null
                || user.getAge() == 0
                || feedRecommendResource.getPrice() - feedRecommendResource.getAfterPrice() <= 0) {
            return 0.0;
        }

        int price = feedRecommendResource.getPrice();
        int afterPrice = feedRecommendResource.getAfterPrice();
        int profit = feedRecommendResource.getPrice() - afterPrice;

        double weight = 0.0;

        // userCategory별 분류

        if (user.getUserCategory() == UserCategory.JOB_SEEKER) {
            double seekerAfterPrice = 0.0;
            double seekerProfit = 0.0;

            if (afterPrice <= 100000) seekerAfterPrice = 1.0 - (((double) 100000 - afterPrice) / 100000);
            else if (afterPrice < 3000000)
                seekerAfterPrice = 1.0 - (((double) afterPrice - 100000) / 2900000);

            if (profit <= 15000) seekerProfit = 1.0 - (((double) 15000 - profit) / 15000);
            else if (profit < 1000000) seekerProfit = 1.0 - (((double) profit - 15000) / 985000);

            weight += seekerAfterPrice * 0.75;
            weight += seekerProfit * 0.5;

        } else if (user.getUserCategory() == UserCategory.COLLEGE_STUDENT) {
            double studentAfterPrice = 0.0;
            double studentProfit = 0.0;

            if (afterPrice <= 200000) studentAfterPrice = 1.0 - (((double) 200000 - afterPrice) / 200000);
            else if (afterPrice < 3000000)
                studentAfterPrice = 1.0 - (((double) afterPrice - 200000) / 2800000);

            if (profit <= 50000) studentProfit = 1.0 - (((double) 50000 - profit) / 50000);
            else if (profit < 1000000) studentProfit = 1.0 - (((double) profit - 50000) / 950000);

            weight += studentAfterPrice * 0.75;
            weight += studentProfit * 0.5;

        } else if (user.getUserCategory() == UserCategory.NEWCOMER_TO_SOCIETY) {
            double newcomerAfterPrice = 0.0;
            double newcomerProfit = 0.0;

            if (afterPrice <= 500000)
                newcomerAfterPrice = 1.0 - (((double) 500000 - afterPrice) / 500000);
            else if (afterPrice < 3000000)
                newcomerAfterPrice = 1.0 - (((double) afterPrice - 500000) / 2500000);

            if (profit <= 150000) newcomerProfit = 1.0 - (((double) 150000 - profit) / 150000);
            else if (profit < 1000000) newcomerProfit = 1.0 - (((double) profit - 150000) / 850000);

            weight += newcomerAfterPrice * 0.75;
            weight += newcomerProfit * 0.5;

        } else if (user.getUserCategory() == UserCategory.EMPLOYEE) {
            double employeeAfterPrice = 0.0;
            double employeeProfit = 0.0;

            if (afterPrice <= 1500000)
                employeeAfterPrice = 1.0 - (((double) 1500000 - afterPrice) / 1500000);
            else if (afterPrice < 3000000)
                employeeAfterPrice = 1.0 - (((double) afterPrice - 1500000) / 1500000);

            if (profit <= 500000) employeeProfit = 1.0 - (((double) 500000 - profit) / 500000);
            else if (profit < 1000000) employeeProfit = 1.0 - (((double) profit - 500000) / 500000);

            weight += employeeAfterPrice * 0.75;
            weight += employeeProfit * 0.5;
        }

        // userAge별 분류

        if (user.getAge() < 20) {
            double underTwentyAfterPrice = 0.0;
            double underTwentyProfit = 0.0;

            if (afterPrice <= 50000)
                underTwentyAfterPrice = 1.0 - (((double) 50000 - afterPrice) / 50000);
            else if (afterPrice < 3000000)
                underTwentyAfterPrice = 1.0 - (((double) afterPrice - 50000) / 2950000);

            if (profit <= 50000) underTwentyProfit = 1.0 - (((double) 50000 - profit) / 50000);
            else if (profit < 1000000) underTwentyProfit = 1.0 - (((double) profit - 50000) / 950000);

            weight += underTwentyAfterPrice * 0.75;
            weight += underTwentyProfit * 0.5;

        } else if (user.getAge() < 25) {
            double underTwentyFiveAfterPrice = 0.0;
            double underTwentyFiveProfit = 0.0;

            if (afterPrice < 500000)
                underTwentyFiveAfterPrice = 1.0 - (((double) 500000 - afterPrice) / 500000);
            else if (afterPrice < 3000000)
                underTwentyFiveAfterPrice = 1.0 - (((double) afterPrice - 500000) / 2500000);

            if (profit < 250000) underTwentyFiveProfit = 1.0 - (((double) 250000 - profit) / 250000);
            else if (profit < 1000000)
                underTwentyFiveProfit = 1.0 - (((double) profit - 250000) / 750000);

            weight += underTwentyFiveAfterPrice * 0.75;
            weight += underTwentyFiveProfit * 0.5;

        } else if (user.getAge() < 28) {
            double underTwentyEightAfterPrice = 0.0;
            double underTwentyEightProfit = 0.0;

            if (afterPrice < 300000)
                underTwentyEightAfterPrice = 1.0 - (((double) 300000 - afterPrice) / 300000);
            else if (afterPrice < 3000000)
                underTwentyEightAfterPrice = 1.0 - (((double) afterPrice - 300000) / 2700000);

            if (profit < 100000) underTwentyEightProfit = 1.0 - (((double) 100000 - profit) / 100000);
            else if (profit < 1000000)
                underTwentyEightProfit = 1.0 - (((double) profit - 100000) / 900000);

            weight += underTwentyEightAfterPrice * 0.75;
            weight += underTwentyEightProfit * 0.5;

        } else {
            double overAfterPrice = 0.0;
            double overProfit = 0.0;

            if (afterPrice < 1000000) overAfterPrice = 1.0 - (((double) 1000000 - afterPrice) / 1000000);
            else if (afterPrice < 3000000)
                overAfterPrice = 1.0 - (((double) afterPrice - 1000000) / 2000000);

            if (profit < 500000) overProfit = 1.0 - (((double) 500000 - profit) / 500000);
            else if (profit < 1000000) overProfit = 1.0 - (((double) profit - 500000) / 500000);

            weight += overAfterPrice * 0.75;
            weight += overProfit * 0.5;
        }

        double per = ((double) price - (double) afterPrice) / (double) price;
        weight += per;

        double rand = Math.random() * 0.25;

        weight += rand;

        return weight;
    }

    @Override
    public int compareTo(FeedWeight fw) {
        return Double.compare(fw.weight, this.weight);
    }
}
