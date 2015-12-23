# sqlConvert
migrate database from MSSQL into PostgreSQL

Now days within the fast and rapid changes in technologies used in application development and the operating environments, it is important to keep old applications up to date to make sure they cope with these changes to run in the new operating environment. Databases are an important part of the applications and it is important to it keep it up to date to work along with applications build or updated to new technologies and make use of new databases features. Databases tend to be updated by migrating into newer version of the same database or by migrating from commercial closed source into open source databases. The second trend is the more used in migration. Open source databases became more powerful and have features allow them to compete with commercial databases with no coast. Database migration usually is done manually by generating a SQL script that contains the structure of the source database, then modify it to match the target database syntax. After that, the data modification and transferring start from source to target database. This process is time consuming and costing, prone to human errors during translating the schema or during modifying the data to be understandable by the target. Most of the scientific research in this area is focused on schema translation without data transferring and existing tools provide semi-automatic database migration for some of the database elements and not for full schema. 
This project aims to solve manual migration problems and automate the process using MSSQL Server as source database and PostgreSQL Server and target database and provide full database migration. SqlConvert is a tool that translate the database and transfer data in minimal time and partial implementation of the proposed design.

<B>
Database Specifications
</B>
<br/>
The analysis and implementation for sqlConvert  tool supports MSSQL and PostgreSQL servers with the following specifications:

<I>MSSQL</I><br/>
	MS SQL Server 2008 or higher.<br/>
	Enable remote connections (TCP/IP).<br/>
	Enable shared memory. <br/>
	Enable names pipes.<br/>

<i>PostgreSQL</i><br/>
	PostgreSQL server 9.4. <br/>
	Default connections and authentication configurations.<br/>
	Enable remote connections (TCP/IP).<br/>

<b>Development environment</b><br/>
This project is a maven project built on Java 1.8 and make use of its features using eclipse
<br/>
<i>Used APIs</i><br/>
	“maven-jar-plugin 2.6” is used to run maven and build jar files<br/>			
	“postgresql jdbc4 ” provides an interface to connect to PostgreSQL database and execute queries<br/>
	“sqlserver sqljdbc4” provide an interface to connected to Microsoft SQL Server<br/>
	“guava 18” provides an easy way to create Stopwatches and timers , which I used to get statistics about the time needed for functions to run <br/>
	“Log4j 2”  is used to created loggers which I used in logging errors, info, warn and debugging<br/>
	“json-smart 2.2”  is used to read and parse json files, which I used to parse datatype and function mapping files<br/>
<br/>
<b>Evaluation</b><br/>
I evaluated sqlConvert based on test the implemented features using three well known databases –Northwind, AdventureWorks, and AdventureWorksDW - provided by Microsoft for development and testing and compare it with other tools.
Since I am not providing full implementation for database migration with all its elements, I would say sqlConvert is a semi-database migration tool.
<br/>
The testing results may differs from one PC to another depend on the specification and the databases location. I run the tests on laptop with the following specifications:
<br/>
	RAM : 8 GB
<br/>
	CPU: Inter coreTM i5 @ 2.5GHZ , dual core , 4 logical processors
<br/>
	OS: Windows 8
<br/>
And the two databases are installed locally on the same laptop
<br/>

SqlConvert took ( 1.601 min) to convert and transfer AdventureWorksDW2014 DB  and (1.63 min) to convert and transfer AdventureWorks2014



