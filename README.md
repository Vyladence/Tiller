# AutoReplant

Autoreplant is a plugin for your 1.21+ Minecraft Server that adds a simple and fully-configurable auto-replant function to your game.

# Features

* Crops harvested by players withthe **autoreplant.use** permission will be automatically replanted
* Automatically detects new crops using the Bukkit API's *Ageable* block type, so custom crops added using that block type should automatically be picked up by the plugin
* Items used to harvest crops can be configured
* Can be enabled/disabled by default
* Players can toggle auto-replant on/off using /autoreplant toggle
* Every option and message is fully configurable
* Very lightweight, only registers one world listener

# Limitations

* No way to toggle autoreplant for other players without something like Essentials' `sudo` command (TODO)
* All crops can be auto-replanted. Crops cannot be disabled (TODO)
* Messages only support in-game YAML codes (TODO)
* Players with `autoreplant.use` will always be able to toggle it on/off (TODO)
* If non-tool items (items without a durability attribute) are configured to replant crops, they will never break and can be used indefinitely.

# Commands

* `/autoreplant` - Displays plugin information (configurable in `config.yml/messages`)
* `/autoreplant toggle` - Toggle auto-replant functionality on/off per-player
* `/autoreplant reload` - Reloads the plugin config
# Permissions

* `autoreplant.use` - Automatically replants crops harvested using items defined in `config.yml`, also grants access to `/autoreplant toggle`
* `autoreplant.command.reload` - Grants user access to the config reload command, should be given to server admins
* `autoreplant.bypass_item_durability` - If item damage is enabled in config.yml, players with this permission will still not lose durability on their tools used to harvest crops
* `autoreplant.ignore_tool_restrictions` - Players with this permission will not be required to use a tool defined in `allowed-items` for their crops to automatically be replanted

# Configuration

[config.yml]([https://github.com/Vyladence/Autoreplant/blob/master/src/main/resources/config.yml](https://github.com/Vyladence/Autoreplant/blob/master/src/main/resources/config.yml))

The config is commented to be as easy-to-understand as possible.

Out of the box:

* Players (with the correct permissions) will have autoreplant enabled by default
* Crops being harvested will obey the fortune enchantment
* Item (Tool) damage is enabled
* Destroying partially grown crops will damage items (tools)
* Partially grown crops will still be replanted
* allowed-items is configured to use all types of Hoe item (wooden, stone, iron, etc)

All of these options can be configured to your liking.

# TO-DO

* Subtract one seed/item drop from harvested crops to better reflect the usage of seeds when replanting
* Configure blacklisted crop types
* Separate autoreplant.use and autoreplant.command.toggle permissions so players can use it without being able to toggle
* only tab complete autoreplant command for players with any autoreplant.command.* permissions
* Support Minedown formatting in messages
* Allow admins to toggle auto-replant on and off for other players
* Redo command/tab complete system to make it easier to add commands and subcommands
* Allow players to reset their replant-state back to the default
