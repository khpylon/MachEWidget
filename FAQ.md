# Frequently Asked Questions

As I find time, more questions and answers will be added here.

#### Q:  Why does the app seems to update for a while, then stop?

My theory is that this is caused by Android's battery optimization settings; alarms are used to signal when to update, and Android sometimes will ignore these when the device is idle.  To stop Android from doing this

1. Open Android's Settings
2. Click on **Apps**
3. Under **All Apps** locate the Mach-E app
4. Under the app's **Battery** settings, select **Unrestricted**

#### Q: Why does the app shows my doors or windows open when they aren't?

The app pulls the latest status is from Ford's servers.  The data it gets isn't always accurate.  If you find this really annoying, try pressing on the lock icon to send a lock/unlock command to you vehicle.  The app will attempt to refresh the status shortly after the command is sent.

#### Q: What does all the OTA update information mean?

I'm not sure there is any consensus on this; it's all speculation.  Ford defines a deployment window of one week for the update to take place.  If the update fails, it's unclear when the next deployment window happens.

Observations from users have noticed this progression:
1. requested (no details)
2. request_delivery_queued (OTAM_S1001)
3. artifact_retrieval_in_progress (OTAM_S1023)
4. installation_queued  (OTAM_S1007)
5. deploying  (OTAM_S1021)
6a. success (OTAM_S1010), or alternately  
6b. failure (OTAM_E1016)
   
#### Q: Why is the odometer showing a different value that the vehicle?

Ford stores the odometer reading as an integer, in kilometers.  When it's converted to miles, it is only an approximation.  It should be accurate to within +/- 0.5 miles.

#### Q: What are the two "X"'s on the bottom left of the widget?

These are short-cut links you can assign to other apps.  To do so, select **Choose Linked Apps** in the three-dot menu, then select which app you'd like to link   to which button.  If you don't see the app you want listed, go into **Setting** and enable **Allow any app to be linked to the "charger" widget**.