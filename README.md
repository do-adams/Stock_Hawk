# Stock Hawk
Take stock of your financials, anywhere you go - with Android.

![Stock Hawk Preview Image](/Stock Hawk Preview Image.png?raw=true)

Whether you're feeling bullish or bearish this week, Stock Hawk is with you. Get the latest updates on your favorite stocks,
see bid prices over time, and open up some space on your home screen because this time your stocks are coming with you, and they're not
leaving your sight! 

# Behind the Scenes

Built during July 2016 as part of completing the [Advanced Android Development](https://www.udacity.com/course/advanced-android-app-development--ud855)
course at [Udacity](https://www.udacity.com/), this project represents a major iteration on the original 
[Stock Hawk Udacity Application](https://github.com/udacity/StockHawk). As a solo developer, I was presented with a
functional but unfinished and unstable version of the application that uses the (hidden) Yahoo Finance API and Google Cloud Messaging to fetch stock data, and it was up to me to:

  * Present the user with a graphical chart that describes bid price stock history in a clear and informative manner.
  * Fix a major bug issue in which an invalid stock search caused the application to crash.
  * Add material design alert dialogs to notify the user in the event of a lost connection and outdated stock data.
  * Create and wire a responsive widget that allows the user to enjoy fresh, periodically-updated stock data on his home screen.
  * Implement RTL layout mirroring support for different user languages. 
  * Provide content descriptions for all UI elements and stock data, for the benefit of sight-impaired users.
  * Extract and refactor the usage of strings and string resources throughout the code, to allow for 
     easier translation efforts in the future and improved code readability.

I believe I accomplished all of these and more. Throughout the undertaking of this project I strove to build upon and improve the
logic and design of the codebase and respect the original spirit of the code, to the end that anyone familiar with the original project
can go over my iteration effortlessly and follow the improvements in a very natural way.

# Getting Started 

Before attempting to build and run the project and in order for the Google Cloud Messaging service to work properly, 
you should provide your own "google-services.json" configuration file in your "app/" or "mobile/" directory. 
More info about this [here](https://developers.google.com/cloud-messaging/android/client) and [here](https://developers.google.com/cloud-messaging/network-manager).

# Libraries

This project makes extensive use of the [OkHttp](https://github.com/square/okhttp), [Google Cloud Messaging](https://developers.google.com/cloud-messaging/), 
[Schematic](https://github.com/SimonVT/schematic), [Material Dialogs](https://github.com/afollestad/material-dialogs), 
[FloatingActionButton](https://github.com/makovkastar/FloatingActionButton), and [Android Db Inspector](https://github.com/infinum/android_dbinspector) 
libraries. 
