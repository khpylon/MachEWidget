# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
but at this time the project does not adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2024.01.28-09
### Fixed
- A few bugs in vehicle database and processing status JSON info.

## 2024.01.27-13:30
### Added
- Allow the choice of what and how information is displayed about the 12V (low-voltage) battery.  This is configurable under "Settings".
### Fixed
- Handle missing JSON status information.

## 2024.01.26:18:08
### Fixed
- Fix problem parsing date/time strings.

## 2024.01.26
### Fixed
- Add missing column when migrating vehicle database.

## 2024.01.25
### Added
- Display 12V battery's state of charge.
### Fixed
- Correct bug in Manage Vehicles which used the old API.
### Changed
- The JSON format of the status information has changed completely, and since changes are based on what
  is discovered from observing actual vehicle data everything may not work.  I only have info for Mach-Es
  and some F-150s variants. If you find something not working, particularly if you have a different vehicle,
  grab a logfile and upload it to GitHib following the instruction in the issue
  ["User-submitted data for new FordPass API"](https://github.com/khpylon/MachEWidget/issues/45).

## 2024.01.24
### Changed
- This release is the first attempt using the new FordPass API (*not* the mythical "public" Ford API).
  Only status information is implemented; vehicles commands and charging activity do not work.
- The JSON format of the status information has changed completely, and since changes are based on what
  is discovered from observing actual vehicle data everything may not work.  I only have info for Mach-Es
  and some F-150s variants. If you find something not working, particularly if you have a different vehicle,
  grab a logfile and upload it to GitHib following the instruction in the issue
  ["User-submitted data for new FordPass API"](https://github.com/khpylon/MachEWidget/issues/45).

## 2023.10.11
### Changed
- PLEASE READ: In early October 2023, major portions of the application programming interface (API)
  were disabled by Ford, making it impossible to read status information about vehicles.
  Ford is developing a "public" API that should restore this functionality. Sources have stated
  it should be released prior to January 2024 but no firm date has been given.
  **No further app updates will be published until that occurs.**
- The app will no longer attempt to send *any information*, including login
    authentication, to Ford's servers in order to avoid the possibility of FordPass accounts being
    locked out.

## 2023.10.08
### Changed
- Disabled remote commands (start/stop, lock/unlock, and force updates) due to API changes.  If Ford's public API supports
these in the future, they will be re-enabled.

## 2023.08.29
### Added
- Thailand-manufactured Ranger vehicles are now recognized (uses F-150 images).

## 2023.08.21
### Added
- Fusion vehicles are now officially supported. Note: this vehicle is the Mondeo outside North 
  America, but I have no information on the VIN pattern to support it.

## 2023.08.20
### Fixed
- Fix crash (again) when setting up menu options. 

## 2023.08.19
### Added
- Preliminary support for Fusion vehicles.
- Force charging reminder alarm to be reloaded on boot.
### Fixed
- Fix crash on some phones during app load. 

## 2023.08.17
### Changed
- Changed the operation of the "View DCFC logs" activity.  This menu option, and charging settings,
  will only appear if you have an electric vehicle in your profile.  When accessed, it will
  provide guidance if the DCFC log file is empty. 

## 2023.08.14
### Fixed
- Change the Update notification to work with the new notification channels.

## 2023.08.13
## Added
- Display real-time DC Fast Charging activity.
### Fixed
- Correct issues with notifications channels.

## 2023.08.10
### Changed
- Increase the size of the refresh icon by 20% (to make it easier to click).
- Separate notifications into different channels to give the user more granular control.]()

## 2023.08.08
### Fixed
- Displayed units for distance and pressure did not match user's locale info.
- Vehicles were not displaying in Charging Reminder activity.

## 2023.08.03
### Added
- Official release of DC Fast Charging activity.

## 2023.07.25
### Added
- "Beta" release of DC Fast Charging activity.  **If you don't own a BEV *or* don't feel
adventurous, you may want to skip this and wait for the next release.**  
When "Display charging session info", "Display DCDC info", and "Save DCFC logs" are all enabled
in "Settings", you will be able to view recent DC fast charging data via "View DCFC Logs" in the
three-dot menu.  The logs will show power, energy, and SOC during each charging session.  Note that
DCFC data is only collected once the app detects that the car is plugged in, so you must either
refresh the app manually after plugging in, or set the time between widget updates to the minimum
of 5 minutes.

## 2023.07.13
### Added
- Display DC fast charging data in "real time" (update widget every 30 seconds).  To use this
feature, both "Display charge session info" and "Display DCFC info" under "Settings" must be enabled.

### Fixed
- Fix bug with location information not updating correctly.

## 2023.07.11
### Added
- Diesel vehicles which use diesel exhaust fluid (which is probably all of them?) now display DEF status
info.  Tap on the "LV Battery" display to see DEF range and DEF level (I'm not completely sure what
this represents; I'm assuming it's a percentage).

### Changed
- Widget update intervals of 5 and 10 minutes have been added back. 

### Fixed
- Notification icons now have a proper shape that is representative of the app.

## 2023.05.23
### Fixed
- Fix various null pointer and other exceptions.

## 2023.04.28
### Fixed
- Fix various null pointer and other exceptions.

## 2023.04.11
### Added
- North American Focus vehicles are now recognized (uses Escape images).

### Fixed
- Another null pointer exception.

## 2023.04.08
### Fixed
- Fix some null pointer exceptions.

## 2023.04.07
### Added
- Vehicles can be removed from the app under Manage Vehicles by swiping them to the right.

### Fixed
- Fixed bug with estimated range not always being displayed for BEVs/PHEVs.

## 2023.04.05
### Added
- For BEVs/PHEVs: while charging, display charge rate and energy added.  This can be enabled or disabled in Settings.

### Changed
- Detect incorrect user ID or password on login.

### Fixed
- Refresh token if necessary before executing commands (door lock/unlock, remote start, etc).

## 2023.03.24
### Fixed
- Catch potential exception with geocoding interfaces on Android 12 and below devices.

## 2023.03.22
### Added
- Display icon for high-voltage battery in blue when plugged in and charge is scheduled.

### Fixed
- Use new geocoding interface on Android 13 devices.
- Force "Last refresh" time to stay on a single line.

## 2023.03.03
### Fixed
- Remove potentially corrupted click counter key-value pairs.
- On each log in, remove vehicles from database which are not associated with the current user.

## 2023.03.02
### Fixed
- Fix bug causing app crash on some widget actions after settings are restored.

## 2023.02.28
## Added
- Ranger and Maverick vehicles are now recognized (uses F150 silhouettes).

### Fixed
- After app setting are restored, update widgets which refer to missing VINs.
- Remove old user information on log-in.
- Still resolving issues with token refreshes.

## 2023.02.15
### Fixed
- Resolving other issues with app not working after a token refresh.

## 2023.01.27
### Fixed
- Fix null pointer exceptions when updating units on settings restore.

## 2023.01.25
### Added
- European Ford Focus vehicles are now recognized (uses the Escape silhouettes).

### Changed
- Support more combinations of displayed units (MPG/KPH, PSI/kPa/BAR).  These are configurable in Settings.

## 2023.01.22
### Changed
- Adjusted the widget layouts to spread out textual info. 

### Fixed
- Fix null pointer exceptions and some other internal issues.

## 2023.01.13
### Fixed
- Attempt to resolve issue with app not working after a token refresh fails.

## 2023.01.12
### Added
- More vehicle-specific logos in the large widget, which are used when vehicle images are not available.

### Changed
- Don't allow vehicles to be added if there is a valid user is not found in the databases.
- Automatically convert lower-case letters to upper-case when entering a VIN.
- Don't restore some info in settings files created with older app versions.

## 2023.01.03
### Added
- Ford Mustang vehicles are now officially supported.  Just need someone with a Mustang to test it out...

### Changed
- Changes to Manage Vehicles activity when entering a new VIN:
-- the user must enter a 17-character alphanumeric entry before being allowed to add the vehicle.
-- when a vehicle type is recognized, it is displayed.

## 2022.12.26
### Changed
- A new interface has been added for managing the vehicles monitored by the widget.  This is due changes in portions of
  the FordPass API.  Look at "Manage Vehicles" in the three-dot menu, and click on the "+" icon to add a new vehicle.  If you've 
  been using the app in the past, you may need to login with your FordPass credentials first for everything to work again.

## 2022.12.07
### Added
- Display explanation about unauthorized third party apps.
- Implement a "hibernation" setting that disables automatic API usage until Ford releases a "public" API.

### Changed
- Remove use of stored credentials.
- Change minimum update interval to 15 minutes.

## 2022.11.04
### Added
- Ford F-350 vehicles are now recognized (uses the F-150 silhouettes).
- Any other Ford truck built in North America should generically be recognized as a regular cab F-150.

### Changed
- Disable support related to OTA info until a new method for retrieving is available.  Any formerly retrieved
info is stored, but nothing is visible in the app or widget.

## 2022.10.29
### Added
- Ford F-250 vehicles are now recognized (uses the F-150 silhouettes).

## 2022.10.06
### Fixed
- Changes to make downloading vehicle images work again. 

## 2022.09.10
### Added
- Display notifications if the FordPass account becomes disabled.

### Changed
- Remove check for OTA information until a new method for retrieving is available.  The app and widget will still show the last stored info.

### Fixed
- Fix bug for possible out of range index when determining vehicle color from images

## 2022.09.05
### Changed
- Made a minor tweak to Mach-E images.

### Added
- Display more informative notifications when new OTA information is found.

### Fixed
- Fix more null pointer exceptions.

## 2022.08.29
### Fixed
- Fix null pointer exception when trying to authenticate the user's account in the background.

## 2022.08.25
### Fixed
- Correct issue with dark mode not working in webviews.

## 2022.08.23
### Changed
- Added setting to disable checking status when Do Not Disturb is active.
- Display additional info in OTA Check activity.

### Fixed
- Fix bug when restoring recently-saved settings.
- Fix more null pointer exceptions caught by Google Play Store.

## 2022.08.20
### Added
- A notification will alert you to log in should the app be unable to refresh the authentication token.

### Changed
- Don't show low tire pressure warning notification if status reports "unknown".
- Various modifications to support Android 13.

## 2022.08.13
### Fixed
- Fix some null pointer exceptions caught by Google Play Store.

## 2022.08.11
### Added
- Per user suggestion, add a reminder to plug in vehicle (PHEV or BEV) if charge level is below a user-defined threshold.

## 2022.07.31
### Changed
- Identify vehicles as ICE, PHEV, or BEV using status information.  Note: if you notice the wrong info displayed in the widget, enable "Use older methods to determine fuel type"
  in Settings and file a bug report on GitHub.

### Fixed
- Fix some null pointer exceptions caught by Google Play Store.

## 2022.07.21
### Fixed
- Open windows and doors were not being displayed on the widget.
- Some North American vehicles were incorrectly identified as PHEVs.

## 2022.07.20
### Added
- Support for Kuga vehicles (cousin of the Escape).  Thanks to @consp for contributing!
- Initial support for Puma vehicles.  There are currently no known users, so if you are a Puma owner and have issues, please submit a bug report.

### Fixed
- Miniature widget was not drawing lock icon for some vehicles when color vehicles was enabled.
- More bugs caught by Google Play Store.

## 2022.07.18
### Fixed
- Fix various bugs discovered from Google Play automated feedback.

## 2022.07.16
### Added
- Use stored vehicle images to help set widget vehicle colors.  A color is automatically assigned on initial use, but user can also choose "Auto" in the Set Vehicle Color activity 
  to use the automatic selection results.  Note: this feature will only work if the necessary images are available on Ford's servers.
- Allow user to choose whether the full-size widget displays a generic icon or vehicle images.

### Changed
- Improve instructions for the ignition and alarm icons.

## 2022.07.13
### Added
- Ford Expedition vehicles are now recognized (uses the Explorer silhouette).

### Fixed
- Corrected bug with initially not drawing silhouette when vehicle color not enabled.  

## 2022.07.11
### Added
- Per user request, vehicles can now be displayed in color in the widgets.  A color picker, written by @skydoves (https://github.com/skydoves/ColorPickerView), is used to choose
  the color for each vehicle. You can also choose a light or dark "wireframe" overlay. 

## 2022.07.02
### Fixed
- Fixed bug processing first log-in attempt.

## 2022.07.01
### Fixed
- New authentication code for changes in Ford's login API.  Note this is a work-in-progress, and you may need to attempt logging in multiple times for it work. 

## 2022.06.26
### Added
- Ford Edge vehicles are now officially supported.

## 2022.06.23
### Fixed
- Fixed bug where window status was not updated correctly in full-size widget.
- Improved the contrast of various UI items

## 2022.06.21
### Changed
- Each widget now separately tracks a vehicle.  This means if you have multiple vehicles, you can have a widget for each.
  Related to this....
- The "OTA Update Info" now uses a pull-down menu to specify the vehicle being viewed (only visible to for multiple vehicle
  accounts).
- You can manually "force" a refresh for a vehicle from each widget.  Since this has the potential to impact the 12V
  battery, the app limits the number of times you can do this within a 24 hour interval.

### Fixed
- Corrected issue when attempting to clear OTA status notification.

## 2022.06.20
### Fixed
- Updated to new Ford API for authentication; the older API stopped working today.  A refresh should
  fix the issue; if not, a log in will be required.

## 2022.06.17
### Changed
- Allow smaller widgets to be resized vertically as well as horizontally.

## 2022.06.16
### Changed
- Allow smaller widgets to be resized so they will look correct on your screen.
- Reduce the size (in bytes) of some vector assets to take up less storage.

## 2022.06.14
### Added
- Per user requests, there are now two smaller fixed-size widgets.  Due to their smaller sizes,
  some of the touch/tap interaction has changed.  See the instructions for details.
- There is a new "Manage Vehicles" activity under the three-dot menu for users with multiple vehicles.  This
  allows you to control which vehicles are displayed.

### Changed
- Vehicles built before 2018 are disabled by default, since that's roughly
  when FordPass Connect was designed.

### Fixed
- When multiple vehicles were present, any vehicle which didn't support
  status updated caused the widget to not update.
- The "enable battery optimization notification" title in Settings was not wrapping.
- The access token was not being refreshed on failure of reading user vehicle information.

## 2022.06.05
### Fixed
- Disabled battery notification

## 2022.06.04
### Added
- If battery optimization is on, a notification will appear prompting you to turn it off.
- Ford Edge VINs are now recognized (no silhouette yet).

### Fixed
- The "Force updates" feature was incorrectly calculating using minutes instead of hours; this would really
have the potential to drain your 12V battery.

## 2022.06.03
### Added
- Added better support for PHEVs in the widget.  You can switch between charge or fuel info with a single tap on the progress bar below the status image.
- Refresh the list of vehicles from the Ford account list every hour.

### Changed
- Create a generic instruction page for all vehicles, including a link to FAQ.
- Remove support for ZIP files with settings.

### Fixed
- Various typos in instructions 

## 2022.05.31
### Added
- Ford Escape vehicles are now supported.
- EVs/PHEVs will send a notification when charging is completed.
- Support for reading and writing of external storage in Android 9.
- Experimental "Force updates" feature (under Settings) which will force vehicles to send an update if the last
  refresh is 6 or more hours old, as long as the vehicle is not in deep sleep and the 12V battery is
  at 13V or higher.  NOTE: this may result in a drain on the 12V battery.

### Changed
- The colors used in the app's theme were changed to blue from purple.
- An internal change to the how internal states interact on log-in.

### Fixed
- Correct a typo in Settings description (thanks @mikebaz).

## 2022.05.25
### Changed
- Temporarily allow unsupported vehicles to be recognized by the app.  The widget will use the Mach-E silhouette for these vehicles.  If your vehicle isn't supported,
  please upload logs showing car status and OTA information to GitHub.
- Use different logic for determining when a vehicle doesn't support OTA updates.

### Fixed
- Database bug which caused entries in the vehicle database to have missing information. 
- Attempt to download alternative vehicles images if the first attempt returns an error.

## 2022.05.20
### Fixed
- Widget should now show units of measure which match your FordPass settings.

## 2022.05.12
### Changed
- The app has been renamed to "Ford Status Widget" in preparation for its eventual release on the Google Play store.
- The app no longer changes icons to reflect the vehicle model.  Supporting this raised a few issues.  If there is interest, I may 
  add a "Settings" option to allow the user to select the icon.
- Saved settings now use a JSON file instead of a ZIP file. You can still restore from a ZIP file for the time
being, but as I believe there are reliability issues when overwriting files this functionality will be removed in the
  near future.  If you use this feature, I highly recommend saving your settings now and removing the older ZIP files.
- The settings, logs, and logcat filenames all start with *fsw_* for consistency, and include the date/time.

## 2022.05.02
### Changed
- Changed the "Update App" menu selection to "Check For Update", and make it immediately check for an update.  If one is found, a notification is created.

### Fixed
- Numerous null pointer checks and additional log output from crash logcats.
- Fix timing issue when switching between vehicle profiles.

## 2022.04.29
### Added
- Your vehicle's image and nickname are now displayed in place of the vehicle logo in the widget.
- You no longer need to enter a VIN when logging into your Ford account.  Once logged in, the app retrieves a list of all supported vehicles and automatically tracks them.
- The way profiles works has changed; for now, only one Ford account can be used at a time.  However, if you have multiple vehicles under that account, you can now switch
  between them by tapping on the vehicle's image.
- If the app crashes, the logcat output for the crash is automatically saved to your Download folder when you restart the app.
- You can view/change the battery optimization settings from the Settings menu.

### Changed
- The ZIP file created when storing user data now contains versioning information.  When significant changes are made to the app, it may not be possible to restore 
  user data from older versions; this is true for this release.
- Log files now have a maximum size limit of 1.5MB.  This is accomplished by using a primary and backup log file of max size 750KB each.  When you save the log file
  to your Download folder, these files are combined into a single file.

### Fixed
- Your Ford user profile information (name, address, phone, etc.) is redacted from log files.

## 2022.04.06
### Fixed
- Changes to the code for the Update activity.  The app will check that it has permission to install from unknown sources, and if the user does
  not grant this permission a toast message will display explaining that these permissions are necessary.  Since this code doesn't work in
  2022.04.04, you will need to manually sideload the apk from [GitHub](https://github.com/khpylon/MachEWidget/blob/master/app/release/app-release.apk?raw=true) this time
  (use this clickable link).

## 2022.04.04
### Added
- Initial support for Ford Explorers SUVs (tell your friends!).  Many thanks to @FrankThompson for being the beta tester.
- A smaller widget is now supported; this widget only show the vehicle image and associated icons.  It's a work in progress; you may end up with 
  some "dead space" to the left side. 
- If a VIN is not recognized upon log-in, the app will display a notice that things may not work correctly and encourage the user to upload data
  to a new Issue on GitHub.
  
### Changed
- Related to the addition of Explorer support, more of the code was changed for recognizing information about each vehicle from its VIN.  Heads up; if you 
notice something not right, *open an issue on GitHub*.

### Fixed
- Profiles should work again; a number of issues were discovered and fixed.

## 2022.03.31
### Added
- An Update Activity has been created.  When a new version of the app is pushed to GitHub, you will get a notification as before; however, when you click on the
notification, an activity will start that lets you read the change log and lets you install the new version.  You can also access this screen
  from the three-bar menu (note that this will *not* check Github for a new update, only reflect information already found there).  The frequency of checking for updates
  has also be changed to once an hour.

### Changed
- Various changes in the networking code to handle retries and log less information for known exceptions.

### Fixed
- Recognize European Mach-E VINs starting with **WF0**.  If your Mach-E's VIN starts with something other **3FM** or **WFO**, please create a new issue and include your VIN except for the last 5 digits.

## 2022.03.21
### Added
- Remote stop command is supported now.  Double-tap on the ignition icon to start or stop.  Once remote start is initiated, the ignition icon will turn yellow to indicate this.

### Changed
- Commands (lock/unlock and remote start/stop) now poll to determine whether the command was completed successfully
- Internal changes for tracking status of Ford server connection.  Hopefully these will improve the performance of automatic updates for the vehicle's status.

### Fixed
- Fuel levels capped at 100%.

## 2022.03.10
### Changed
- Make the odometer output match the value reported by FordPass.

### Fixed
- Correct an issue with determining the vehicle type (only affects F-150 and Bronco owners).

## 2022.03.09
### Added
- Add the ability to save and restore user data (Android 10 or higher).  Data is stored in a ZIP file in the Download folder and 
  can later be loaded back.
- When using stored credentials, the Log-in page now allows you to re-use your username/password/VIN with
  fingerprint biometrics.
- Additional support for Bronco SUVs.

### Changed
- Reorganized the content of the OTA Info page.
- Try storing/reusing distance-to-empty and fuel level values on F-150s and Broncos when the status reports invalid numbers (experimental).

### Fixed
- Some inconsistencies within the logging output code.

## 2022.03.03
### Added
- Add the ability to save logging output to a file (Android 10 or higher).  To use this, enable logging in Settings, then when
  ready to save data, choose "Save logfile" from the three-dot menu.  The file will be stored in yor Downloads
  folder.  After saving, disable logging.
- Initial support for Bronco SUVs.  This is very beta; while there is an image of the vehicle, it does
  not yet update correctly.

### Changed
- Use the technique for drawing the F-150 images with the Mach-E.

## 2022.03.02
### Changed
- Change the way F-150 images are drawn, and use SVG drawables instead PNG images.

## 2022.02.28
### Changed
- Added support for Android 9 devices.  This has not been thoroughly tested.

### Fixed
- Crash on fresh install when checking VIN.

## 2022.02.26
### Changed
- Automatically determine which widget and icon to display (Mach-E or F-150) on the current VIN.
- After issuing a command (lock, unlock, or remote start), attempt to update the car's status after a few seconds.
- Include specific drawables for F-150 SuperCab, SuperCrew, and Raptor.

## 2022.02.22
### Added
- Initial support for Ford F-150 trucks.  To use this, toggle "F-150 Mode" under "Settings".  Note that this
  will also change the name of the app to "F-150 Info", although the widget will still appear as "Mach-E Info"
  when selecting widgets for your phone's home screen.
- Add a developer's option for generating verbose HTTP information to log files.  This should only be used when
  gathering debugging info to the developers; this output may contains usernames and password for your FordPass account.

### Changed
- Add a toggle switch for storing credentials to the log-in screen.

## 2022.02.17
### Changed
- Add an option (under "Settings") to store FordPass login credentials (username and password). These are
  stored encrypted, and are only used if the access token is lost and the app needs to log into the Ford
  servers again.  When this option is disabled, they are removed from the stored files.

## 2022.02.11
### Changed
- Adjust size of assets in the widget to make it fit better on the screen.

### Fixed
- Various changes in stored data to address issues with incorrect info being stored.

## 2022.02.09
### Added
- Allow units to be chosen instead of relying on the FordPass settings.
- Clicking on the "New update" notification open the GitHub repo.

### Changed
- Logic for when to refresh access token.

## 2022.02.08
### Added
- Widget should allow resizing, so you can fit to your available space.
- "About" moved from menu to Settings; it includes a link to the GitHub repo.

### Fixed
- Corrected an exception which caused the last OTA date to be displayed incorrectly.
- Corrected some typos in the instructions.

## 2022.02.06
### Added
- Support for multiple user profiles.
- Alert user when a new version of the app is available.

## 2022.02.01
### Added
- Allow display of OTA status and location to be disabled on the widget

### Fixed
- Incorrect display of last OTA status in the OTA Update Info activity.

## 2022.01.30
### Added
- Support for locking/unlocking doors and remote start by double-clicking on their icon.
- Allow choice of how to see last refresh time information.
- Allow user to enable/disable linked app widget buttons.
- Display information below estimated range when plugged into charger.

### Changed
- Change format of displayed dates.
- Limit manual refresh to once every five minutes.
- Remove parent activity for Settings (this avoids needing to go back through the main activity when "Settings" is pressed in the widget).

### Fixed
- Icons without correct transparency.
