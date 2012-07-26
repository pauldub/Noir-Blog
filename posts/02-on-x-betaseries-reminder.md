Microsoft's automation tool for Android is really a promising one, it allows you to program your phone with Javascript and it already has a nice API. I've built a reminder that will tell me if there are any new episode of the shows I track using BetaSeries.com

## Dead simple !

I won't cover registering at [on{x}](http://www.onx.ms/) and installing the Android application, what you will really need is giving an eye to the [documentation](https://www.onx.ms/#apiPage), it's clear and has helpful examples.

### So what is the idea ? 

I enjoy whatching TV shows as they go out, and I use [BetaSeries](http://www.betaseries.com/) to track episodes and download subtitles, fortunately BetaSeries generates a planning of the upcoming new shows. I wished my phone showed a reminder every morning if there are any episodes out on this particular day.

To achieve this goal I first got a look at the BetaSeries's API, once again it's a simple one, all we have to do is : 

 1. Register for an API key here.
 2. Use the method [_/planning/member[/username]_](http://www.betaseries.com/wiki/Documentation#cat-planning) to get our planning. 
 3. Let the phone show a notification if a new show is available today.
 4. Tell the phone to do that every morning at 9.00 AM.

## Sauce :

Well now that we defined the recipe's behaviour and as it is yet again a simple one, I will post the whole source for it and explain things as they go.

> The first thing I did was to create a simple __Object__ to hold API call's parameters and such allowing on to use the recipe by only changing a few variables. It only has a function __url()__ that returns the URL we want to use to get our planning depending on the other members of the object.

	var config = {
	    server: 'http://api.betaseries.com/',
	    method: 'planning/member',
	    username: 'paul.dub',
	    format: 'json',
	    key: 'THE_KEY',

	    url: function() {
	        return this.server + this.method + '/' + this.username + '.' + this.format + '?key=' + this.key;
	    }    
	};

> Then we define the actual task to be ran, it creates a notification, queries the BetaSeries API using our parameters and then process the response to update the notification which when you touch/click it will show more informations about today's shows.

	var betaSeriesReminder = function() {    

> We create the reminder with [__device.notifications.createNotification(String title)__](https://www.onx.ms/#apiPage/Notification) and store it in a variable that we will update later.

	    var reminder = device.notifications.createNotification('Querying for new shows...');

> Now we use [__device.ajax(Object args, Object onSuccess, Object onError)__](https://www.onx.ms/#apiPage/httpClient) to query for the BetaSeries's API.

	    device.ajax(
	        {
	            url: config.url(),
	            type: 'GET'
	        },
	        function onSuccess(body, textStatus, response) {
	            var parsedBody;
	            
	            //We parse the response into JSON.
	            if(!(body && (parsedBody = JSON.parse(body)))) {
	                var error = {};
	                error.message = 'invalid body format';
	                error.content = body;
	                console.error('error: ',error);
	            }

	            //If there are no errors
	            if(parsedBody.root.code == 1) {
	                var planning = parsedBody.root.planning;
	                var planningLength = 0;
	                
	                var index = null;

	                var todayShows = [];    //Holds show that are out today.
	                
	                for(index in planning) {
	                    planningLength++;
	                    //We check that the show is not in state 'To Be Announced' (Game of Throne for example...)
	                    if(planning[index].date != 'TBA') {                     
	                        var showDate = new Date();
	                        showDate.setTime(planning[index].date * 1000);
	                        
	                        //We compare the parsed show date with today's date and add the show to todayShow[] 
	                        //if it's going to happen today
	                        if(showDate.getFullYear() == now.getFullYear() 
	                        && showDate.getMonth() == now.getMonth() 
	                        && showDate.getDate() == now.getDate()) {
	                            todayShows.push(planning[index]);
	                        }
	                    } 
	                }
	                
	                //Setup the click handler for the notification.
	                reminder.on('click', function() {
	                    var box = device.notifications.createMessageBox('Today shows');
	                    
	                    if (todayShows.length > 0) {
	                        if(todayShows.length == 1) {
	                            box.content = "A new episode is out : \n";
	                        } else {
	                            box.content = todayShows.length + " episodes are out : \n";
	                        }
	                        
	                        var index = null;
	                        //We loop throught todayShows[] and adds the information to the message box.
	                        for(index in todayShows) {
	                            box.content = box.content + "Title : "  + todayShows[index].title + "\n";    
	                            box.content = box.content + "Show : "  + todayShows[index].show + "\n";    
	                            box.content = box.content + "Number : "  + todayShows[index].number + "\n";    
	                        }
	                        
	                    } else {
	                        box.content = "I told you, there are no new episode to watch today...";
	                    }
	                    
	                    box.buttons = ['Ok'];
	                    box.show();
	                });
	                
	                if (todayShows.length === 0) {
	                    reminder.content = 'Nothing to watch today...';
	                } else {
	                    reminder.content = 'I\'ve found ' + todayShows.length + ' new show(s)  !';
	                }
	                reminder.show();    
	            } else {
	                //An error occured on BetaSeries side so we update the notification to show it.
	                reminder.content = 'An error occured on the BetaSeries API [code:' + parsedBody.root.code + ']';
	                reminder.show();
	            }     
	        },
	        function onError(textStatus, response) {
	            reminder.content = "An error occured while querying BetaSeries";
	            reminder.show();
	        }
	    ); 
	};

> Finaly we tell the phone to run our task at a given time by using [__device.scheduler.setTimer()__](https://www.onx.ms/#apiPage/scheduler) to do so we create two __Date()__ objects : _now_ to hold current time and _timeStarter_ to hold the time we would like the task to be ran at, and set the parameter _interval_ to _'day'_ so it's ran daily.

	var hours = 9;
	var minutes = 0;
	
	var now = new Date();
	var timeStarter = new Date(now.getFullYear(), now.getMonth(), now.getDate(), hours, minutes, 0, 0);

	device.scheduler.setTimer({
	    name: 'dailyBetaSeriesCheck',
	    time: timeStarter.getTime(),
	    interval: 'day',
	    repeat: true,
	    exact: true
	},
	betaSeriesReminder);

> The last two lines of code allow easy testing of the recipe, we set the task to be ran on __screen unlock__ and __emit__ an __unlock__ signal to run the task whenever you clik "Save and send to phone". They should be commented if you don't need notification spam.

	device.screen.on('unlock', betaSeriesReminder);
	device.screen.emit('unlock');