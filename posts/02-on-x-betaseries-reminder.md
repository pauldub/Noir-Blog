Microsoft's automation tool for Android is really a promising one, it allows you to program your phone with Javascript and it already has a nice API. I've built a reminder that will tell me if there are any new episode of the shows I track using BetaSeries.com

## Dead simple !

	//The hour and minutes the recipe should be ran.
	var hours = 9;
	var minutes = 0;

	var now = new Date();
	//A date object to represent the hour the recipe should be ran.
	var timeStarter = new Date(now.getFullYear(), now.getMonth(), now.getDate(), hours, minutes, 0, 0);

	//A handy configuration object to easily modify the API call parameters.
	var config = {
	    server: 'http://api.betaseries.com/',
	    method: 'planning/member',
	    username: 'paul.dub',
	    format: 'json',
	    key: 'c50d03e2c570',

	    // Returns the final url we want to use.
	    url: function() {
	        return this.server + this.method + '/' + this.username + '.' + this.format + '?key=' + this.key;
	    }    
	};

	//The task that will be ran, it creates a notification, queries the BetaSeries API 
	//using our parameters and then process the respnse to update the notification which
	//on click will show informations about today's shows.
	var betaSeriesReminder = function() {    
	    var reminder = device.notifications.createNotification('Querying for new shows...');

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

	//And finally we setup scheduler to run our task.
	device.scheduler.setTimer({
	    name: 'dailyBetaSeriesCheck',
	    time: timeStarter.getTime(),
	    interval: 'day',
	    repeat: true,
	    exact: true
	},
	betaSeriesReminder);

	// To test the reminder uncomment following lines. 
	//device.screen.on('unlock', betaSeriesReminder);
	//device.screen.emit('unlock');