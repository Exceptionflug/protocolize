
<div align=center>
    <img src="./protocolize-logo.webp" width="512">
    <br/><br/>
    <p>Next generation protocol manipulation framework for proxy servers.</p>
    <a href="http://ci.exceptionflug.de/job/Protocolize2/"><img src="http://ci.exceptionflug.de/buildStatus/icon?job=Protocolize2"></a>
    <a href="https://github.com/Exceptionflug/Protocolize/releases"><img src="https://img.shields.io/github/downloads/Exceptionflug/Protocolize/total?color=GREEN&label=Downloads"></a>
    <a href="https://discord.gg/ZsYjwcNRbV"><img src="https://img.shields.io/discord/752533664696369204?label=Discord"></a>
    <img src="https://img.shields.io/github/license/Exceptionflug/Protocolize"></a>
    <a href="https://twitter.com/Exceptionflug"><img src="https://img.shields.io/twitter/follow/Exceptionflug?label=Twitter&style=social"></a>
</div>

# Protocolize v2
This is the official repository for the next generation proxy server protocol manipulation framework. If you wish to see the repository for Protocolize v1, click [here](https://github.com/Exceptionflug/protocolize/tree/v1).

## Getting started
### Installation
In order to use Protocolize, you have to install it onto your proxy server. Protocolize is supporting BungeeCord and Velocity proxy servers.
Download the latest protocolize version for your proxy software right here: https://exceptionflug.de/protocolize.
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
    <version>2.3.3</version>
    <scope>provided</scope>
</dependency>
```
**Or alternatively, with Gradle:**
```kotlin
repsitories {
    maven {
        url = uri('https://mvn.exceptionflug.de/repository/exceptionflug-public/')
    }
}

dependencies {
    compileOnly("dev.simplix:protocolize-api:2.3.3")
}
```

## Compatibility

Protocolize is shipped with it's default data module which adds support for the following versions:
| Minecraft Version | Supported |
|--|--|
| 1.8.x - 1.12.2 | ❌ (Only with [additional legacy module](https://ci.exceptionflug.de/job/Protocolize-Legacy-Data/)) |
| 1.13 - 1.13.2 | ✔️ (Sounds only with [additional legacy module](https://ci.exceptionflug.de/job/Protocolize-Legacy-Data/)) |
| 1.14 - 1.20.4 | ✔️ |

Implemented packets by default:

| Packet | Class |
|--|--|
| Player block placement | dev.simplix.protocolize.data.packets.BlockPlacement |
| Inventory click | dev.simplix.protocolize.data.packets.ClickWindow |
| Close inventory | dev.simplix.protocolize.data.packets.CloseWindow |
| Confirm transaction | dev.simplix.protocolize.data.packets.ConfirmTransaction |
| Held item change | dev.simplix.protocolize.data.packets.HeldItemChange |
| Named sound effect | dev.simplix.protocolize.data.packets.NamedSoundEffect |
| Sound effect | dev.simplix.protocolize.data.packets.SoundEffect |
| Open inventory | dev.simplix.protocolize.data.packets.OpenWindow |
| Player look update | dev.simplix.protocolize.data.packets.PlayerLook |
| Player position update | dev.simplix.protocolize.data.packets.PlayerPosition |
| Player position and look update | dev.simplix.protocolize.data.packets.PlayerPositionLook |
| Player slot update | dev.simplix.protocolize.data.packets.SetSlot |
| Player item interaction | dev.simplix.protocolize.data.packets.UseItem |
| Inventory items | dev.simplix.protocolize.data.packets.WindowItems |
| Inventory properties | dev.simplix.protocolize.data.packets.WindowProperty |

You can easily add support for new packets and versions by installing modules.


## Known issues
- Protocolize is unstable when being used with **proxy installations** of Geyser and/or ViaVersion.

## Get some help
For documentation take a look at the wiki pages.
If you have questions feel free to join our [discord server](https://discord.gg/ZsYjwcNRbV).
