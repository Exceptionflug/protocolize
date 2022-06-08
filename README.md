
<div align=center>
    <img src="./protocolize-logo.webp" width="512">
    <br/><br/>
    <p>Next generation protocol manipulation framework for proxy servers.</p>
    <a href="http://ci.exceptionflug.de/job/Protocolize2/"><img src="http://ci.exceptionflug.de/buildStatus/icon?job=Protocolize2"></a>
    <a href="https://github.com/Exceptionflug/Protocolize/releases"><img src="https://img.shields.io/github/downloads/Exceptionflug/Protocolize/total?color=GREEN&label=Downloads"></a>
    <a href="https://discord.simplixsoft.com/"><img src="https://img.shields.io/discord/752533664696369204?label=Discord"></a>
    <img src="https://img.shields.io/github/license/Exceptionflug/Protocolize"></a>
    <a href="https://twitter.com/Exceptionflug"><img src="https://img.shields.io/twitter/follow/Exceptionflug?label=Twitter&style=social"></a>
</div>

# Protocolize v2
This is the official repository for the next generation proxy server protocol manipulation framework. If you wish to see the repository for Protocolize v1, click [here](https://github.com/Exceptionflug/protocolize/tree/v1).

## Why a completely recode?
Protocolize v1 was already a successful approach on how to interact with the Minecraft protocol. The downsides were severe: An outdated code base that was hard to maintain and only supported BungeeCord. All those reasons resulted in the making of an whole new fresh code base that supports Velocity and that also takes a new approach on how to provide mappings for all supported protocol mappings.

### Migration
We provide some documentation on how to migrate your existing plugin to use the new Protocolize v2 API. [Click here](https://github.com/Exceptionflug/protocolize/wiki/Migrating-from-Protocolize-v1).

## Getting started
### Maven dependency and repository
```xml
<repository>
    <id>exceptionflug</id>
    <url>https://mvn.exceptionflug.de/repository/exceptionflug-public/</url>
</repository>
```
```xml
<dependency>
    <groupId>dev.simplix</groupId>
    <artifactId>protocolize-api</artifactId>
    <version>2.2.1</version>
    <scope>provided</scope>
</dependency>
```
### Install the plugin
In order to use Protocolize, you have to install it onto your proxy server. Protocolize is supporting BungeeCord and Velocity proxy servers.

## Compatibility
Protocolize is shipped with it's default data module which adds support for the following versions:
| Minecraft Version | Supported |
|--|--|
| 1.8.x - 1.12.2 | ❌ (Only with [additional legacy module](https://ci.exceptionflug.de/job/Protocolize-Legacy-Data/)) |
| 1.13 - 1.13.2 | ✔️ (Sounds only with [additional legacy module](https://ci.exceptionflug.de/job/Protocolize-Legacy-Data/)) |
| 1.14 - 1.19 | ✔️ |

Implemented packets by default:

| Packet | Class |
|--|--|
| Player block placement | dev.simplix.protocolize.data.packets.BlockPlacement |
| Inventory click | dev.simplix.protocolize.data.packets.ClickWindow |
| Close inventory | dev.simplix.protocolize.data.packets.CloseWindow |
| Confirm transaction | dev.simplix.protocolize.data.packets.ConfirmTransaction |
| Held item change | dev.simplix.protocolize.data.packets.HeldItemChange |
| Named sound effect | dev.simplix.protocolize.data.packets.NamedSoundEffect |
| Open inventory | dev.simplix.protocolize.data.packets.OpenWindow |
| Player look update | dev.simplix.protocolize.data.packets.PlayerLook |
| Player position update | dev.simplix.protocolize.data.packets.PlayerPosition |
| Player position and look update | dev.simplix.protocolize.data.packets.PlayerPositionLook |
| Player slot update | dev.simplix.protocolize.data.packets.SetSlot |
| Player item interaction | dev.simplix.protocolize.data.packets.UseItem |
| Inventory items | dev.simplix.protocolize.data.packets.WindowItems |
| Inventory properties | dev.simplix.protocolize.data.packets.WindowProperty |

You can easily add support for new packets and versions by installing modules.

## Get some help
For documentation take a look at the wiki pages.
If you have questions feel free to join our [discord server](https://discord.simplixsoft.com/).
