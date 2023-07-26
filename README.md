# README.md

## Project Description

This project is about processing a `.vcf` file, which contains a list of variants (mutations) of a given genetic sample(s). The project reads a variant file, processes it according to some parameters, and splits it into different output files.

## Project Setup

### Prerequisites

Ensure that you have the following installed on your system:

1. Java JDK (version 8 or later)
2. Maven
3. Git (Optional)

### How to run

After cloning and cd homeAss, you can compile the project with the following command:

```shell
mvn compile
```

To package the project into an executable jar file, use the following command:

```shell
mvn package
```

The project can be run with the following command:

```shell
mvn exec:java -Dexec.args="limit [start] [end] [minDP] [deNovo]"
```

The first argument is mandatory limit < 10, while the others are optional. 

For example, to run the project with a limit of 5 and a minDP of 10, you would use the following command:

```shell
mvn exec:java -Dexec.args="5 . . 10"
```

## Program Behavior

Upon initiation, the program outputs a greeting message, "Hello". As it begins processing the VCF file, a status message "processing VCF..." is displayed. Upon successful completion of the process, the program indicates completion with the message "Done."

Errors are handled gracefully throughout the program's operation. Any non-terminal errors encountered during execution will prompt appropriate error messages but will not interrupt the overall operation of the program.

Note: deNovo option currently limited to one set of: proband, mother, father; within the samples in the file.

## Project Functionality

The main functionalities of this project include:

1. Reading the content of a gzipped vcf file from AWS S3.
2. For each sample in the VCF, outputting into a new VCF file named `<SAMPLE>_filtered.vcf`.
3. The new VCF file includes the VCF header, VCF column line with the relevant sample's column, and all the variant lines present in that sample that meet the filter criteria.
   It is assumed that a variant is considered not to be present in that sample if genotype is ('.'). This could be changed easily to include a filter for (0/0) - homozygous to the reference allel.
5. An API is used to get the variant gene which is included as a new subfield in the INFO column of the output variant line.
6. The output for each sample stops when reaching the end of the original VCF file, or after outputting for that sample, the number of lines specified in the limit parameter.
7. The output VCF files will be generated to the 'output' direcory located within the project directory.
   

## Personal Note

The project implements all the required functionality in an object-oriented manner and is structured for future enhancements. The current version has basic error handling but going forward, we can improve the robustness of the error handling system. Additionally, the functionality of the deNovo feature can also be enhanced and thorough testing for various user scenarios can be conducted. Program flow and time can also be further improved, handeling io with threads for example.

## License
All Rights Reserved.

## Contact
Itai alcalai - itai.alcalai@gmail.com

