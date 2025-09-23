# What is this?
I'm not really sure, I found the [ISS Piss Tracker](https://bsky.app/profile/iss-piss-tracker.bsky.social) on Bluesky and decided that it needed a minecraft version.
This 1.21.8 Fabric mod renders a icon, you can modify where it is on screen

# Recommended mods
[ModMenu](https://modrinth.com/mod/modmenu) (allows editing the config in-game)
[Animatica](https://modrinth.com/mod/animatica) (Allows custom animated textures, the mod comes bundled with some extra textures)

# How to Build
Make sure you have JDK 21 installed, I recommend [Adoptium](https://adoptium.net/temurin/releases?version=21&os=any&arch=any)
```
git clone https://github.com/Chilllyy/ISSOSDFabric
(on linux) chmod +x gradlew
(windows command prompt) gradlew build
(linux & windows powershell) ./gradlew build
```[anim](src/main/resources/assets/minecraft/optifine/anim)