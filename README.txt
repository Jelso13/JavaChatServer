Some additional functionality has been included in the project that is outlined here:

When a client first connects to the server, the ServerConnection class verifies if the username is an available username.
If the username is a keyword reserved for server messages for example or if the username requested has already been taken 
by another user then the user will be informed that they should attempt to retry with a different username, informing them
of the reserved keywords to make choosing a valid username more clear.

The server has added functionality of being able to determine the usernames of all users currently connected to the server,
these usernames are freed from the list of usernames when the user disconnects to allow the user to re-login.