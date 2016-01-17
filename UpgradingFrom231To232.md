# Upgrading from version 2.3.1 to 2.3.2 #

---

Upgrading from JForum version 2.3.1 to version 2.3.2 is easy. All you have to do is to carefully follow the steps here described.

The suggested approach is to unpack JForum 2.3.2 into some temporary directory, make the steps here shown, and then, when all is finished and tested, copy it over the directory where the previous version is located. This is a suggestion, and you're free to proceed the way you prefer.

## What's new ##

---

For a list of changes made in JForum 2.3.2, please check [New and Changed Features in JForum 2.3.2](NewFeatures232.md).
## Backup your data ##

---

First, make a backup of the database and the current directory where JForum is installed. JForum's directories are: _templates_, _images_, _upload_ and _WEB-INF/config_. Each database has a different backup [tool](http://www.aquafold.com/), so please check its documentation or with your system administrator. For HSQLDB backup, the database files are in the directory _WEB-INF/config/database/hsqldb_, and all you have to do is to copy it.
## Backup configuration files ##

---

You'd like to take special care for _SystemGlobals.properties_ and _jforum-custom.conf_, as these are the main configuration files. Backup them and then compare and merge your current version with the new version that comes with JForum 2.3.2. The same is valid for database-specific configurations, that are stored in the directory _WEB-INF/config/database_.
## Upgrading the database schema ##

---

There are some changes to the database schema - a new column here, a removed column there... In order to get the new database right, go to the directory **_upgrade/2.3.2_**, where you'll find the upgrade script for all supported databases - Oracle, MySQL, PostgreSQL, SQL Server and HSLQDB.
Using your database management console / tool, import the appropriate script. Below is a list of commands for each database - another possible approach is to simple use the management console and paste the script there. It is up to you.

### MySQL ###

`mysql -u USERNAME -p DBNAME < mysql_2.3.1_to_2.3.2.sql`
### PostgreSQL ###

Log in to postgres and type

`\i /path/to/postgresql_2.3.1_to_2.3.2.sql`

Or, directly from the command line, use the `psql` tool, as in

`psql -f /path/to/postgresql_2.3.1_to_2.3.2.sql`

### Oracle ###

`sqlplus @oracle_2.3.1_to_2.3.2.sql`

### SQL Server ###
You can use SQL Server Management Studio and simply paste the contents of file _sqlserver\_2.3.1\_to\_2.3.2.sql_ into the text area at the right, and click "Execute"

### HSQLDB ###

The process for HSQLDB is trickier. First, from the command line, go to the directory _WEB-INF/config/database/hsqldb_, which is where the database is located. Then, run the following command (you must have Java installed):

`java -Djava.ext.dirs=../../../lib org.hsqldb.util.DatabaseManagerSwing`

This will open a dialog window asking you for connection settings. In "Type" choose "HSQL Database Engine Standalone", and, for "URL", put the value _jdbc:hsqldb:jforum_. Leave "User" and "Password" with the default values, and click "OK". A new window will show up, listing all tables in the left column.

Now simply paste the contents of file _hsqldb\_2.3.1\_to\_2.3.2.sql_ into the text area at the right, and click "Execute"

## Check the configuration files ##

---

### SystemGlobals.properties / jforum-custom.conf ###

Open the file _WEB-INF/config/SystemGlobals.properties_ and check every property, setting it up according to your needs. Please note that JForum stores customized configurations (those saved from the Admin Panel -> Configurations page) into a file named _jforum-custom.conf_, which your current installation of JForum may or may not have. If you have it, please make sure to update any necessary value there as well.
### modulesMapping.properties ###

Open the file _WEB-INF/config/modulesMapping.properties_ and remove the following line, if it exists:

`install = net.jforum.view.install.InstallAction`

## Testing ##

---

Now, test JForum 2.3.2 before adding it to the production environment. The easier way is to put it under some another Context. If it starts and runs without any problems, then you can proceed to the final step, which is just a matter of replacing the old version with this new one.