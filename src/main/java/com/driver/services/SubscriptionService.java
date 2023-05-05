package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        int noOfScreensRequired = subscriptionEntryDto.getNoOfScreensRequired();

        int noOfScreensSubscribed = -1;

        int plan = -1;
        if(subscription.getSubscriptionType() == SubscriptionType.BASIC) {
            noOfScreensSubscribed = 1 + noOfScreensRequired;
            plan = 500 + 200 * noOfScreensSubscribed;
        }
        else if(subscription.getSubscriptionType() == SubscriptionType.PRO) {
            noOfScreensSubscribed = 2 + noOfScreensRequired;
            plan = 800 + 250 * noOfScreensSubscribed;
        }
        else {
            noOfScreensSubscribed = 3 + noOfScreensRequired;
            plan = 1000 + 350 * noOfScreensSubscribed;
        }


        subscription.setNoOfScreensSubscribed(noOfScreensSubscribed);
        subscription.setTotalAmountPaid(plan);

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        return plan;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        if(subscription.getSubscriptionType() == SubscriptionType.ELITE) throw new Exception("Already the best Subscription");

        int price = -1;
        if(subscription.getSubscriptionType() == SubscriptionType.BASIC){
            int oldPrice = subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.PRO);
            price = 850 + 250 * subscription.getNoOfScreensSubscribed() - oldPrice;

        }
        else if(subscription.getSubscriptionType() == SubscriptionType.PRO){
            int oldPrice = subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.PRO);
            price = 1000 + 350 * subscription.getNoOfScreensSubscribed() - oldPrice;
        }
        subscriptionRepository.save(subscription);
        return price;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int revenue = 0;
        for(Subscription subscription : subscriptions){
            revenue += subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
