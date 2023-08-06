# Usage

Fabric Minecraft mod which allows the bundle to pickup mobs (like the nabsack from Hogwarts Legacy). To pick up a mob with the nabsack (bundle), right click on it while holidng the bunlde in your main hand. The bundle will be converted into a spawn egg, containing the mob you collected. This can be used like a regular spawn egg.

## Config

It is possible to configure which entities can be picked up with the nabsack by adding and removing entity IDs from the `entityIdWhitelist` in the `nabsack.json` file. This file is created when you first start the server. Updates to the file will take effect on the next server restart.

```json
{
  "entityIdWhitelist": [
    "minecraft:sheep"
  ]
}
```
