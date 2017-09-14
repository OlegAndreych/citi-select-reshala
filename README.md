# citi-select-reshala
Utility for optimal bonus selections in Citibank's Cashback Pro.

First of all you should create a file with data to analyze.
First row of this file should be an amount of selects you have.
All following rows should be a copy-paste of the table with your purchases.

Example:
3,252
13/09/2017BEAUTY SHOP RU1.00  23.00 RUB
13/09/2017GYM RU3,753.00  873.00 RUB
13/09/2017CAFFE RU412.00  96.00 RUB
13/09/2017CAFFE RU258.00  60.00 RUB

To calculate optimal subset of prchases you should run an app as following:
java -jar citi-knapsack-1.0-SNAPSHOT.jar /path/to/the/file/with/data

In the output you'll find an amount of select needed, an amount of money you'll get, and a list of value:cost pairs (in cents) with necessary amount of picks to compose optimal subset of purchases.

Example:
Parsing limit
Limit is 325200
Parsing rows
Rows has been parsed
Max ratio is 0.23
Amount of rows before filtering is 136
Amount of rows after filtering is 136
Amount of rows after grouping is 93
Solving started
Cost sum is 325200
Value sum is 75800
Recrord: [3900:16700], amount: 2
Recrord: [6300:27000], amount: 3
Recrord: [7600:32600], amount: 1
Recrord: [8300:35600], amount: 1
Recrord: [4200:18000], amount: 1
Recrord: [3400:14600], amount: 1
Recrord: [10900:46800], amount: 1
Recrord: [14700:63200], amount: 1
Solution has been found in 76484 millis
