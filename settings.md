## Fixing the App's Settings Manually 

Recently Ford changed their API so that the app isn't able to gather information about your vehicles.  I plan to change the app to help you enter this, but in the meantime you can work around the issue by manually entering the necessary info.  To do so, you'll need a log file containing token information and a saved copy of your settings.   The settings file is a JSON- formatted text file.  If you don't have a JSON editor, I recommend using an online one such as https://jsoneditoronline.org/
Everything inside these sections is a key/value pair; sometimes the value is an array of values such as ["Boolean","true"].

1) Enable logging for the app.
2) Log in on the app with your FordPass credentials (yes, this will fail when attempting to get the user data, but we need to get the access tokens).
3) Save the log file.  Copy to your computer.
4) Save your settings.  Copy the JSON file to your computer.
5) Open the log file with a text editor.
6) Search for a line containing "*200 https://api.mps.ford.com/api/token/v2/cat-with-ci-access-token*", then a few lines
after it find the line with "*access_token*" and "*refresh_token*".  Save both  of these values.
7) Open the JSON file with an editor.  The JSON file has four major sections: *"prefs", "saveAppInfo", "users"*, and "*vehicles*".  The
"*users*" and "*vehicles*" may be missing; if so, you'll create them later.  Be sure they appear after the "*prefs*" and "*saveAppInfo*" sections.
8) Look in the "prefs" section for a key/value pair 
"*userId":["String","**somestring***"]
If it exists, copy the ***somestring*** value.  If it doesn't exist, create it and choose an alphanumeric string for the value.
9) Look for the "*users*" section.  This is the data about you, the user.  It is an array, but only needs one entry.
 - If this exists, replace the "*accessToken*" and "*refreshToken*" values with the ones you saved from the log file (note the keys are spelled differently), and change the "*userId*" value matches the one you found in Step 8.  Also set the  value of "*programState*" to "***HAVE_TOKEN_AND_VIN***"
 - If this doesn't exist, use the text below to create it after the "saveAppInfo" section. Use the "accessToken", "refreshToken", and
   "userId" values from Steps 6 and 8. Edit other fields (country,
   uomPressure, uomSpeed, etc) as appropriate.
> "users":[{"accessToken":"","refreshToken":"","userId":"","programState":"HAVE_TOKEN_AND_VIN","lastModified":"","expiresIn":0,"id":2,"country":"USA","language":"en-US","uomDistance":1,"uomPressure":"PSI","uomSpeed":"MPH"}],

10) Look for the "*vehicles*" section.  This is the data for your vehicles.  It is an array, so each vehicle needs its own entry.
- If this section exists, then for each vehicle change the "userId" value to the one you found in Step 8.
- If it doesn't exist, create an entry for each vehicle containing a "*VIN*" and "*userId*" key, as shown below (separate each entry with a comma):
> {"VIN":"your vehicle's vin","userId":"userId from Step 8"}
11) After you confirm the JSON formatting is OK, save the file then upload to your phone.
12) Wipe the app's data, or uninstall/reinstall the app.
13) Restore settings.
