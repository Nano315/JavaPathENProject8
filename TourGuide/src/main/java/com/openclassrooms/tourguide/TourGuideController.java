package com.openclassrooms.tourguide;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;

	@Autowired
	RewardsService rewardsService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	return tourGuideService.getUserLocation(getUser(userName));
    }
    
    @RequestMapping("/getNearbyAttractions") 
    public List<NearbyAttractionDTO> getNearbyAttractions(@RequestParam String userName) {
    	User user = getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
    	List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);
    	
    	List<NearbyAttractionDTO> nearbyAttractions = new ArrayList<>();
    	for (Attraction attraction : attractions) {
    		double distance = rewardsService.getDistance(attraction, visitedLocation.location);
    		int rewardPoints = tourGuideService.getAttractionRewardPoints(attraction, user);
    		
    		NearbyAttractionDTO dto = new NearbyAttractionDTO(
    			attraction.attractionName,
    			attraction.latitude,
    			attraction.longitude,
    			visitedLocation.location.latitude,
    			visitedLocation.location.longitude,
    			distance,
    			rewardPoints
    		);
    		nearbyAttractions.add(dto);
    	}
    	
    	return nearbyAttractions;
    }
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

    /**
     * DTO representing a nearby attraction with all required details.
     */
    public static class NearbyAttractionDTO {
        public String attractionName;
        public double attractionLatitude;
        public double attractionLongitude;
        public double userLatitude;
        public double userLongitude;
        public double distance;
        public int rewardPoints;

        public NearbyAttractionDTO(String attractionName, double attractionLatitude, double attractionLongitude,
                                   double userLatitude, double userLongitude, double distance, int rewardPoints) {
            this.attractionName = attractionName;
            this.attractionLatitude = attractionLatitude;
            this.attractionLongitude = attractionLongitude;
            this.userLatitude = userLatitude;
            this.userLongitude = userLongitude;
            this.distance = distance;
            this.rewardPoints = rewardPoints;
        }
    }
}
