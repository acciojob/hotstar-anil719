package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository ;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User addedUser = userRepository.save(user);
        return addedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).get();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int ans = 0;
        for(WebSeries webSeries : webSeriesList){
            int age = user.getAge();
            SubscriptionType userSubscription = user.getSubscription().getSubscriptionType();
            SubscriptionType webSeriesSubscription = webSeries.getSubscriptionType();

            if(age >= webSeries.getAgeLimit() && userSubscription == SubscriptionType.ELITE) ans++;   //can watch all series & shows
            else if(age >= webSeries.getAgeLimit() && userSubscription == SubscriptionType.PRO &&
            webSeriesSubscription == SubscriptionType.BASIC || webSeriesSubscription == SubscriptionType.PRO) ans++;

            else if(age >= webSeries.getAgeLimit() && userSubscription == SubscriptionType.BASIC
            && webSeriesSubscription == SubscriptionType.BASIC) ans++;
        }

        return ans;

    }


}
