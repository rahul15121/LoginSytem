# LoginSystem Plugin


![login System](https://github.com/user-attachments/assets/8c6cd61a-8d7a-4991-9944-abb943eb89f4)

## Overview
The `LoginSystem` plugin is a simple authentication system for Minecraft Paper servers. It ensures that players must register and login before they can interact with the server or use any commands.

## Features
- Players must register with a password using `/register <password>`.
- Registered players must login using `/login <password>` each time they join the server.
- Players can change their password using `/changepassword <new_password>` after logging in.
- Titles are displayed on the screen prompting players to register or login until they authenticate.
- Players cannot move or use any commands (except register/login) until they authenticate.
- Blindness and slowness effects are applied to players until they authenticate.
- All commands are prefixed with `[security]` and use colorful messages.

## Commands
- `/register <password>`: Register a new account with the specified password.
- `/login <password>`: Login with the specified password.
- `/changepassword <new_password>`: Change your password after logging in.

## Installation
1. Download the plugin jar file and place it in your server's `plugins` folder.
2. Restart the server to generate the necessary configuration files.

## Configuration
No additional configuration is required. The plugin will automatically create a `players.yml` file in the plugin's data folder to store player credentials.

## Permissions
The plugin does not use any permissions. All players must register and login.

## Example Usage
1. **Register**: A new player joins the server and sees the title "Register" with the subtitle "Use /register <password>". The player types `/register mypassword` in the chat.
2. **Login**: When the player rejoins the server, they see the title "Login" with the subtitle "Use /login <password>". The player types `/login mypassword` in the chat.
3. **Change Password**: After logging in, the player can change their password using `/changepassword mynewpassword`.
