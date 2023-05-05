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
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        int noOfScreensSubscribed = subscriptionEntryDto.getNoOfScreensRequired();
        int plan = -1;
        if(subscription.getSubscriptionType() == SubscriptionType.BASIC) {
            plan = 500 + 200 * noOfScreensSubscribed;
        }
        else if(subscription.getSubscriptionType() == SubscriptionType.PRO) {
            plan = 800 + 250 * noOfScreensSubscribed;
        }
        else {
            plan = 1000 + 350 * noOfScreensSubscribed;
        }

        subscription.setNoOfScreensSubscribed(noOfScreensSubscribed);
        subscription.setTotalAmountPaid(plan);

        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        return plan;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
//       User user = userRepository.findById(userId).get();
//       Subscription subscription = user.getSubscription();
//       SubscriptionType userPack = subscription.getSubscriptionType();
//       if(userPack == SubscriptionType.ELITE) throw new Exception("Already the best Subscription");
//       int diff = 0;
//       if(userPack == SubscriptionType.BASIC){
//           diff =  800 + 250 * subscription.getNoOfScreensSubscribed() - subscription.getTotalAmountPaid();
//           subscription.setTotalAmountPaid(800 + 250 * subscription.getNoOfScreensSubscribed());
//           subscription.setSubscriptionType(SubscriptionType.PRO);
//       }
//       else if(userPack == SubscriptionType.PRO){
//           diff = 1000 + 350 * subscription.getNoOfScreensSubscribed() - subscription.getTotalAmountPaid();
//           subscription.setTotalAmountPaid(1000 + 350 * subscription.getNoOfScreensSubscribed());
//           subscription.setSubscriptionType(SubscriptionType.ELITE);
//       }
//       subscriptionRepository.save(subscription);
//       return diff;

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        int amtDiffer = 0;

        if(subscription.getSubscriptionType()==SubscriptionType.ELITE)
            throw new Exception("Already the best Subscription");

        else if (subscription.getSubscriptionType()==SubscriptionType.PRO) {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            amtDiffer = (1000 + (350*subscription.getNoOfScreensSubscribed()))-subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(1000 + (350*subscription.getNoOfScreensSubscribed()));
            subscription.setStartSubscriptionDate(new Date());
        }
        else if (subscription.getSubscriptionType()==SubscriptionType.BASIC) {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            amtDiffer = (800 + (250*subscription.getNoOfScreensSubscribed()))-subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(800 + (250*subscription.getNoOfScreensSubscribed()));
            subscription.setStartSubscriptionDate(new Date());
        }

        subscriptionRepository.save(subscription);
        return amtDiffer;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

//        List<Subscription> subscriptions = subscriptionRepository.findAll();
//        int revenue = 0; // declaring revenue as an Integer object
//
//        for (Subscription subscription : subscriptions) {
//            int totalAmountPaid = subscription.getTotalAmountPaid();
//            revenue += totalAmountPaid;
//        }
//        return revenue;


        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int revenue = 0;
        for(Subscription subscription:subscriptions){
            revenue += subscription.getTotalAmountPaid();
        }
        return revenue;
    }

}
