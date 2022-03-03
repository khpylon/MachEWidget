# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
but at this time the project does not adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2022.03.03
### Added
- Add the ability to save logging output to a file.  To use this, enable logging in Settings, then when 
ready to save data, choose "Save logfile" from the three-bar menu.  The file will be stored in yor Downloads
  folder.  After saving, disable logging.  This only works on Android 10 and later.
- Initial support for Bronco SUVs.  This is very beta; while there is an image of the vehicle, it does
not yet update correctly.

### Changes
- Use the technique for drawing the F-150 images with the Mach-E.

## 2022.03.02
### Changes
- Change the way F-150 images are drawn, and use SVG drawables instead PNG images.

## 2022.02.28
### Changes
- Added support for Android 9 devices.  This has not been thoroughly tested.

### Fixed
- Crash on fresh install when checking VIN.

## 2022.02.26
### Changes
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

### Changes
- Add a toggle switch for storing credentials to the log-in screen.

## 2022.02.17
### Changes
- Add an option (under "Settings") to store FordPass login credentials (username and password). These are
  stored encrypted, and are only used if the access token is lost and the app needs to log into the Ford
  servers again.  When this option is disabled, they are removed from the stored files.

## 2022.02.11
### Changes
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
