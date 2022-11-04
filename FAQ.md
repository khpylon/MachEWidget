# Frequently Asked Questions

As I find time, more questions and answers will be added here.

#### Q: What's the difference between the Google Play and GitHub versions of the app?

Very little; the GitHub version has a "Check for Updates" option in the three-dot menu.  That's about it.

#### Q: The "Last refresh" time shown on widget changes randomly; the value I set for the time between widget updates has no effect.  What's happening?

The time shown in "Last refresh" is based on a specific value read from the servers.  It corresponds (I believe) to the last time the vehicle
uploaded data.  The time between widget updates controls when the app attempts to read from the servers.  Sometimes, frustratingly, the last upload
occurs as you open doors to get out of the vehicle.

If you want to confirm that the app *is* doing something, triple-tap on "Last refresh".  It will display the time it read the last update and the time 
of the last alarm (when it requested an update).  If the last alarm time doesn't match your time between widget updates, you likely have battery 
optimizations turned on.  If the last update time is different from the last alarm time, then the app may have lost sync with the servers and you'll 
need to log back into your Ford account.

Note: the time between widget updates and battery optimization settings affect the app's draw on your phone's battery.  Since the widget is only active
when it does an update, using the 5 or 10 minute settings should not significantly impact your battery level.

#### Q:  Why does the app seems to update for a while, then stop?

My theory is that this is caused by Android's battery optimization settings; alarms are used to signal when to update, and Android sometimes will ignore these when the device is idle.  
To stop Android from doing this, 

1. Go to "Settings" in the Ford Status Widget app 
2. Find the "Battery optimization" setting.  If it says "On", tap on the text to open the Android battery optimizaion settings.
3. Under **All Apps**, locate the app
4. Under the app's **Battery** settings, choose **Unrestricted** or **Don't optimize** (the wording may vary)
5. Go back to "Setting" and verify that the setting is now "Off (recommended)"

#### Q: Why does the app shows my doors or windows open when they aren't?

The app pulls the latest status is from Ford's servers.  The data it gets isn't always accurate.  
If you find this really annoying, there are some things you can do:
1. Send a lock/unlock command to you vehicle.  The app will refresh the status shortly after the command is sent.
2. Open the FordPass app, and pull down in the main screen.  This will force the vehicle to perform an update.
Be aware that Ford has discouraged forcing status updates too frequently as it can drain the 12V battery.

<!--
#### Q: What does all the OTA update information mean?

I'm not sure there is any consensus on this; it's all speculation.  Ford defines a deployment window of one week for the update to take place.  If the update fails, it's unclear when the next deployment window happens.

Observations from users have noticed this progression:
1. requested (no details)
2. request_delivery_queued (OTAM_S1001)
3. artifact_retrieval_in_progress (OTAM_S1023)
4. installation_queued  (OTAM_S1007)
5. deploying  (OTAM_S1016 or OTAM_S1021)
6. success (OTAM_S1010), or alternately  
7. failure (OTAM_E1016)

#### Q: Why do the app say there is no OTA information?  I know the vehicle is OTA capable.
#### Q: Why do the app say there is no OTA information?  It used to show something but now it say something like "N/A".

The OTA information from Ford's servers is apparently removed some number of days after the update (again, there is no consensus as far as I know
what the number of days is).  WHen this happens, the app can tell the vehicle is OTA capable, but simply doesn't receive any information to display. 
-->

#### Q: Why do the vehicle and app show different odometer readings?

Ford stores the odometer reading as an integer, in kilometers.  When it's converted to miles, it is only an approximation.  It should be accurate to within +/- 0.5 miles.

#### Q: What are the two "X"'s on the bottom left of the widget?

These are short-cut links you can assign to other apps.  To do so, select **Choose Linked Apps** in the three-dot menu, then select which app you'd like to link   to which button.  If you don't see the app you want listed, go into **Setting** and enable **Allow any app to be linked to the "charger" widget**.

#### Q: Why does the GitHub version of the app require permission to "install unknown apps"?

Google's Play Store vets the apps you download to prevent malware from being installed on your phone which might compromise your personal data.
As such, a non Play Store app needs your permission to perform an installation.  This app only downloads the *app-release.apk* file from the
project's GitHub repository and nothing else.  It's meant as a convenience; if you are still suspicious of it, you can still download the apk directly. 
Go to "Settings" in the three-dot menu, scroll to the bottom, and click on "GitHub repo" to take you there.

#### Q: Why does my vehicle have the wrong status image/wrong fuel type/no OTA information/etc etc etc?

The app decodes each vehicle's VIN information (which is retrieved when you log into your Ford account) to determine the  model information,
which is then used to determine what image to use.  It uses the vechicle's status information to determine if the vehicle is a gasoline/hybrid, electric, or plug-in hybrid.
I've only added support for the vehicles users have requested, and even then it's limited to what graphics I'm able to obtain. 

#### Q: How can I get support for my vehicle?

I'm happy to expand support to other Ford/Lincoln vehicles which are supported by FordPass; all that's needed is to open a new Issue on GitHub.  But I will need a couple of things:
1. I need logs from the app to see the status information about the vehicle (status and OTA capabilities).  The best way to get this for me is to place the widget on your homescreen,
clear all of the app's data (including your login information), turn on "Enable logging" under "Settings", log into your Ford account, then let the app run a fre minutes until the
widget updates.  Now choose "Save Logfile" from the app's three-dot menu, and upload the logfile from your Download directory to GitHub (if you want to redact your VIN, please only
remove the last 5 digits; the others are valuable).
2. I need the graphic image of your vehicle (actually, I don't **need** this, but you probably won't be happy seeing a Mach-E in place of your vehicle).  Other users have been
kind enough to donate funds for me to purchase the graphics currently used in the app, so if there is sufficient interest I might have funds to purchase graphics for your
vehicle; if I do, please consider paying it forward for others.

#### Q: Why don't you charge for the app?

There are a number of reasons, but the primary one is I started doing this for myself as a hobby.   I only request donations to offset my expenses needed to expand the app; as of
November 2022 I've spent about $150 to purchase graphic content, a developer's account on Google Play, and various other development tools.  Eventually any unused funds will be donated to charities.
