# Mustang Mach-E Widget for Android

[![Donate](https://img.shields.io/badge/donate-paypal-green.svg?style=for-the-badge)](https://www.paypal.com/donate/?hosted_button_id=HULVHBSWXDU9S)

## Intro

This app/widget is based on the work of [Anthony (tonesto7)](https://github.com/tonesto7), which is in turn based on the earlier work of 
[David Schablowsky](https://github.com/dschablowsky/FordPassWidget), and also the work of [@DevSecOps](https://www.macheforum.com/site/threads/guide-android-mme-widget-more-complex.13588/)
.  It is not a complete implementation of the functionality of their widgets, as it:
- is only intended for the Ford Mustang Mach-E, as that's the only Ford I own.  As of Feb 2022 I'm extending capabilities to support F-150 and Bronco owners as well (feedback needed) 
- does not include all the function of tonesto7's Apple/iOS widget
- does not currently support accounts outside of the United States, although it may still work

<img src="app/src/main/assets/appwidget_sample.png" alt="Mach-E widget example" width="300" />
<img src="app/src/main/assets/appwidget_sample_f150.png" alt="F-150 widget example" width="300" />
<img src="app/src/main/assets/appwidget_sample_bronco.png" alt="Bronco widget example" width="300" />

My purpose for hosting this code on GitHub under the GPL v3 license is to provide a starting point for anyone who wants to extend its functionality.

## Downloads

Download the [app-release.apk](https://github.com/khpylon/MachEWidget/blob/master/app/release/app-release.apk?raw=true) file and sideload on your Android device.

## Requirements

- Android 9, 10, 11, or 12 (may work on earlier Android versions, but not tested)
- [FordPass account](https://sso.ci.ford.com/authsvc/mtfim/sps/authsvc?PolicyId=urn:ibm:security:authentication:asf:basicldapuser&Target=https%3A%2F%2Fsso.ci.ford.com%2Foidc%2Fendpoint%2Fdefault%2Fauthorize%3FqsId%3D1f0281db-c684-454a-8d31-0c0f297cc9ed%26client_id%3D880cf418-6345-4e3b-81cd-7b623309b571&identity_source_id=75d08ad1-510f-468a-b69b-5ebc34f773e3#appID=CCCA9FB8-B941-46AD-A89F-F9D9D699DD68&propertySpecificContentKey=brand_ford&originURL=https%3A%2F%2Fwww.ford.com%2F&lang=en_us&fsURL=https%3A%2F%2Fapi.mps.ford.com) (has to be working with FordPass app, and NOT MyFord Mobile)

## Features

- Will automatically fetch data from servers (default is every 10 minutes, but configurable)
- Shows recent OTA information
- Send commands to car to lock or unlock doors, and perform remote start
- Assign "short cuts" to other related apps such as FordPass, Waze, etc

## Known Issues & Limitations

As listed above, this app
- is only intended for the Ford Mustang Mach-E, as that's the only Ford I own
- does not include all the function of tonesto7's Apple/iOS widget
- is not guaranteed to support accounts outside of the United States
- if you are using the app with an F-150, it may be necessary to uninstall when upgrading to newer versions.  This is because the 
  app is registered by the name "Mach-E Info", and using with an F-150 aliases it under the name "F-150 Info" which can confuse
  your Android OS when it comes time to upgrade
  
## Bug Reports

If you discover something which doesn't work the way you expect, check first on the forums to see whether someone else has
reported a similar problem (or whether the app is actual working properly).  Also search under
["Issues"](https://github.com/khpylon/MachEWidget/issues) on GitHub to see if it's been reported.  If not, you will need to gather
some data to create a new bug report.  As of Version 2022.03.02 the app has some built-in support for gathering log information:

1. Under "Settings", activate "Enable logging"
2. Perform any actions you think will demonstrate the issue
3. Under the three-dot menu, select "Save logfile"; this will save a file named *mache_logfile.txt* or something similar in the *Downloads* folder of your
external storage
4. Deactive "Enable logging"
5. Create/append to an Issue and upload the file as an attachment

If the bug is something more serious that it isn't captured in these logs, you may need to capture some logcat output. 
A good summary of ways to get logcat output is
[described here](https://www.xda-developers.com/how-to-take-logs-android/).  If your issue seems related to network access
with the Ford servers, you should go to Settings and set "Enable verbose HTTP logging".  Note that this output may expose
your Ford account credentials (username and password), so before sending the logs be sure to remove this information.  There
will probably be a lot of unrelated information in the log output; the only lines of interest are those which contain the string "934TXS" and, if 
verbose HTTP logging is enabled, those which contain the string "OkHttp".

## To do

- Display information using more graphics and less text where possible.

## Credits

Thanks to [tonesto7](https://github.com/tonesto7/fordpass-scriptable) for his work on the new widget, and to
[dschablowsky](https://github.com/dschablowsky/FordPassWidget) for his work on the original widget, 
[d4v3y0rk](https://github.com/d4v3y0rk) for finding out the information about the ford api.
Thanks to [@DevSecOps](https://www.macheforum.com/site/members/devsecops.7076/) on the [Mach-E Forums](https://www.macheforum.com/site/) 
for his valuable input and help with updating the appearance of the widget and permitting the use of the assets from his app.
Additional thanks to [marco79cgn](https://github.com/marco79cgn) and [Tobias Battenberg](https://github.com/mountbatt) for your widgets.  Also, thanks to those
who have funded the project via PayPal so that I have funds to purchase additional resources to further enhance the app.

A number of the icons used in this app are derived from free ones found on [Icon8](https://icons8.com/); thanks to the original creators.

## Disclaimer

I was inspired to create this app for Android in order to see more information about my vehicle, and to learn new things about Android programming.
I am not employed by Ford, and this app is not supported by Ford. 
The API used can be changed at any time by Ford. 
I am NOT liable for any kind of damage (special, direct, indirect, consequential or whatsoever) resulting from the use of 
this app. 

## License

This code is released as open source software under the GPL v3 license: see the [LICENSE](https://github.com/khpylon/MachEWidget/LICENSE.txt) file in the project root for the full license text.
