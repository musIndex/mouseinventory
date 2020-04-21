# MSU Branch of Mouse Inventory

## Administrative Documentation for MSU version of Web App

### Billing Overview

An overview of the ITM 444 Azure subscription can be found [here](https://portal.azure.com/#blade/Microsoft_Azure_CostManagement/Menu/costanalysis)

An overview of the AWS costs can be found [here](https://console.aws.amazon.com/billing/home?region=us-east-2#/)

More detailed AWS billing information can be found in the "Bills" tab on the left side of the above screen. (Note: there are AWS costs listed about EC2s (virtual computer instances; his was only relevant during early development and the instance was terminated on 04/18/20 so the EC2 costs stopped on that date.)

### Azure Resource Overview

There are 5 Azure items within the ITM444 Subscription

| Name | Type |
|------|------|
|msurodentdatabaseservice|App Service|
|msurodentdatabaseservice-dev|App Service|
|RodentServicePlan|App Service plan|
|RodentResourceGroup|Resource Group|
|rodentaccount|Storage account|

`msurodentdatabaseservice` is the front-end for the web app, accessible via [MSU's domain](https://carrodentdatabase.msu.edu/) or [Azure's Domain] (https://msurodentdatabaseservice.azurewebsites.net/)

`msurodentdatabaseservice-dev` ist the front end for the dev envrionemnt, accessible via [Azure's domain](https://msurodentdatabaseservice-dev.azurewebsites.net/)

Both App Services are cconfigured using envrionment variables. The path to change the environment variables is `App Service Name > Settings > Configuration`. The description of the environment variables canbe found [here](https://github.com/musIndex/mouseinventory#environment-variables).

### How to Manage the Single-Sign-On (SSO) Settings

`msurodentdatabaseservice > Authentication / Authorization > Azure Active Directory`

By clicking on Azure AD, you can find the  `Client ID` and the `Issuer URL`, which MSU IT Services can use to add more users to the app.

`App Name: ITM444`
`Client ID: 5cdaee4e-6f57-452a-89d1-568459cc95fc`
`Issuer URL: https://sts.windows.net/22177130-642f-41d9-9211-74237ad5687d/`

### How to Add Administrators to the Web App

In order to add someone to the list of administrators for the web app, you must first find their `Object ID`

To find their Object ID, you must go to this [link](https://developer.microsoft.com/en-us/graph/graph-explorer/preview)

Login on the left side of the screen; after logging in, select the query `GET`. Scroll down to the `Response Preview` screen and find your `id`. Copy this ID. Then go to [Azure](https://www.portal.azure.com/) and find the `msurodentdatabaseservice` app service. On the left side of the screen, select `Configuration` and then edit the value of `ADMINISRATOR_IDS`. Add the copied object ID to the list.

Instruct the user to log out, close their browser, and go to the web app. Verify that they can click the button `admin use only` ont he right side of the screen without errors. If the error perists, instruct them to open a chrome ingonito window, go tto the web app, and try again

### How to Edit the Database via SQL Editor (DataGrip)

A [DataGrip](https://www.jetbrains.com/datagrip/) educational [license](https://www.jetbrains.com/community/education/#students) is free for those with an `.edu` address. After acquring the licese, install the software. Once installed, yiu must create a connection to the datasource.

To add a new datasource, click `File > New > Data Source > MariaDB`

Fill the connection settings with the following settings (Note: the URL string following `jdbc:mariadb://` is the RDS instance's endpoint which can be found in AWS):
|*parameter*| *value*|
|---------|----------|
| Name| MariaDB - @rodentdatabase.cix4pp5vgvuz.us-east-2.rds.amazonaws.com|
|Host| rodentdatabase.cix4pp5vgvuz.us-east-2.rds.amazonaws.com|
|User| caradmin|
|Password| *Given*|
|Database| *Empty*|
|URL| jdbc://rodentdatabase.cix4pp5vgvuz.us-east-2.rds.amazonaws.com|

Select [*any* of 7] and ensure **both schemas `rodentdatabase` and `rodentdatabase-dev` are checked off and able to be opened**

Expand to view Settings and you can edit and push from here.

To save and submit the web app, select the button `DB` button at the top of the window with an arrow pointing to the letters (the button lights up after applying changes).

### AWS Resource Overview

There are 2 AWS resources, an RDS Instance named `rodentdatabase` which hosts the rodent data, and an S3 Storage bucket called `msurodentdatabasefiles` which hosts documentation and PDFs. 

AWS can be logged onto via [MSU SSO](https://auth.msu.edu/).

##### View Database Instance

To view the database, select `view all services`, then select DS under `Database`

After clicking RDS you will see the `Databases` screen.

By clicking on `rodentdatabase`, you can see the RDS details such as endpoint, which is the address you would use to remote into the database.

##### View S3 Bucket

To view the S3 Storage Bucket, select `view all services`, then select S3 under `Storage`.

Select `msurodentdatabasefiles` and you will be able to edit which files are hosted.

### Guide to the MySQL Command Line Interface (CLI)

##### Remote into the RDS instance via PowerShell

```sh
mysql -h rodentdatabase.cix4pp5vgvuz.us-east-2.rds.amazonaws.com -u caradmin -p <password>
```

##### How to Populate an Empty Database

1. Remote into the RDS instance
2. USE *dbName*
3. SOURCE `.../Demo_Database_sql`

This file `Demo_Database.sql` can be found in the Github repo.

### How to Deploy GitHub Code Changes to the Web App
*must be a member of the `mousespace` Azure DevOps project or else you will have a **permissions error** *

* Change code from the `release/msu` branch on the `mouseinventory` [repo](https://github.com/musIndex/mouseinventory/tree/release/msu) 
* Go to the Azure DevOps [pipeline](https://dev.azure.com/EstelleWall/mousespace/_releaseDefinition?definitionId=2&_a=definition-tasks) and create a new release from the MSU pipeline
* Go to the `Releases` tab
* Verify the release has been deployed successfully
