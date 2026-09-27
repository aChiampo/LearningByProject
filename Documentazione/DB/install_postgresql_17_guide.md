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

When the installer asks for the password of the default `postgres` user, choose a unique local-development password. Do not use `root`, reuse a personal password, or commit the selected value to this repository.

Store project-specific local credentials in the ignored root `.env` file or in your development environment. The committed `.env.example` contains placeholders only.

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

## 10. Configure a Local Connection

1. Open pgAdmin.
2. Select **Add New Server**.
3. Enter any descriptive name on the **General** page.
4. Configure the connection with values from your local installation:
   - **Host**: `localhost`
   - **Port**: `5432`
   - **Maintenance database**: `postgres`
   - **Username**: `postgres`
   - **Password**: the unique password selected during installation

The PostgreSQL container in `compose.yml` is intentionally not published to the host. Use `docker compose exec database psql ...` to inspect the containerized database, or run a separate local PostgreSQL instance for non-Docker development.

The remote database and email credentials previously committed to this project must be rotated. They can remain recoverable from Git history even after the current files are corrected; coordinate history cleanup before treating the repository as sanitized.
