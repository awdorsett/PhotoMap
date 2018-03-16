# PhotoMap
The purpose of the application is to allow the user to map the pictures on their phone to locations on Google Maps. This makes the experience of sharing travel photos more immersive. It also allows the traveler to go back and explore their previous trips and see pictures they may have forgotten about. 

This is very much in beta at the moment as I explore and understand Android development as well as Google Maps APIs. There hasn't been much optimization and there may be issues with large amounts of photos. I've included a button on the main page to clear out the local database in case you need to start from scratch.

## Getting Started
The only thing you'll need to get started is a Google Maps developer API key. This should be placed in a resource xml file under the res/values folder with the name `google_maps_api.xml`. This should include the value:

`<string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR API KEY HERE</string>`

I'll work on putting a template in the repo.
This should allow you to compile and create an APK for your program.

## How It Works
The program works by reading the longitude and latitude from a photo's exif data. It then makes a call to Google Maps' reverse geocoding API which will return various information including a city name. Using that city it will generate a group object. Any image that is in that city will be associated with that group. The map then shows the groups as pins. When you click on a pin you'll launch a new activity to view that group's images. 

Note that pins will cluster together when zoomed out. As you get closer they'll uncluster into pins or smaller sub clusters. 

## Features
### Current Features
- Read location exif data from pictures stored locally on your phone
- Groups photos by city and display city groups as pins on map
- Clusters group pins and display count when zoomed out
- Clicking on city group pin displays associated images
- Displays list of groups on main page
- Clicking on a group on the main page takes you to the location of the pin on the map

### Planned/In-Progress Features
- Clean up code
- Optimize image loading including making it asynchronous and lazy
- Improve grouping to move away from cities and use more compatiable criteria
- Improve zooming UX so that clicking on a cluster will zoom until it expands
- Improve experience when clicking on a group on main page
- Enhance UI

### Future Features/Stretch Goals
- Allow user to get images from other sources (Google Photos, Facebook, etc)
- Show images that do not have location exif data and allow them to be manually associated with location
- Allow user to change image locations to existing group or manually select a location
