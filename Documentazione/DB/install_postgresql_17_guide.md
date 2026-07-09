# PostgreSQL 17 Installation Guide

This guide explains how to install PostgreSQL 17 on Windows and how to use the PostgreSQL command-line tools for this project.

## Requirements

- Windows
- Internet connection
- Administrator permissions on your computer

## 1. Download PostgreSQL 17

1. Open the official PostgreSQL Windows download page:
   <https://www.postgresql.org/download/windows/>
2. Click **Download the installer**.
3. Select **PostgreSQL 17** for Windows.
4. Download the installer.

## 2. Start the Installer

1. Run the downloaded installer.
2. Click **Next**.
3. Choose the installation folder or keep the default one.

The default folder is usually:

```text
C:\Program Files\PostgreSQL\17
```

## 3. Select Components

When the installer asks which components to install, select:

- **PostgreSQL Server**
- **Command Line Tools**
- **pgAdmin**

Do not install:

- `Stack Builder`

Then click **Next**.

## 4. Choose the Data Folder

Keep the default data folder, unless you have a specific reason to change it.

The default folder is usually:

```text
C:\Program Files\PostgreSQL\17\data
```

Click **Next**.

## 5. Set the Database Password

When the installer asks for the password of the default PostgreSQL user, use:

```text
root
```

The default PostgreSQL user is:

```text
postgres
```

So your local database credentials will be:

```text
Username: postgres
Password: root
```

Click **Next**.

## 6. Set the Port

Keep the default PostgreSQL port:

```text
5432
```

Click **Next**.

## 7. Choose the Locale

Keep the default locale.

Click **Next**.

## 8. Finish the Installation

1. Review the installation summary.
2. Click **Next**.
3. Wait for the installation to finish.
4. At the end, make sure **Launch Stack Builder** is not selected.
5. Click **Finish**.

## 9. Check That PostgreSQL Works

Open PowerShell and run:

```powershell
psql --version
```

You should see a PostgreSQL 17 version, for example:

```text
psql (PostgreSQL) 17.x
```

## 10. Setting BD connection
 
1. Open pgAdmin
2. Open `Add New Server`
    
   ![alt](./../assets/point1.jpg)
     
1. Put any name in `General` Page
     
   ![alt](./../assets/point3.jpg)
     
1. Paste this settings in connection:
   - **Host** : `psql-database-01-edu-0b7e.j.aivencloud.com`
   - **Port** : `11216`
   - **Maintenance database** : `defaultdb`
   - **Username** : `avnadmin`
   - **Password** : `AVNS_bbjXNdSDpMo1dF4DBAg`
 
![alt](./../assets/point4.jpg)