#Weather Data Generator
#####Geographical Area under consideration 
Indian subcontinent - mainly India and Sri Lanka.
Size of area considered will be approximately half the size of Australia.
#####Factors influencing the climate of area considered
Indian subcontinent is surrounded by Indian ocean and it has Western Ghats(mountain) and Himalayas. Due to this geographical positioning, proximity to ocean and the topology varaiations, two monsoon monsoon seasons - 
Southwest monsoon and Northeast monsoon occur. These monsoons are the main dynamic factors incfluencing the climate of Indian subcontinent.
##Approaches considered for solving the problem
###Approach 1 - Based on historical data
Retrieve the historical weather data of past 10 years from a reliable source for the selected weather stations. 
Derive a regression equation based on the history data and predict the current value

######Pros:
* Easy to implement
* Better accuracy

######Cons:
* As the problem statement clearly mention to consider geographic,topographic,oceanographic factors,the assumption is that this is **not** the expected approach for problem resolution
* The model will not incorporate the requirements of considering the dynamic factors which evolves over time based on geography


###Approach 2 - Mathematical modelling of weather data

 - Mathematical modellling of the parameters affecting weather is created.
 - Influence of both static parameters(topography, geography, proximity to sea etc.) and dynamic parameters(monsoon clouds from Indian ocean, cold waves from central Asia) can be included in the model.
 <a href="url"><img src="/images/Cloud Movement.png" align="center" height="500" width="575" ></a>
 - The area under which the monsoon clouds are present is represented as a circle in the above diagram. 
 - The cloud (circle)movement path towards Northeast India is indicated as red lines.
 - **The approximate line equation of the cloud movement path, from the centre of circle to Himalayas is derived .**
 - The weather stations which come inside/near the circle can be assumed as affected by the monsoon clouds. 
 - Similarly we can model the movement of cold waves occur in North India during winter season.
 

######Cons:
- The line equation (center of circle to Himalayas) has a dependency on Time. So the final derived equation will be a complex quadratic equation which depends on (lattitude, longitude, month of the year). 


###Approach 3 (Implemented Approach - combination of approach 1 and 2) 

* __Step 1__-
 * __Step 1a__ - Retrieve the monthly average minimum and monthly average maximum of temperature and humidity of required weather stations from a reliable source. Load the data to model.
 * __Step 1b__ - As the mathematical modelling of cloud movement (mentioned in approach 2) will take multiple days for implementation,the probability range of monsoon clouds (each month) in different stations are configured, based on the movement pattern of monsoon clouds.
 * __Step 1c__ - Load the lookup table of pressure and altitide- represents the pressure variation with respect to altitude above sea level.
* __Step 2__ - Initialize the clock with the command line argument. Date should be given as command line argument in the format 'YYYY-MM-DD'. The clock will be incremented by **1 hour**
* __Step 3__ - Generate RANDOM Monsoon Cloud Probability at each weather stations within configured range for the month

 - Every hour generate a random cloud probabaility within the monthly range.
 - Example for probability range configuration of monsoon clouds - In Kochi(COK) the monsoon clouds will be present during the months    June - September, so the probability of clouds in Kochi can be configured between 0.5 to 1 during rainy season and 0 to 0.5 during    other months.
* __Step 4__ - Calculate temperature based on the time(hour) of the day. 

  - The general pattern of diurnal temperature variation is  that temperature increases from 4AM in morning till 1PM noon, and after 1PM it reduces till 4AM in morning(next day). 
  - A graph is plotted based on the above pattern and the monthly average minimum temperature and monthly average maximum temperature collected in step 1.
  - ![Graph-Temperature Vs Time](/images/Temperature Vs Time.png)
  - From the graph, the slope m1 and intercept c1 are calculated for the time between 4AM and 1PM during which temperature is rising.
  - The slope m2 and intercept c2 are calculated for the time between 1PM and 4AM(next day) during which temperature is reducing.
  - The equation of 2 lines are obtained using the above values.
  - Equation for time between 4Am and 1PM : y=m1*x+c1
  - Equation for time between 1PM and 4AM(next day) :  y=m2*x+c2
  - The temperature values at any time of day can be calculated by substituting the time in 24 hour format as x. For calculating the temperature between 12AM to 4AM(next day), we have to add 24 to the hour.  The y value thus calculated using the equations give the temperature at that particular time of the day.

* __Step 5__ - Calculate temperature adjustment due to clouds
 - When the probability of monsoon clouds is between 0 and 0.5, there is no temperature adjustment as there is no much influence of clouds on temperature.
 - When the probability of monsoon clouds is between 0.5 and 0.75, there is considerable impact for clouds on the temperature. So temperature adjustment has to be done based on the time of the day.
    - During day time, the presence of clouds results in a decrease in temperature. So temperature should be reduced by a factor during day time.
    - During night time, the presence of clouds results in increase in temperature. So temperature should be increased by a factor during night time.
 - When the probability of monsoon clouds is between 0.75 and 1, there is a maximum probability of raining. So the temperature should be decreased by a factor.
* __Step 6__ - Calculate actual temperature as a weighted sum of temperatures calculated in step 4 and step 5.
* __Step 7__ - Determine the weather conditions based on the probability of monsoon clouds and actual temperature calculated in 
    step 6. 
    - The graph below represents the weather condition based on probability of monsoon clouds.
 
    -![Graph-Weather condition - Cloud probabilty mapping](/images/Weather Condition and Cloud Probability.png)

    - If the actual temperature calculated in step 6 is less than 0 then weather condition is 'Snowy'
* __Step 8__ - Calculation of atmospheric pressure in hPa.
  - Atmospheric pressure is considered as a dependent variable of Elevation, Temperature and relative humidity. 
  - A look up table of pressure and altitide which represents the pressure variation with respect to altitude above sea level is used for this. 
  - The elevation in hectofeet(1 hectofeet=100 feets)at a weather station is looked up in the table to obtain the atmospheric pressure in hPa.

 
##Architectural Design Considerations Taken Care
* Ability to generate weather data in realistic fashion by taking into account dynamic factors like monsoon clouds and cold waves which varies across seasons and geography.
* Ability to add new weather stations and its configuration data without rebuilding the code.
* Fine grained control of weather influencing parameters of each station across seasons
* Ability to plug in future enhancements - Instead of manually configuring dynamic parameters like monsoon cloud movement, probability of clouds can be generated using mathematical modelling.

##Configuring weather influencing parameters
1. Perform mvn install at the project base folder
2. Folder name 'config' will be generated in the project base folder
3. Edit 'stations.json' to add a new station
4. Under config/weather, add a new folder with IATA code(as folder name) which contains weather details
5. Under config/cloudProbability, add a file which contains the monsoon cloud probabilities of the new station for all months

##Running weather data generator
1. Change directory to project base folder and run *java -jar WeatherGenerator.jar* 
2. Start date of the clock can be send as a command line argument in format YYYY-MM-DD

##Number of unit test cases successfully executed : 77
Configuring unit test data (Station named TEST is added with dummy weather data) :
- Under [WeatherGenerator/src/main/config folder], edit [test_stations.json] file
- Under [WeatherGenerator/src/main/config/weather/TEST] folder, edit [temperature.json] file
- Under [WeatherGenerator/src/main/config/cloudProbability] folder, edit [TEST.json] file

##Performance testing results 
- Number of records generated ( 10 stations, 1 year, every hour) : **87600**
- Time to generate 87600 records              : **566 msec** 

(Testing done on Intel Core i5 - 2.8GHz )

 


##Future enhancements
* Due to time limitation, the relative humidity calculation is pending and can be included later
* Due to time limitation, adjustment of atmospheric pressure based on temperature and humidiy is pending.
* The monsoon cloud probability can be generated automatically(instead of manually configuring) by mathematical modelling of cloud movement
* Other weather influencing parameters like cold waves from central asia can also be incorporated in the model
