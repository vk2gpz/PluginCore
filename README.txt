
PluginCore for Bukkit, written by Acru Jovia, updated by ElgarL and
now mainteined by vk2gpz
Distributed under the The Non-Profit Open Software License version 3.0 (NPOSL-3.0)
http://www.opensource.org/licenses/NOSL3.0

This project is a build requirement for the Lockette project,
and needs the following Libraries: (Version used in brackets)

* Bukkit (1.2.5-R5.0)
* bPermissions (2.9.4)
* Factions (1.6.8)
* GroupManager (2.0.23)
* LWC (4.2.0)
* mcMMO (1.3.09)
* Permissions (2.5.4)
* PermissionsBukkit (1.6)
* PermissionsEx (1.19.2)
* Register (1.8)
* SimpleClans (2.3.4)
* Towny (0.81)

To compile against spigot1.8, you need to obtain your own copy of
spigot.jar since it's no longer distributed in the form of .jar.

This version now use Maven as the project management system.  In order
to use PluginCore for other package like Lockette, do

mvn clean package install

to install it into your local Maven repository.



