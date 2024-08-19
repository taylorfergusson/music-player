# // AudioFrost Music Player

Bash script that allows the user to get and compare provincial COVID-19 info from a CSV dataset in order to understand the spread of the disease. The data.csv file contains a data visualization of COVID-19 in Canada, which can be analyzed. 

## Project Overview
This project provides a tool for analyzing COVID-19 data specific to Canadian provinces. It allows users to extract and compare key statistics, such as confirmed cases, deaths, and tests conducted, from a given CSV dataset. Users can analyze data over specific date ranges and compare results across different provinces. This script is useful for researchers, public health officials, or anyone interested in understanding the trends and impact of COVID-19 in Canada.

## Prequisites
- **Bash**: Must have Bash installed on a Unix-like environment.
- **awk**:  For internal text processing.


## Installation

To install the script, follow these steps:

```
git clone https://github.com/taylorfergusson/covid-analysis.git
cd covid-analysis
chmod +x covidata.sh
```

Ensure that the data.csv file is in the directory where the script is run.

## Usage Guide

### Procedures
- get: Allows the user to receive in an output file only the rows pertaining the specified province ID, and provides calculated information regarding the number of rows, average number of confirmed cases, average number of deaths, and average number of tests. 
- compare: Computes statistics for extracted rows and compares them to the statistics in the compare file (assumed to be formatted as an output file from the get procedure). The output file contains in order the rows from the new action, the rows from the compare file, the newly calculated statistics, the statistics from the compare file, then a comparative statistic. The comparative statistic provides the differences in row count, average number of confirmed cases, average number deaths, and average number of tests. The compare values are subtracted from the newly calculated values.

### Arguments
- -r: Switch indicating that the results should only be from the provided startDate and endDate.
- procedure: Specified action on the dataset, either "get" or "compare".
- id: Province Unsigned ID (pruid), unique identifier for each province and territory.
- startDate: Beginning year and month for range, in YYYY-MM format.
- endDate: Ending year and month for range, in YYYY-MM format.
- inputFile: Filename for CSV dataset to be analyzed, titled data.csv in this case.
- outputFile: Desired filename for output file, must be CSV format.
- comparefile: CSV filename of previously generated results, will be compared to new province.

### Script Syntax
```
./covidata.sh -r procedure id startDate endDate inputFile outputFile compareFile
```

### Legal Usage Examples
```
./covidata.sh get 35 data.csv result.csv
./covidata.sh -r get 35 2020-01 2020-03 data.csv result.csv
./covidata.sh compare 10 data.csv result2.csv result.csv
./covidata.sh -r compare 10 2020-01 2020-03 data.csv result2.csv result.csv
```

## Examples

### Terminal Example
![Demo Example Gif](https://raw.githubusercontent.com/taylorfergusson/covid-analysis/master/terminal-example.gif "Demo Example Gif")


### Detailed Example

For instance, running the following command:

```
./covidata.sh -r get 35 2020-03 2020-07 data.csv result.csv
```

Will extract data for Ontario (pruid 35) from March 2020 to July 2020, compute the nunber of rows, average confirmed cases, deaths, and tests, and save these results in result.csv.

Then, if you run:

```
./covidata.sh compare 47 data.csv result2.csv result.csv
```

The script will compare the data extracted in the first step with the newly computed data for Saskatchewan (pruid 47), and save the differences in row count, average confirmed cases, deaths, and tests into the results2.csv file.

## Credits

CSV data sourced from Government of Canada.

## Contact

If you have any questions or comments, don't hesitate to reach out to me via email at hello@taylorfergusson.com