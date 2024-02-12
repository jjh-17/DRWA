package com.a708.drwa.redis.constant;

import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;

public final class Constants {
    public static final String RANK_REDIS_KEY = "rank";
    public static final String RANK_FOOD_REDIS_KEY = "rank:food";
    public static final String RANK_ECONOMY_REDIS_KEY = "rank:economy";
    public static final String RANK_SPORTS_REDIS_KEY = "rank:sports";
    public static final String RANK_ANIMAL_REDIS_KEY = "rank:animal";
    public static final String RANK_SHOPPING_REDIS_KEY = "rank:shopping";
    public static final String RANK_LOVE_REDIS_KEY = "rank:love";
    public static final String RANK_POLITICS_REDIS_KEY = "rank:politics";
    public static final String RANK_SOCIAL_REDIS_KEY = "rank:social";
    public static final String RANK_PERSON_REDIS_KEY = "rank:person";
    public static final String RANK_CULTURE_REDIS_KEY = "rank:culture";
    public static final String RANK_ETC_REDIS_KEY = "rank:etc";

    public String getConstantsByCategory(String category) {
        if(category.equals(DebateCategory.FOOD.name()))             return Constants.RANK_FOOD_REDIS_KEY;
        else if(category.equals(DebateCategory.ECONOMY.name()))     return Constants.RANK_ECONOMY_REDIS_KEY;
        else if(category.equals(DebateCategory.SPORTS.name()))      return Constants.RANK_SPORTS_REDIS_KEY;
        else if(category.equals(DebateCategory.ANIMAL.name()))      return Constants.RANK_ANIMAL_REDIS_KEY;
        else if(category.equals(DebateCategory.SHOPPING.name()))    return Constants.RANK_SHOPPING_REDIS_KEY;
        else if(category.equals(DebateCategory.LOVE.name()))        return Constants.RANK_LOVE_REDIS_KEY;
        else if(category.equals(DebateCategory.POLITICS.name()))    return Constants.RANK_POLITICS_REDIS_KEY;
        else if(category.equals(DebateCategory.SOCIETY.name()))     return Constants.RANK_SOCIAL_REDIS_KEY;
        else if(category.equals(DebateCategory.CHARACTER.name()))   return Constants.RANK_PERSON_REDIS_KEY;
        else if(category.equals(DebateCategory.CULTURE.name()))     return Constants.RANK_CULTURE_REDIS_KEY;
        else if(category.equals(DebateCategory.ETC.name()))         return Constants.RANK_ETC_REDIS_KEY;
        else                                                        throw new DebateException(DebateErrorCode.CATEGORY_NOT_FOUND);
    }
}
