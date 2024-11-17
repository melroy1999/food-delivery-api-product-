package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.model.Activity;
import com.assignment.fooddelivery.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity not found"));
    }

    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Activity updateActivity(Long id, Activity activity) {
        Activity existingActivity = activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity not found"));
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setTimestamp(activity.getTimestamp());
        // Update other fields as necessary
        return activityRepository.save(existingActivity);
    }

    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity not found"));
        activityRepository.delete(activity);
    }
}