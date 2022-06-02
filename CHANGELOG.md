# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
but at this time the project does not adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
