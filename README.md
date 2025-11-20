# Stormatica

An enhanced fork of [Litematica](https://github.com/maruohon/litematica) with powerful material list features for survival Minecraft.

## What is Stormatica?

Stormatica extends Litematica's material list functionality with two major features:

### 🗂️ Custom Material Lists
Load material lists from JSON or text files containing **any Minecraft items** - not just placeable blocks:
- Tools, weapons, and armor
- Food items and potions
- Mob drops and brewing ingredients
- Literally any item in the game!

Perfect for creating shopping lists, raid preparation checklists, or resource gathering goals.

### 🧠 Smart Cache-Based Sorting
Material lists automatically prioritize items you've recently accessed in containers:
- Open a chest → Items from that chest jump to the top of your list
- Open more containers → Most recently seen items stay at the top
- Your HUD always shows the items that are currently accessible to you
- No more running across your world looking for scattered materials!

## Installation

### Requirements
- Minecraft 1.21.10 (Fabric)
- [Fabric Loader](https://fabricmc.net/) 0.17.3+
- [Malilib](https://www.curseforge.com/minecraft/mc-mods/malilib) 0.26.4+

### Steps
1. Download the latest release from [Releases](https://github.com/cubicmetre/stormatica-extension/releases)
2. Place `stormatica-fabric-0.24.5-stormatica-1.0.0.jar` in your `.minecraft/mods/` folder
3. Remove the original Litematica mod (Stormatica replaces it completely)
4. Launch Minecraft

**Note:** Stormatica is a **fork**, not an addon. Do not run it alongside the original Litematica.

## Usage

### Creating Custom Material Lists

#### JSON Format
```json
{
  "name": "Raid Preparation",
  "items": [
    {"id": "minecraft:diamond_sword", "count": 1},
    {"id": "minecraft:diamond_chestplate", "count": 1},
    {"id": "minecraft:golden_apple", "count": 10},
    {"id": "minecraft:ender_pearl", "count": 16},
    {"id": "minecraft:shield", "count": 1}
  ]
}
```

#### Text Format
```
minecraft:diamond_sword 1
minecraft:diamond_chestplate 1
minecraft:golden_apple 10
minecraft:ender_pearl 16
minecraft:shield 1
```

Save files in your schematics folder, then load via "Load Schematics" menu.

### Using Cache-Based Sorting

**Cache sorting is enabled by default!**

1. Load any material list
2. Start opening containers (chests, barrels, shulker boxes, etc.)
3. Items you just saw automatically move to the top
4. Your HUD now shows the most accessible items first

**To change sort order:** Click the **Item** column header to cycle through NAME → CACHE_ORDER → CACHE_ORDER reversed → NAME reversed

## Credits

### Original Litematica
- **Author:** [masa](https://github.com/maruohon)
- **License:** LGPLv3
- **Repository:** https://github.com/maruohon/litematica

### Stormatica Extensions
- **Author:** cubicmetre
- **Features Added:** Custom material lists, cache-based sorting
- **License:** LGPLv3 (same as original)

## Compiling

```bash
git clone https://github.com/cubicmetre/stormatica-extension.git
cd stormatica-extension
./gradlew build
```

The built jar file will be in `build/libs/`

## License

This project is licensed under the GNU Lesser General Public License v3.0 (LGPLv3), the same license as the original Litematica.

## Support

- **Issues:** [GitHub Issues](https://github.com/cubicmetre/stormatica-extension/issues)
- **Original Litematica:** [masa's repository](https://github.com/maruohon/litematica)

This is an **unofficial fork** - not supported by the original Litematica team.
