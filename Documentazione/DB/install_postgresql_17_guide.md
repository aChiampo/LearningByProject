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

If PowerShell says that `psql` is not recognized, add PostgreSQL 17 to the Windows `PATH`.

The folder to add is usually:

```text
C:\Program Files\PostgreSQL\17\bin
```

After changing the `PATH`, close and reopen PowerShell, then run again:

```powershell
psql --version
```

## 10. Create the Project Database

From PowerShell, create a local database for the project:

```powershell
createdb -U postgres -h localhost -p 5432 vetmanager
```

When asked for the password, type:

```text
root
```

## 11. Run the Project Schema

Move to the project folder:

```powershell
cd "C:\Users\andrea.chiampo\Desktop\LearningByProject"
```

Run the schema file:

```powershell
psql -U postgres -h localhost -p 5432 -d vetmanager -f ".\Documentazione\DB\create_schema.sql"
```

When asked for the password, type:

```text
root
```

## 12. Check the Created Tables

Connect to the database:

```powershell
psql -U postgres -h localhost -p 5432 -d vetmanager
```

When asked for the password, type:

```text
root
```

Inside `psql`, list the tables:

```sql
\dt
```

To exit `psql`, run:

```sql
\q
```

## Summary

Use these values for the local PostgreSQL installation:

```text
PostgreSQL version: 17
Host: localhost
Port: 5432
Database: vetmanager
Username: postgres
Password: root
```

Install only:

- **PostgreSQL Server**
- **Command Line Tools**
- **pgAdmin**

Do not install:

- `Stack Builder`
