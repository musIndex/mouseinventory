# Mouse Inventory Database

The mouse inventory database is an application for tracking an institution's inventory of mice for genetics research.


## Features

See the <a href="http://ucsf-mousedb.github.com/mouseinventory">home page</a>


## Requirements

To run the mouse database, your institution will need a J2EE container and a database.  Also, you will need to obtain a free public SQL account from MGI (Mouse Genome Informatics)

For the purposes of this document, I'll assume that you are using the following setup, but any compatible alternative should work.

Assumed setup:

1. Ubuntu server 11.04
2. MySQL server 5.1
3. Apache Tomcat 6
4. Nginx as a reverse proxy


## Installation on a dedicated server

Follow this guide if you will be hosting the mouse database on a dedicated linux server.

NOTE: this guide assumes that you are already familiar with MySQL, Tomcat, and Maven.  If not, now is a good time to learn the basics!

Start with a clean install of ubuntu 11.04 server$
### Update and install basic packages
    >sudo apt-get update
    >sudo apt-get dist-upgrade

### Install required packages
    >sudo apt-get install nginx tomcat6 tomcat6-admin mysql-server
    >sudo apt-get install git maven2 openjdk-6-jdk

### Copy files to the server
    >cd ~
    >git clone git://github.com/mouseinventorydatabase/mouseinventory.git


### Configure MySQL
Download the mysql JDBC connector, and copy it to the server.
It can be downloaded from http://dev.mysql.com/downloads/connector/j/, 
Copy it to the tomcat lib directory

    >sudo cp mysql-connector-java-5.0.8.jar /usr/share/tomcat6/lib/

Create the database
Replace 'password' with your password for mouseuser - the account used by the application
this password should match the password for the JDBC resource that you will specify later in /var/lib/tomcat6/conf/context.xml

    >mysql -u root -p
      CREATE DATABASE mouse_inventory;
      CREATE USER 'mouseuser'@'localhost' IDENTIFIED BY 'password';
      GRANT SELECT,UPDATE,INSERT,DELETE ON mouse_inventory.* TO 'mouseuser'@'localhost' WITH GRANT OPTION;
      quit

Create initial database and load the mouse database software

    >cd ~/mouseinventory/database
    >mysql -u root mouse_inventory < blankdb.sql

### Configure nginx reverse proxy

For a default https install, proxying to tomcat on port 8080, and forcing HTTPS, do the following:

Obtain SSL certificates for your domain, and place the certificate (if there is a chain cert, just append it into the main certificate file) and key  in /etc/ssl/certs and /etc/ssl/private, respectively

    >cd ~/mouseinventory
    >cp serverFiles/nginx/mouseinventory-proxy to /etc/nginx/sites-available

Enable the site:

    >ln-s /etc/nginx/sites-available/mouseinventory-proxy /etc/nginx/sites-enabled/mouseinventory-proxy

Add favicon

    >cd ~/mouseinventory
    >cp favicon.ico /var/www

Start nginx

    >sudo /etc/init.d/nginx start

### Configure tomcat

Copy and edit configuration template files

    >cd ~/mouseinventory
    >sudo cp serverFiles/tomcat/tomcat-users.xml /var/lib/tomcat6/conf/tomcat-users.xml
    >sudo cp serverFiles/tomcat/context.xml /var/lib/tomcat6/conf/context.xml

#### Edit tomcat-users.xml

    >sudo vim /var/lib/tomcat6/conf/tomcat-users.xml

Change the administrator password and/or username.  This is used  by the database administrator to log in and make changes to the site.
Uncomment the manager section. This will allow you to use the tomcat manager application to update the application.  Set your own username and password for the tomcat manager

#### Edit context.xml

    >sudo vim /var/lib/tomcat6/conf/context.xml

The username and password for the jdbc resource are the account that the application uses to access the mysql database
The MGI_DATABASE_USER is the SQL account with MGI.  Request one from MGI: http://www.informatics.jax.org/software.shtml#sql.
The site name will appear at the top of each page on the site
The google analytics account and domain suffix can be used if you have a google analytics account and want to track this site's usage.


#### Restart tomcat to apply the changes
    >sudo /etc/init.d/tomcat6 restart



### Compile and install the server to tomcat
    >cd ~/mouseinventory/project
    >mvn package cargo:deploy


### Test - go to the server's URL - http://hostname/

You should be redirected to https://hostname/mouseinventory/, and see the about.jsp page.
If you don't, check the most recent log files in /var/log/tomcat6


### Completed!
You may want to take additional steps, and set up database backup cron jobs
