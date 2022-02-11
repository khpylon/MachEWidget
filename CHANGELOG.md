# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
but at this time the project does not adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
