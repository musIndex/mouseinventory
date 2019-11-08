# Mouse Inventory Database

The mouse inventory database is an application for tracking an institution's inventory of mice for genetics research.

## Features

See the [home page](http://musIndex.github.com/mouseinventory)

## Getting Started

The following instructions assume you're working on a Mac.

### Installing Prequisites

There are four prequisites that are required to run MouseInventory.

- MariaDB
- Tomcat (version 9)
- Java (version 8)
- Maven (version 3)

The easiest way to install these is using `brew`, a package manager for the Mac.

### Mac

```
git clone git@github.com:musIndex/mouseinventory.git

1. install tomcat with brew
2. update tomcat users
3. start tomcat with the following command

## Developing

See the [development notes wiki](https://github.com/musIndex/mouseinventory/wiki/Development-notes)
to learn about how to work with the code.

## Environment Variables

Environment Variable | Description | Example
--|--|--
DB_DRIVER_CLASSNAME      | Classname of the driver for the main database                 | `org.mariadb.jdbc.Driver`
DB_CONNECTION_STRING     | Connection string for the main databse                        | `jdbc:mariadb://localhost:3306/mousespace?user=fakeUser&password=b4dp455w0rd`
MGI_DB_DRIVER_CLASSNAME  | Classname of the driver for the MGI database.                 | `org.postgresql.Driver`
MGI_DB_CONNECTION_STRING | Connection string for the MGI database                        | `jdbc:postgresql://mgi-adhoc.jax.org:5432/mgd?username=prodUser&password=an0th3rp444w0rd`
SMTP_SERVER              | _optional_ The URL of the SMTP server.                        | `smtp.server.com`
SMTP_USER                | _optional_ The username for connecting to SMTP server.        | `james.bond@mi6.com`
SMTP_PW                  | _optional_ The password for connecting to SMTP server.        | `lic3nset0k1ll`
SMTP_PORT                | _optional_ The port used to connect to SMTP server..          | `465`
SMTP_SSL_ENABLED         | _optional_ Whether or not to use encrypted SMTP  (`true` or `false`) | `true`
GOOGLE_ANALYTICS_ACCOUNT | _optional_ The account for Google Analytics.                  | `18988`
GOOGLE_ANALYTICS_DOMAIN_SUFFIX | _optional_ The domain suffix used for Google Analytics. | `www.example.com`

## Setting environment variables

There are a number of ways to set environment variables.

### Command line (Mac/Linux)
Assuming you use the command line to start Tomcat, you can also set the enviroment variables directly on the command line.

```sh
export DB_DRIVER_CLASSNAME=org.mariadb.jdbc.Driver
export DB_CONNECTION_STRING="jdbc:mariadb://localhost:3306/mousespace?user=fakeUser&password=b4dp455w0rd"
export MGI_DB_DRIVER_CLASSNAME=org.postgresql.Driver
export MGI_DB_CONNECTION_STRING="jdbc:postgresql://mgi-adhoc.jax.org:5432/mgd?username=prodUser&password=an0th3rp444w0rd"
export SMTP_SERVER=""
export SMTP_USER=""
export SMTP_PW=""
export SMTP_PORT=430
export SMTP_SSL_ENABLED=True
export GOOGLE_ANALYTICS_ACCOUNT=""
export GOOGLE_ANALYTICS_DOMAIN_SUFFIX=""
catalina start
```

### Adding to plist (Mac)
If you installed Tomcat using `brew` and you don't plan on using it for anything
else, you can hardcode the environment variables directly into the `homebrew-mxcl.tomcat.plist`
configuration file for Tomcat. Just edit file located in `/usr/local/Cellar/tomcat/<version>/`
and add the following just before the closing `</dict></plist>` tags.

```xml
    <key>EnvironmentVariables</key>
    <dict>
      <key>DB_DRIVER_CLASSNAME</key>
      <string>org.mariadb.jdbc.Driver</string>
      <key>DB_CONNECTION_STRING</key>
      <string>jdbc:mariadb://localhost:3306/mousespace?user=mousedb&password=abc123</string>
      <key>MGI_DB_DRIVER_CLASSNAME</key>
      <string>org.postgresql.Driver</string>
      <key>MGI_CONNECTION_STRING</key>
      <string>jdbc:postgresql://mgi-adhoc.jax.org:5432/mgd?username=mousedb&password=abc123</string>
      <key>SMTP_SERVER</key>
      <string></string>
      <key>SMTP_USER</key>
      <string></string>
      <key>SMTP_PW</key>
      <string></string>
      <key>SMTP_PORT</key>
      <string>1</string>
      <key>SMTP_SSL_ENABLED</key>
      <string>1</string>
      <key>GOOGLE_ANALYTICS_ACCOUNT</key>
      <string></string>
      <key>GOOGLE_ANALYTICS_DOMAIN_SUFFIX</key>
      <string>True</string>
    </dict>
```

A sample `homebrew-mxcl.tomcat.plist` is [included in the repo](serverFiles/brew/homebrew.mxcl.tomcat.plist).